package ca.ualberta.cs.phebert.litx;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.ualberta.cs.phebert.litx.annotations.*;

import static com.loopj.android.http.AsyncHttpClient.log;

public class Request {
    public static final String REQUESTS_COLLECTION = "Requests";
    private static Map<String, Request> db;
    /**
     * The task for fetching db from firestore.
     */
    private static Task<QuerySnapshot> task;
    private static final String CHANNEL_ID = "ca.ualberta.cs.phebert.litx.notifs";

    private User requester;
    private User bookOwner;
    private Book book;

    private RequestStatus status;
    private String docId;

    // Get requests
    private static void loadDb(){
        if(task == null && User.isSignedIn()) {
            task = FirebaseFirestore.getInstance().collection(REQUESTS_COLLECTION)
                    .get();
        }
    }

    /**
     * get all the current requests. use this over directly using the attribute,
     * as this will await {@link #task} is complete
     */
    public static Map<String,Request> getAll() {
        loadDb();
        if(!User.isSignedIn()) return null;
        while(!task.isComplete()) Thread.yield();
        if(!task.isSuccessful()) return null;

        if(db == null) {
            db = new HashMap<>();
            List<DocumentSnapshot> docs = task.getResult().getDocuments();
            for (DocumentSnapshot doc : docs) {
                Log.v("LitX.Request", doc.getId());
                db.put(doc.getId(), fromSnapshot(doc));
            }
        }

        return db;
    }

    public String getDocId(){return this.docId;}
    /**
     *
     * @param snapshot
     * @return
     */
    private static Request fromSnapshot(DocumentSnapshot snapshot) {
        Request ans = new Request(
                // need to somehow acces Book.
                Book.findByDocId(snapshot.getString("book")), // need book.byDocId;
                User.findByUid(snapshot.getString("owner")),
                User.findByUid(snapshot.getString("requester")),
                snapshot.getString("status")
        );
        ans.docId = snapshot.getId();
        log.d("Litx", ans.docId);
        ans.book.addRequest(ans);
        ans.requester.addRequest(ans);

        return ans;
    }

    /**
     * gets a request from its online document
     * @param docId the document's name in the database
     * @return the request associated with the document.
     */
    @Nullable
    public static Request findByDocId(String docId) {
        return getAll().get(docId);
    }

    /**
     * Look requests up on firebase, and get all pertaining
     * If any requests are not on disk, Create a notification.
     * This should be called by a daemon/service.
     * FIXME: may be replaced by firebase push notifications.
     * @return all the requests
     * @param ctx
     */
    @OwnerCalled
    public static void scan(Context ctx) {
        HashMap<String, Request> onlineRequests = new HashMap<>();
        HashMap<String, Request> offlineRequests = new HashMap<>();


        // TODO get stored Requests

        while(!task.isComplete()) {
            if(task.isCanceled()) return;
        }
        if(task.isSuccessful()) {
            Request request; // will be set in a foreach loop
            // TODO compare stored requests with online requests
            if(false) {
                request.generateNotification(ctx);
            }
        }
    }

    /**
     * Push the given requests to firebase.
     * If any are resolved or refused, delete them from firebase.
     */
    @OwnerCalled
    public static void push() {
        for(String id : db.keySet()) {
            Request request = db.get(id);
            if(request == null) continue;
            if(!request.status.deletable()) {
                if(request.docId == null) {
                    request.docId = FirebaseFirestore.getInstance()
                            .collection(REQUESTS_COLLECTION)
                            .document().getId();
                }
                FirebaseFirestore.getInstance()
                        .collection(REQUESTS_COLLECTION)
                        .document(request.docId)
                        .set(request.toMap());
            } else { // deletable
                if(request.docId == null) continue;
                FirebaseFirestore.getInstance()
                        .collection(REQUESTS_COLLECTION)
                        .document(request.docId)
                        .delete();
            }
        }
    }


