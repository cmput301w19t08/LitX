package ca.ualberta.cs.phebert.litx;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import ca.ualberta.cs.phebert.litx.annotations.BorrowerCalled;

/**
 * Represents the various users using the app.
 *
 * Kind of a god class, this handles both authenticating this user and
 * getting other users to communicate with them.
 * @see ProfileActivity
 */
@SuppressWarnings("WeakerAccess")
public class User implements Serializable {
    /**
     * this classe's specific log tag.
     */
    private final static String TAG = "LitX.User";
    /**
     * the collection's name on {@link FirebaseFirestore}.
     */
    final static String USER_COLLECTION = "Users";
    /**
     * A database containing all the users keyed by their {@link #uid}.
     * this is the result of {@link #loadDb()}.
     * @see #getAll()
     */
    private static Map<String,User> db;
    /**
     * Used to prevent the dual creation of tasks,
     * and to prevent the GC from collecting a running task.
     * @see #loadDb()
     * @see #getAll()
     */
    private static Task<QuerySnapshot> task;
    /**
     * Used in {@link #getAll()} to prevent data races.
     */
    private static boolean ready = false;
    /**
     * The current user using this Application
     */
    private static User current;
    /**
     * Set by {@link FirebaseAuth}, is a unique id used to identify users
     * @see #getUserid()
     */
    private String uid;
    /**
     * the user name of this user, used to identify him.
     * it should be unique, but that is not limited yet
     * @see #setUserName(String)
     * @see #getUserName()
     */
    private String userName;
    /**
     * the email address of this user, it is also used to authenticate the user,
     * so it is harder to change and is used to sign in.
     * @see #setEmail(String)
     * @see #getEmail()
     */
    private String email;
    /**
     * The phone number of this user, used to contact him.
     * Could be blank.
     * @see #setPhoneNumber(String)
     * @see #getPhoneNumber()
     */
    private String phoneNumber;
    /**
     * A list of observers to notify when a user updates
     * @see #addObserver(UserObserver)
     */
    private ArrayList<UserObserver> observers;
    /**
     * A list of requests accepted by the user
     * FIXME unused, and for some odd reason, cannot be expanded
     * @see #getAcceptedRequests()
     */
    private ArrayList<Request> acceptedRequests;
    /**
     * A list of requests sent by this user.
     * @see #addRequest(Request)
     * @see #getRequests()
     * @see #removeRequest(Request)
     */
    private ArrayList<Request> myRequests;
    /**
     * A list of the books this user registered.
     * @see #addBook(Book)
     * @see #getMyBooks()
     */
    private ArrayList<Book> myBooks;
    /**
     *
     */
    private FirebaseUser certificate;
    private boolean syncScheduled;


    /*
     * Check if username is unique
     * Used for creation of new user
     */
    public User(String username, String email, String phone) {
        observers = new ArrayList<>();
        acceptedRequests = new ArrayList<>();
        myRequests = new ArrayList<>();
        myBooks = new ArrayList<>();
        certificate = null;
        editProfile(username, email, phone);
    }

    public User() {
        observers = new ArrayList<>();
        acceptedRequests = new ArrayList<>();
        myRequests = new ArrayList<>();
        myBooks = new ArrayList<>();
        userName = "";
        email = "";
        phoneNumber = "";
        syncScheduled = false;
    }

    ///////////////////////////////////  Database stuff ////////////////////////////////////////////

    static private void loadDb() {
        if(task == null && isSignedIn()) {

            task = FirebaseFirestore.getInstance()
                    .collection(USER_COLLECTION)
                    .get();
            FirebaseFirestore.getInstance()
                    .collection(USER_COLLECTION)
                    .addSnapshotListener((result, e) -> {
                        if(e != null) Log.e(TAG,"firebase error", e);
                        if (result == null) return;
                        for (DocumentSnapshot doc: result.getDocuments()) {
                            User oldVal = findByUid(doc.getId());
                            User newVal = fromSnapshot(doc);
                            if(oldVal == null || newVal == null) continue;
                            if(oldVal.equals(newVal)) continue;
                            // not using setters because they write to firebase, infinite loop
                            oldVal.email = newVal.email;
                            oldVal.userName = newVal.userName;
                            oldVal.phoneNumber = newVal.phoneNumber;
                            for(UserObserver observer:oldVal.observers) {
                                observer.onUserUpdated(oldVal);
                            }
                        }
                    });
        }
    }

