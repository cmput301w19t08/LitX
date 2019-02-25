package ca.ualberta.cs.phebert.litx;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class User {
    private boolean isOwner;
    private String userName;
    private String email;
    private int phoneNumber;
    private ArrayList<Request> acceptedRequests;
    private ArrayList<Request> myRequests;
    private ArrayList<Book> borrowedBooks;
    private Coordinate myLocation;
    private FirebaseUser certificate;

    /*
     * Check if username is unique
     */
    public User(String username, String email, int phone) {
        editProfile(username, email, phone);
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

    /*
     * Gets profile information
     * in easily read manor
     */
    public void getProfile() {

    }

    public void editProfile(String username, String email, int phone) {
        setUserName(username);
        setEmail(email);
        setPhoneNumber(phone);
    }

    public ArrayList<Book> getBorrowedBooks() {
        return borrowedBooks;
    }

    public void

    public void setMyLocation (double x, double y) {
        myLocation = new Coordinate(x, y);
    }

    public Coordinate getMyLocation (){
        return myLocation;
    }

    public void setAuth (FirebaseUser newFbUser) {
        certificate = newFbUser;
    }

    public FirebaseUser getAuth () {
        return certificate;
    }
}
