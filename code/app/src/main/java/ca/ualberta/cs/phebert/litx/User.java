package ca.ualberta.cs.phebert.litx;

import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

import ca.ualberta.cs.phebert.litx.annotations.BorrowerCalled;
import ca.ualberta.cs.phebert.litx.annotations.OwnerCalled;

public class User {
    private String userName;
    private String email;
    private String phoneNumber;
    private ArrayList<Request> acceptedRequests;
    private ArrayList<Request> myRequests;
    private ArrayList<Book> borrowedBooks;
    private ArrayList<Book> myBooks;
    private Coordinate myLocation;
    private FirebaseUser certificate;

    /**
     * Check if username is unique
     * Used for creation of new user
     */
    public User(@NonNull String username, @NonNull String email, @NonNull String phone) {
        certificate = null;
        editProfile(username, email, phone);
    }

    public User() {
        userName = "";
        email = "";
        phoneNumber = "";
    }

    /**
     * called upon authentication. this is
     * @param fbUser
     */
    public User(FirebaseUser fbUser) {
        // use lazy instantiation.
        userName = null;
        email = fbUser.getEmail();
        phoneNumber = null;
        certificate = fbUser;
    }

    /**
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
            // TODO sync with firebase/store.
            // if the new username is not unique, do not change it
        }
        this.userName = username;
    }

    public String getUserName() {
        // no need to to sync, should be automaticly set when loading the user
        if(userName == null && certificate != null) {
            // TODO get username from firestore
        }
        return userName;
    }

    public void setEmail(@NonNull String newEmail) {
        // TODO Validate email
        email = newEmail;
        if(certificate != null) {
            certificate.updateEmail(newEmail);
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
            // TODO sync with Firebase/store
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

    @BorrowerCalled
    public void removeRequest (Request request) {

    }

    /*
     * Notifies user of acceptance on their request
     * Adds this book to their list of acceptedRequests
     * Maybe call this from Request
     */
    @BorrowerCalled
    public void successfulRequest (Request request) { // TODO rename (name is not a verb)

    }

    @BorrowerCalled
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

    /**
     * @deprecated Use ProfileActivity instead.
     */
    @Deprecated
    public void getProfile () { }

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


    /**
     * Used for testing
     * @return mybooks
     */
    @OwnerCalled
    public ArrayList<Book> getMyBooks() {
        return myBooks;
    }

    @OwnerCalled
    public void addBook(String author, String title, int ISBN) {
        Book newBook = new Book(this, author, title, ISBN);
        myBooks.add(newBook);  //Creates instance
    }

    /*
     * Should delete the book and then remove it form myBooks
     */
    @OwnerCalled
    public void deleteBook(Book book) {

    }

}
