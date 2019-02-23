package ca.ualberta.cs.phebert.litx;

import java.util.ArrayList;

public class User {
    protected String userName;
    protected String email;
    protected int phoneNumber;
    protected ArrayList<Request> acceptedRequests;
    protected ArrayList<Request> myRequests;
    protected ArrayList<Book> borrowedBooks;

    /*
     * Check if username is unique
     */
    public User(String username, String email, int phone) {

    }

    /*
     * Check if username is unique
     */
    public void setUserName(String username) {

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

    /*
     * Takes input from textviews
     * Using setters for User Profile
     */
    public void editProfile() {

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

    public ArrayList<Request> viewAcceptedRequests () {
        return acceptedRequests;
    }

    public void displayProfile(User user) {

    }

    public ArrayList<Book> viewBorrowedBooks() {
        return borrowedBooks;
    }

    /*
     *
     */
    public Book scanBook() {
        return new Book();
    }

    public void returnBook () {

    }

    /*
     * Takes searchbar String and returns Books database query result
     */
    public Book searchForBook (String author, String title, int ISBN) {
        return new Book();
    }

    /*
     * Takes searchbar String and returns User database query result
     */
    public User findUser(String username) {
        return this;        //temporary return while not implemented.
    }

    public void viewPickupLocation (Request request) {

    }

    //public Photograph viewPhotograph() {
    //    return photograph;
    //}

    /*public void setPhotograph(Photograph photograph) {
        this.photograph = photograph;
    }*/
}
