package ca.ualberta.cs.phebert.litx;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import java.util.ArrayList;

import ca.ualberta.cs.phebert.litx.annotations.*;

public class Request {
    private static final String CHANNEL_ID = "ca.ualberta.cs.phebert.litx.notifs";
    private User requestor;
    private User bookOwner;
    private Book book;
    private Status status;

    enum Status {
        Pending,
        Accepted,
        Resolved,
        Refused
    }

    /**
     * Look requests up on firebase, and get all pertaining
     * If any requests are not on disk, Create a notification.
     * This should be called by a daemon/service.
     * @return all the requests
     * @param ctx
     */
    @OwnerCalled
    public static ArrayList<Request> scan(Context ctx) {
        return null;
    }

    /**
     * Push the given requests to firebase.
     * If any are resolved or refused, delete them from firebase.
     * @param requests ArrayList of all requests for a book
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
     * @param ctx
     */
    private void generateNotification(Context ctx) {
        // TODO

        // Create an explicit intent for an Activity in your app
        // Not sure what activity should be started when the notification is clicked. Change 'User.class'
        Intent intent = new Intent(this, User.class);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        String textTitle = "Request";
        String textContent = requestor.getUserName() + " wants to borrow book";

        NotificationCompat.Builder builder = new NotificationCompat.Builder(ctx, CHANNEL_ID)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle(textTitle)
                .setContentText(textContent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        // shows the Notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // notificationId is a unique int for each notification that you must define
        // right now its a magic number
        int notificationId = 123;
        notificationManager.notify(notificationId, builder.build());
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
     * @param requestor The requestor of the book
     */
    @BorrowerCalled
    public Request(Book book, User owner, User requestor) {
        this.book = book;
        this.bookOwner = owner;
        this.requestor = requestor;
        status = Status.Pending;
    }

    /**
     * This constructor is used by scan
     * @param book The book that is being requested
     * @param owner The owner of the book
     * @param requstor The requestor of the book
     * @param state Current status of the book
     */
    @OwnerCalled
    private Request(Book book, User owner, User Requestor, Status state) {
        this(book,owner,Requestor);
        status = state;
    }

    /**
     * returns the book owner
     *@return
     /*
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
    public User getRequestor() {
        return requestor;
    }
    
    /**
     * Returns the status of the book one of pending, accepted, resolved or refused
     * @return
     */
    public Status getStatus() {
        return status;
    }

    /**
     * First sets the books Request to this, if not set,
     * then sets status to accepted.
     */
    @OwnerCalled
    public void accept() {
        status = Status.Accepted;
    }

    /**
     * First sets the books Request to null, if not set,
     * then sets status to resolved.
     */
    @OwnerCalled
    public void resolve() {
        status = Status.Resolved;
    }

    /**
     * sets status to Refused.
     */
    @OwnerCalled
    public void delete() {
        status = Status.Refused;
    }
}
