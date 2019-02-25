package ca.ualberta.cs.phebert.litx;

import android.media.Image;

import java.util.ArrayList;

public class Owner {


    private ArrayList<Book> myBooks;

    /*
     * Creates a Book by starting a book layout (much like we do in edit profile)
     * then pass the values our customer enters into the constructor for a Book and adds
     * this new book to myBooks. The parameters here are meant to represent the values our customer
     * will enter into the views
     */
    public void addBook(String author, String title, int ISBN) {

    }

    /**
     * Used for testing
     * @return mybooks
     */
     public ArrayList<Book> getMyBooks() {
        return myBooks;
    }

    /* Uses the getters for each book to fill in the views of a viewBook layout
     * (Probably going to be a custom adapter layout). If no filter should display all books.
     * If there is a filter only dispaly books with the specified status
     */
    public void viewBooks(String filter) {

    }

    /*
     * Should delete the book and then remove it form myBooks
     */
    public void deleteBook(Book book) {

    }

    /*
     * This method should use our getter methods for author, title and ISBN to fill in views of
     * book layout and then should use our setter methods to change the values to the new values
     * in the views when the customer hits "done".
     * The extra parameters of this function represent the values the customer may enter in the views
     * of the book layout of the profile layout. In addition this function should only be able
     * to be called if the User is trying to edit their own profile
     */
    public void editBook(Book book, String newAuthor, String newTitle, int newISBN) {

    }

    /*
     * This invokes a call to accepted in the Request object we have passed in and if Coordinate is
     * not null and is a valid Coordinate it invokes a call to addMap(Coordinate) in the
     * Request object we has passed in as a parameter
     */
    public void acceptRequest(Request request, Coordinate coordinate) {

    }

    // Declines a request
    public void declineRequest(Request request) {

    }

    /*
     * Scans the ISBN to get the book, sets the books status to be borrowed.
     */
    public void handOver(int ISBN) {

    }

    /*
     * Scans the ISBN to get the book then calls setAvailability on the book to change its availabilty
     * to True. (Assuming the Borrower returned it and thereby changed its status to available).
     */
    public void receiveReturn(int ISBN) {

    }

    /*
     * Creates a Photograph and then calls setPhotograph on the book we were passed in as a parameter
     */
    public void addPhotograph(Book book, Image image) {

    }

    // Calls setPhotograph(Null) on the book we passed in as a parameter
    public void removePhotograph(Book book) {

    }

    // Calls viewRequests on the Book we passed in as a parameter
    public void viewBookRequests(Book book) {

    }
}
