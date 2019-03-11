package ca.ualberta.cs.phebert.litx;

        import com.google.firebase.auth.FirebaseAuth;
        import com.google.firebase.auth.FirebaseUser;

        import java.util.ArrayList;

public class User {
    private String userName;
    private String email;
    private int phoneNumber;
    private ArrayList<Request> acceptedRequests;
    private ArrayList<Request> myRequests;
    private ArrayList<Book> borrowedBooks;
    private ArrayList<Book> myBooks;
    private Coordinate myLocation;
    private FirebaseUser certificate;

    /*
     * Check if username is unique
     * Used for creation of new user
     */
    public User(String username, String email, int phone) {
        editProfile(username, email, phone);
    }

    /*
     * Gets the user from
     */
    public User(FirebaseUser fbUser) {

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
        this.userName = username;
    }

    public String getUserName() {
        return userName;
    }

    public void setEmail(String newEmail) {

    }

    public String getEmail() {
        return email;
    }

    public void setPhoneNumber (int newPhoneNumber) {

    }

    public int getPhoneNumber () {
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

    public void editProfile(String username, String email, int phone) {
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
