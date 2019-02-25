package ca.ualberta.cs.phebert.litx;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class Request {
    private static final String CHANNEL_ID = "ca.ualberta.cs.phebert.litx.notifs";
    private User requestor;
    private Owner bookOwner;
    private Book book;
    private Map map; // Should be set to null by constructor

    /**
     * Look requests up on firebase, and get all pertaining
     * If any requests are not on disk, Create a notification.
     * This should be called by a daemon/
     * @return
     */
    public static ArrayList<Request> scan(Context ctx) {
        return null;
    }

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
        new NotificationCompat.Builder(ctx, CHANNEL_ID)
    }

    /**
     * should be called upon starting the application.
     *
     * Based on
     * <a https://developer.android.com/training/notify-user/build-notification#java>
     *     Create a Notification  |  Android Developers
     * </a>
     */
    private void createNotificationChennel(Context ctx) {
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

    // Should be called in the constructor
    public void notifyOwner(Owner owner) {

    }

    public void setBookOwner(Owner bookOwner) {

    }

    public Owner getBookOwner() {
        return bookOwner;
    }

    public void setBook(Book book) {

    }

    public Book getBook(){
        return book;
    }

    public void setRequestor(User requestor) {

    }

    public User getRequestor() {
        return requestor;
    }

    /*
     * This first calls succesfulRequest in User (our requestor) then acceptedRequest in Book
     */
    public void accepted() {

    }

    /*
     * This calls requestResolved in Book
     */
    public void resolved() {

    }

    /*
     * Calls removeRequest in User(requestor) then deletes this object
     */
    public void delete() {

    }

    /*
     * Adds a map which is assigned to map
     */
    public void addMap(Coordinate coordinate) {

    }

    public Map getMap() {
        return map;
    }
}