    static public User fromSnapshot(DocumentSnapshot doc) {
        User ans = new User();
        ans.userName = doc.getString("userName");
        ans.email = doc.getString("email");
        ans.phoneNumber = doc.getString("phoneNumber");
        ans.uid = doc.getId();
        return ans;
    }

    static public Map<String, User> getAll() {
        loadDb();
        if(!isSignedIn()) {
            Log.d(TAG,"User is not logged in");
            return null;
        }
        while(!task.isComplete()) Thread.yield();
        if(!task.isSuccessful()) {
            Log.e(TAG, "Task was not successful", task.getException());
            return null;
        }

        if(db == null) {
            db = new HashMap<>();
            for(DocumentSnapshot snapshot : task.getResult().getDocuments()) {
                Log.v(TAG,snapshot.getId());
                db.put(snapshot.getId(), fromSnapshot(snapshot));
            }
            ready = true;
        }
        while(!ready) Thread.yield();

        return db;
    }

    public static User findByUid(String uid) {
        User ans = getAll().get(uid);
        return ans;
    }

    public static User currentUser() {
        if(current == null) {
            if(isSignedIn()) {
                current = findByUid(FirebaseAuth.getInstance().getUid());
                current.certificate = FirebaseAuth.getInstance().getCurrentUser();
            } else return null;
        }
        return current;
    }

    void addObserver(UserObserver observer) {
        observers.add(observer);
    }

    public void scheduleSync() {
        syncScheduled = true;
    }

    public void sync() {
        if(syncScheduled) {
            HashMap<String, Object> user = new HashMap<>();
            user.put("userName", userName);
            user.put("email", email);
            user.put("phoneNumber", phoneNumber);
            FirebaseFirestore.getInstance()
                    .collection(USER_COLLECTION)
                    .document(certificate.getUid())
                    .set(user)
                    .addOnSuccessListener(ign -> {

                    })
                    .addOnFailureListener(e -> Log.wtf("LitX.User", e));

        }
        syncScheduled = false;
    }

    ////////////////////////////////////// Auth stuff //////////////////////////////////////////////

