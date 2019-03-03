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
    public User(String username, String email, String phone) {
        certificate = null;
        editProfile(username, email, phone);
    }

    /**
     * called upon authentication. this is
     * @param fbUser
     */
    public User(FirebaseUser fbUser) {
        // does not use the
        userName = ""; //TODO
        email = fbUser.getEmail();
        phoneNumber = "(780) 000 0000";// TODO
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
        this.userName = username;
        if(certificate != null) {
            // TODO sync with firebase/store
        }
    }

    public String getUserName() {
        // no need to to sync, should be automaticly set when loading the user
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
            // we will not be using phone authentication, so no need to update in Firebase User
            // TODO sync with Firebase/store
        }
    }

    public String getPhoneNumber () {
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

    public void editProfile(String username, String email, String phone) {
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

    }

    /*
     * Should delete the book and then remove it form myBooks
     */
    @OwnerCalled
    public void deleteBook(Book book) {

    }

}
