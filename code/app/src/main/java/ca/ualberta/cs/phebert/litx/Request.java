package ca.ualberta.cs.phebert.litx;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
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
import java.util.Random;

import ca.ualberta.cs.phebert.litx.annotations.*;

import static com.loopj.android.http.AsyncHttpClient.log;

/**
 * Request object for a book, contains the book, book owner and user that is requesting the book
 * @author phebert
 * @version 1.0
 * @see BookStatusActivity, BookViewActivity, ExchangeActivity
 */
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


    private Boolean requestSeen;

    private RequestStatus status;
    private String docId;

    /**
     * Loads the collection from firestore
     */
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

    /**
     * Getter for docId
     * @return document ID of the request
     */
    public String getDocId(){return this.docId;}

    /**
     * Converts from the database into an object
     * @param snapshot snapshot from database
     * @return request object
     */
    private static Request fromSnapshot(DocumentSnapshot snapshot) {
        Request ans = new Request(
                // need to somehow access Book.
                Book.findByDocId(snapshot.getString("book")), // need book.byDocId;
                User.findByUid(snapshot.getString("owner")),
                User.findByUid(snapshot.getString("requester")),
                snapshot.getString("status")
        );
        try {
            ans.requestSeen = snapshot.getBoolean("seen");
        } catch (RuntimeException e) {}
        ans.docId = snapshot.getId();
        log.d("Litx", ans.docId);
        ans.book.addRequest(ans);
        ans.requester.addRequest(ans);

        Log.i("THIS IS BEFORE", snapshot.getString("status")+"AND ITS ID IS "+snapshot.getString("book"));

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

        while(!task.isComplete()) {
            if(task.isCanceled()) return;
        }
        if(task.isSuccessful()) {
            Request request;
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


    /**
     * Push itself to the database
     */
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
    public void generateNotification(Context ctx) {
        Random rand = new Random();
        // 10000 is just a magic number for unique number for notifications
        int randInt = rand.nextInt(10000);
        String textTitle;
        String bookTitle;
        String textContent;
        PendingIntent pendingIntent;
        if (!status.equals(RequestStatus.Accepted)) {
            Intent intentForOwner = new Intent(ctx.getApplicationContext(), BookViewActivity.class);
            intentForOwner.putExtra("Book", book.getDocID());
            intentForOwner.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            pendingIntent = PendingIntent.getActivity(ctx, randInt, intentForOwner, 0);
            textTitle = "Request";
            bookTitle = book.getTitle();
            textContent = requester.getUserName() + " wants to borrow " + bookTitle;

        } else {
            Intent intentForOwner = new Intent(ctx.getApplicationContext(), BookStatusActivity.class);
            intentForOwner.putExtra("Book", book.getDocID());
            intentForOwner.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            pendingIntent = PendingIntent.getActivity(ctx, randInt, intentForOwner, 0);

            textTitle = "Accepted!";
            bookTitle = book.getTitle();

            textContent = bookOwner.getUserName() + " accepted your request to borrow " + bookTitle;

        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(ctx, CHANNEL_ID)
                .setSmallIcon(R.drawable.book_icon2)
                .setContentTitle(textTitle)
                .setContentText(textContent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(ctx);
        notificationManager.notify(randInt, builder.build());
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
        this.requestSeen = false;
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
     * @return owner of the book
     */
    public User getBookOwner() {
        return bookOwner;
    }
    /**
     * returns the requested book
     * @return book that has been requested
     */
    public Book getBook(){
        return book;
    }

    /**
     * returns the user that is requesting the book
     * @return user requesting that book
     */
    public User getRequester() {
        return requester;
    }

    /**
     * Returns the status of the book one of pending, accepted, resolved or refused
     * @return status of the book
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

    /**
     * Sets the status of the request
     * @param status new status to set the book to
     */
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
        push();
    }

    /**
     * sets status to Refused.
     */
    @OwnerCalled
    public void delete() {
        status = status.refuse(book, this);
        requester.removeRequest(this);
        book.getRequests().remove(this);

    }

    /**
     * returns true if notification has been generated or false if it has not
     * @return notification has been generated or no
     */
    public Boolean getRequestSeen() {
        return requestSeen;
    }

    /**
     * Sets the notification
     * @param requestSeen notification has been seen
     */
    public void setRequestSeen(Boolean requestSeen) {
        this.requestSeen = requestSeen;
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
        ans.put("status", status.toString());
        ans.put("seen", requestSeen);
        return ans;
    }
}