    /**
     * // this whole block is based on
     * <a href="https://firebase.google.com/docs/auth/android/email-link-auth">
     *     Authenticate with Firebase Using Email Link in Android
 *     </a>
     *
     * @param userName the provided user name
     * @param email the provided email
     * @param phoneNumber the provided phone number
     * @param act the Activity which should finish, or null
     */
    public static void logIn(final String userName,
                             final String email,
                             final String phoneNumber,
                             @Nullable final Activity act) {
        FirebaseAuth.getInstance()
                .signInWithEmailAndPassword(email,"a23b45").addOnSuccessListener(res -> {
            Log.v(TAG,"signed in");
            if(act != null) act.finish();
        }).addOnFailureListener(e -> {
            e.printStackTrace();
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,"a23b45")
                    .addOnSuccessListener(res ->{
                        HashMap<String, Object> user = new HashMap<>();
                        user.put("userName", userName);
                        user.put("email",  email);
                        user.put("phoneNumber", phoneNumber);
                        FirebaseFirestore.getInstance()
                                .collection(User.USER_COLLECTION)
                                .document(res.getUser().getUid())
                                .set(user)
                                .addOnSuccessListener(ign -> {

                                })
                                .addOnFailureListener(e2 -> Log.wtf("LitX.User", e));
                        Log.d(TAG,"signed up");
                        if(act != null) act.finish();
                    }).addOnFailureListener(e2 -> Log.e(TAG, "could not sign up", e2));
        });
    }

    public static boolean isSignedIn() {
        return FirebaseAuth.getInstance().getCurrentUser() != null;
    }

    /**
     * Gets this users {@link #uid}, which is the user's actual user ID assigned by firebase.
     * @return the user's {@link #uid}
     */
    public String getUserid () {
        return uid;
    }

    ////////////////////////////////// setters and getters /////////////////////////////////////////

    /*
     * Check if username is unique
     */
    public void setUserName(String username) {
        if(certificate != null) {
            scheduleSync();
        }
        this.userName = username;
    }

    public String getUserName() {
        return userName;
    }

    public void setEmail(@NonNull String newEmail) {
        email = newEmail;
        if(certificate != null) {
            certificate.updateEmail(newEmail);
            scheduleSync();
        }
    }

    public String getEmail() {
        return email;
    }

    /**
     *
     * @param newPhoneNumber the new value to set {@link #phoneNumber to}
     */
    public void setPhoneNumber (String newPhoneNumber) {
        // TODO validate phone Number
        phoneNumber = newPhoneNumber;
        if(certificate != null) {
            scheduleSync();
        }
    }

    /**
     * gets the {@link #phoneNumber} from this user.
     * @return {@link #phoneNumber}
     */
    public String getPhoneNumber () {
        return phoneNumber;
    }

    /**
     * set the three main fields of the user. uses the setters, so a sync must be called for it
     * to be reflected on {@link FirebaseFirestore}.
     * @param username the {@link #userName} of the user .
     * @param email the {@link #email} of the user.
     * @param phone the {@link #phoneNumber} of the user.
     */
    public void editProfile(String username, @NonNull String email, String phone) {
        setUserName(username);
        setEmail(email);
        setPhoneNumber(phone);
    }

    //////////////////////////////////////// requests //////////////////////////////////////////////

    public ArrayList<Request> getRequests() {
        return myRequests;
    }

    /**
     * adds the passed request into the user's list of personal requests
     *
     * @param request
     */
    @BorrowerCalled
    public void addRequest(Request request) {
        myRequests.add(request);
    }

    /**
     * @return the ArrayList of accepted requests of the user.
     */
    public ArrayList<Request> getAcceptedRequests() {
        return acceptedRequests;
    }


    /**
     * removes a request from the user's list of requests.
     * @param request the request to remove
     * @see Request#delete()
     */
    public void removeRequest (Request request) {
        if(myRequests.contains(request)) {
            myRequests.remove(request);
        }
    }

    /////////////////////////////////////// books //////////////////////////////////////////////////

    /**
     * Used for testing
     * @return mybooks
     */
    public ArrayList<Book> getMyBooks() {
        return myBooks;
    }

    /**
     * Adds a books to this user's {@link #myBooks}
     * @param book
     */
    public void addBook(Book book) {
        Log.d("MyBooks", "Book Added to myBooks" + book.getTitle());
        myBooks.add(book);

    }

    /**
     * Should delete the book and then remove it form {@link #myBooks}
     * @param book the book to delete
     */
    public void deleteBook(Book book) {
        if(myBooks.contains(book)) {
            book.delete(book);
            myBooks.remove(book);
        }
    }

    /**
     * Verifies if one user is the same as the other.
     * Used to tell if a user was updated
     * @param obj If
     * @return true if and only if the users have the same email, user, and phone number
     */
    @Override
    public boolean equals(@Nullable Object obj) {
        if(obj instanceof User) {
            User u = (User) obj;
            return email.equals(u.email) &&
                    userName.equals(u.userName) &&
                    phoneNumber.equals(u.phoneNumber);
        } else return false;
    }

    /**
     * makes a hashcode for this user, only implemented because equals was implemented.
     * it is guaranteed that if(a.equals(b)) a.hashCode() == b.hashCode()
     */
    @Override
    public int hashCode() {
        return (email.hashCode() % (Integer.MAX_VALUE / 3)) +
                (userName.hashCode() % (Integer.MAX_VALUE / 3)) +
                (phoneNumber.hashCode() % (Integer.MAX_VALUE / 3));
    }
}
