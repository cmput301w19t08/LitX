package ca.ualberta.cs.phebert.litx;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.function.Consumer;

import ca.ualberta.cs.phebert.litx.annotations.*;

public class Request {
    private static final String CHANNEL_ID = "ca.ualberta.cs.phebert.litx.notifs";
    private User requester;
    private User bookOwner;
    private Book book;
    private RequestStatus status;


    /**
     * Look requests up on firebase, and get all pertaining
     * If any requests are not on disk, Create a notification.
     * This should be called by a daemon/service.
     * @return all the requests
     */
    @OwnerCalled
    public static ArrayList<Request> scan(Context ctx) {
        FirebaseFirestore.getInstance().collection("Requests")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<DocumentSnapshot> docs = queryDocumentSnapshots.getDocuments();
                });
        return null;
    }

    /**
     * Push the given requests to firebase.
     * If any are resolved or refused, delete them from firebase.
     */
    @OwnerCalled
    public static void push(ArrayList<Request> requests) { }

    /**
     * create a notification
     * <p>
     *     Based on
     *     <a https://developer.android.com/training/notify-user/build-notification#java>
     *         Create a Notification  |  Android Developers
     *     </a>
     * </p>
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

    @BorrowerCalled
    public Request(Book book, User owner, User requester) {
        this.book = book;
        this.bookOwner = owner;
        this.requester = requester;
        status = RequestStatus.Pending;
    }

    /**
     * This constructor is used by scan
     */
    @OwnerCalled
    private Request(Book book, User owner, User requester, String state) {
        this(book,owner, requester);
        status = RequestStatus.get(state);
    }

    public User getBookOwner() {
        return bookOwner;
    }

    public Book getBook(){
        return book;
    }

    public User getRequester() {
        return requester;
    }

    public RequestStatus getStatus() {
        return status;
    }

    /**
     * First sets the books Request to this, if not set,
     * then sets status to accepted.
     */
    @OwnerCalled
    public void accept() {
        status = status.accept(book);
    }

    /**
     * First sets the books Request to null, if not set,
     * then sets status to resolved.
     */
    @OwnerCalled
    public void resolve() {
        status = status.resolve(book);
    }

    /**
     * sets status to Refused.
     */
    @OwnerCalled
    public void delete() {
        status = status.refuse(book);
    }
}
