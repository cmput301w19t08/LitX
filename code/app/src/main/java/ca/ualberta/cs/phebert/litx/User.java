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

@SuppressWarnings("WeakerAccess")
public class User implements Serializable {
    private final static String TAG = "LitX.User";
    final static String USER_COLLECTION = "Users";
    private static Map<String,User> db;
    private static Task<QuerySnapshot> task;
    private static boolean ready = false;
    private static User current;
    private String uid;
    private String userName;
    private String email;
    private String phoneNumber;
    private ArrayList<UserObserver> observers;
    private ArrayList<Request> acceptedRequests;
    private ArrayList<Request> myRequests;
    private ArrayList<Book> myBooks;
    private Coordinate myLocation;
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
        if(!isSignedIn()) return null;
        while(!task.isComplete()) Thread.yield();
        if(!task.isSuccessful()) return null;

        if(db == null) {
            db = new HashMap<>();
            for(DocumentSnapshot snapshot : task.getResult().getDocuments()) {
                Log.v(TAG,snapshot.getId());
                db.put(snapshot.getId(), fromSnapshot(snapshot));
            }
        }

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

    public void setPhoneNumber (String newPhoneNumber) {
        // TODO validate phone Number
        phoneNumber = newPhoneNumber;
        if(certificate != null) {
            scheduleSync();
        }
    }

    public String getPhoneNumber () {
        if(phoneNumber == null && certificate != null) {
            // TODO getPhoneNumber from FireStore
        }
        return phoneNumber;
    }

    public void editProfile(String username, @NonNull String email, String phone) {
        setUserName(username);
        setEmail(email);
        setPhoneNumber(phone);
    }

    //////////////////////////////////////// requests //////////////////////////////////////////////

    public ArrayList<Request> getRequests() {
        return myRequests;
    }

    @BorrowerCalled
    public void addRequest(Request request) {
        myRequests.add(request);
    }

    public ArrayList<Request> getAcceptedRequests() {
        return acceptedRequests;
    }

    public void acceptRequest(Request request) {
        myRequests.add(request);
    }

    public void removeRequest (Request request) {
        request.delete();
    }

    /////////////////////////////////////// books //////////////////////////////////////////////////

    /**
     * Used for testing
     * @return mybooks
     */
    public ArrayList<Book> getMyBooks() {
        return myBooks;
    }

    public void addBook(Book book) {
        myBooks.add(book);
    }

    /**
     * Should delete the book and then remove it form myBooks
     * @param book the book to delete
     */
    public void deleteBook(Book book) {
        if(myBooks.contains(book)) {
            book.delete();
        }
    }

    ///////////////////////////////////// Location /////////////////////////////////////////////////
    // TODO: put in main part of class when possible, or deprecate

    public void setMyLocation (double x, double y) {
        myLocation = new Coordinate(x, y);
    }

    public Coordinate getMyLocation (){
        return myLocation;
    }

    //////////////////////////////////////// misc //////////////////////////////////////////////////

    @Override
    public boolean equals(@Nullable Object obj) {
        if(obj instanceof User) {
            User u = (User) obj;
            return email.equals(u.email) &&
                    userName.equals(u.userName) &&
                    phoneNumber.equals(u.phoneNumber);
        } else return false;
    }

    // guarantee if(a.equals(b)) a.hashCode() == b.hashCode()
    @Override
    public int hashCode() {
        return (email.hashCode() % (Integer.MAX_VALUE / 3)) +
                (userName.hashCode() % (Integer.MAX_VALUE / 3)) +
                (phoneNumber.hashCode() % (Integer.MAX_VALUE / 3));
    }
}
