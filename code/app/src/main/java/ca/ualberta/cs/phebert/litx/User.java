package ca.ualberta.cs.phebert.litx;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.HashMap;

import ca.ualberta.cs.phebert.litx.annotations.BorrowerCalled;
import ca.ualberta.cs.phebert.litx.annotations.OwnerCalled;

public class User {
    final static String USER_COLLECTION = "Users";
    private String userName;
    private String email;
    private String phoneNumber;
    private ArrayList<UserObserver> observers;
    //private int phoneNumber;
    private ArrayList<Request> acceptedRequests;
    private ArrayList<Request> myRequests;
    private ArrayList<Book> borrowedBooks;
    private ArrayList<Book> myBooks;
    private Coordinate myLocation;
    private FirebaseUser certificate;
    private boolean syncScheduled;


    /*
     * Check if username is unique
     * Used for creation of new user
     */
    public User(@NonNull String username, @NonNull String email, @NonNull String phone) {
        observers = new ArrayList<>();
        certificate = null;
        editProfile(username, email, phone);
    }

    public User() {
        observers = new ArrayList<>();
        userName = "";
        email = "";
        phoneNumber = "";
        syncScheduled = false;
    }

    /**
     * called upon authentication. this is
     * @param fbUser
     */
    public User(@NonNull FirebaseUser fbUser) {
        observers = new ArrayList<>();
        // use lazy instantiation.
        FirebaseFirestore.getInstance()
                .collection(USER_COLLECTION)
                .document(fbUser.getUid())
                .get()
                .addOnSuccessListener(snapshot -> {
                    userName = snapshot.getString("userName");
                    email = snapshot.getString("email");
                    phoneNumber = snapshot.getString("phoneNumber");
                });
        userName = null;
        email = fbUser.getEmail();
        phoneNumber = null;
        certificate = fbUser;
        FirebaseFirestore.getInstance()
                .collection(USER_COLLECTION)
                .document(fbUser.getUid())
                .addSnapshotListener((documentSnapshot, e) -> {
                    if(e == null && documentSnapshot != null) {
                        userName = documentSnapshot.getString("userName");
                        email = documentSnapshot.getString("email");
                        phoneNumber = documentSnapshot.getString("phoneNumber");
                        for(UserObserver observer:observers) {
                            observer.onUserUpdated(this);
                        }
                    }
                });
    }

    public void scheduleSync() {
        syncScheduled = true;
    }

    void addObserver(UserObserver observer) {
        observers.add(observer);
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

    /*
     * Method to search for other users
     */
    public static User findByUid (String Uid) {
        return null;
    }

    /*
     * Check if username is unique
     */
    public void setUserName(String username) {
        if(certificate != null) {
            sync();
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
            sync();
            // TODO sync with firebase/store
        }
    }

    public String getEmail() {
        return email;
    }

    public void setPhoneNumber (String newPhoneNumber) {
        // TODO validate phone Number
        phoneNumber = newPhoneNumber;
        if(certificate != null) {
            sync();
        }
    }

    public String getPhoneNumber () {
        if(phoneNumber == null && certificate != null) {
            // TODO getPhoneNumber from FireStore
        }
        return phoneNumber;
    }

    public ArrayList<Request> viewRequests () {
        return myRequests;
    }

    public void removeRequest (Request request) {

    }

    /*
     * Notifies user of acceptance on their request
     * Adds this book to their list of acceptedRequests
     */
    public void successfulRequest (Request request) {

    }

    public ArrayList<Request> getAcceptedRequests () {
        return acceptedRequests;
    }

    public long scanIsbn () {
        return 1; //TODO: Use Scanner external implementation
    }

    /*
     * Gets profile information
     * in easily read manor
     */
    public void getProfile () {

    }

    public void editProfile(String username, @NonNull String email, String phone) {
        setUserName(username);
        setEmail(email);
        setPhoneNumber(phone);
    }

    public ArrayList<Book> getBorrowedBooks() {
        return borrowedBooks;
    }

    public void setMyLocation (double x, double y) {
        myLocation = new Coordinate(x, y);
    }

    public Coordinate getMyLocation (){
        return myLocation;
    }

    /*public void setAuth (FirebaseUser newFbUser) {
        certificate = newFbUser;
    }*/

    public FirebaseUser getAuth () {
        return certificate;
    }

    public String getUserid () {
        return certificate.getUid();
    }

    //********************************Owner******************************

    /**
     * Used for testing
     * @return mybooks
     */
    public ArrayList<Book> getMyBooks() {
        return myBooks;
    }

    public void addBook(String author, String title, int ISBN) {

    }

    /*
     * Should delete the book and then remove it form myBooks
     */
    public void deleteBook(Book book) {

    }



}
