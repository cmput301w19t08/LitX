package ca.ualberta.cs.phebert.litx;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

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
     */
    @OwnerCalled
    public static ArrayList<Request> scan(Context ctx) {
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
        new NotificationCompat.Builder(ctx, CHANNEL_ID);
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
    public Request(Book book, User owner, User requestor) {
        this.book = book;
        this.bookOwner = owner;
        this.requestor = requestor;
        status = Status.Pending;
    }

    /**
     * This constructor is used by scan
     */
    @OwnerCalled
    private Request(Book book, User owner, User Requestor, Status state) {
        this(book,owner,Requestor);
        status = state;
    }

    public User getBookOwner() {
        return bookOwner;
    }

    public Book getBook(){
        return book;
    }

    public User getRequestor() {
        return requestor;
    }

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