    public void selfPush() {
        Log.v("LitX.Request","pushing");
        if (docId == null || docId.isEmpty()) {
            docId = FirebaseFirestore.getInstance()
                    .collection(REQUESTS_COLLECTION)
                    .document().getId();

        }
        FirebaseFirestore.getInstance()
                .collection(REQUESTS_COLLECTION)
                .document(docId)
                .set(toMap());
    }

    /**
     * create a notification
     * <p>
     *     Based on
     *     <a https://developer.android.com/training/notify-user/build-notification#java>
     *         Create a Notification  |  Android Developers
     *     </a>
     * </p>
     * @param ctx
     */
    private void generateNotification(Context ctx) {
        // TODO

        // Create an explicit intent for an Activity in your app
        // Not sure what activity should be started when the notification is clicked. Change 'User.class'
        // Error Comment out Intent was wrong
//        Intent intent = new Intent(this, User.class);
        // Error comment out
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        String textTitle = "Request";
        String textContent = requester.getUserName() + " wants to borrow book";

        NotificationCompat.Builder builder = new NotificationCompat.Builder(ctx, CHANNEL_ID)
                // Error
//                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle(textTitle)
                .setContentText(textContent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                //error
//                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        //error
        // shows the Notification
//        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // notificationId is a unique int for each notification that you must define
        // right now its a magic number
        int notificationId = 123;
//        notificationManager.notify(notificationId, builder.build());
    }

    /**
     * should be called upon starting the application.
     *
     * Based on
     * <a https://developer.android.com/training/notify-user/build-notification#java>
     *     Create a Notification  |  Android Developers
     * </a>
     * @param ctx
     */
    @OwnerCalled
    public static void createNotificationChannel(Context ctx) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = ctx.getString(R.string.channel_name);
            String description = ctx.getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = ctx.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
    /**
     * Constructor for request
     * @param book The book that is being requested
     * @param owner The book owner
     * @param requester The requestor of the book
     */
    @BorrowerCalled
    public Request(Book book, User owner, User requester) {
        this.book = book;
        this.bookOwner = owner;
        this.requester = requester;
        status = RequestStatus.Pending;
    }

    /**
     * This constructor is used by firebase.
     * @param book The book that is being requested
     * @param owner The owner of the book
     * @param requester The requestor of the book
     * @param state Current status of the book
     */
    @OwnerCalled
    private Request(Book book, User owner, User requester, String state) {
        this(book,owner, requester);
        status = RequestStatus.valueOf(state);
    }

    /**
     * returns the book owner
     * @return
     */
    public User getBookOwner() {
        return bookOwner;
    }
    /**
     * returns the requested book
     * @return
     */
    public Book getBook(){
        return book;
    }

    /**
     * returns the user that is requesting the book
     * @return
     */
    public User getRequester() {
        return requester;
    }

    /**
     * Returns the status of the book one of pending, accepted, resolved or refused
     * @return
     */
    public RequestStatus getStatus() {
        return status;
    }

    /**
     * First sets the books Request to this, if not set,
     * then sets status to accepted.
     */
    @OwnerCalled
    public void accept() {
        status = status.accept(book, this);
        selfPush();
    }

    public void setStatus(RequestStatus status){
        this.status = status;
    }

    /**
     * First sets the books Request to null, if not set,
     * then sets status to resolved.
     */
    @OwnerCalled
    public void resolve() {
        status = status.resolve(book, this);
    }

    /**
     * sets status to Refused.
     */
    @OwnerCalled
    public void delete() {
        status = status.refuse(book, this);
        requester.getRequests().remove(this);
        book.getRequests().remove(this);

    }

    /**
     * turns this Request into a Map, so that it can be pushed.
     * @return a map version of this request.
     */
    @OwnerCalled
    public Map<String,Object> toMap() {
        Map<String,Object> ans = new HashMap<>();
        ans.put("requester",requester.getUserid());
        ans.put("owner", bookOwner.getUserid());
        ans.put("book", book.getDocID());
        ans.put("status", status.name());
        return ans;
    }
}
