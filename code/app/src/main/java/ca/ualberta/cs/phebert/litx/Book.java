package ca.ualberta.cs.phebert.litx;

import android.widget.ImageView;

import java.io.Serializable;
import java.util.ArrayList;

public class Book implements Serializable {
    private String author;
    private String title;
    private long isbn;
    private String status;
    private String ownerUid;

    // For testing the adapter
    //private User borrower;

    private String owner;
    private String docID; // Document ID in Firestore

    private ArrayList<Request> requests;
    private Request acceptedRequest;

    private ImageView photograph;

    public Book(String owner, String author, String title, long isbn, String ownerUid) {
        this.owner = owner;
        this.author = author;
        this.title = title;
        this.isbn = isbn;
        this.status = "Available";
        this.ownerUid = ownerUid;
    }

    public Book() {
    } // For firestore

    /**
     * sets Owner of the book
     *
     * @param owner string of the owner of the book
     */
    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getDocID() { return docID; }

    public void setDocID(String newDocID) { this.docID = newDocID; }

    public String getOwnerUid(){ return ownerUid;}

    public void setOwnerUid(String newUid){this.ownerUid = newUid; }

    /**
     * getter for status
     * @return String
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets this.status to status
     *
     * @param status String one of accepted, available, borrowed, requested
     */
    public void setStatus(String status) {
        this.status = status;

    }

    /**
     * Getter for owner
     *
     * @return String
     */
    public String getOwner() {
        return owner;
    }

    /**
     * Returns if the book is available
     *
     * @return Boolean
     */
    public Boolean isAvailable() {
        return this.status == "Available";
    }

    /**
     * getter for author
     *
     * @return String
     */
    public String getAuthor() {
        return author;
    }

    /**
     * setter for Author
     *
     * @param author String
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * getter for title
     *
     * @return String
     */
    public String getTitle() {
        return title;
    }

    /**
     * Setter for Title
     *
     * @param title String
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Getter for ISBN
     *
     * @return long type
     */
    public long getIsbn() {
        return isbn;
    }

    /**
     * setter for ISBN
     *
     * @param isbn long type
     */
    public void setIsbn(long isbn) {
        this.isbn = isbn;
    }

    /**
     * getter for requests
     *
     * @returns ArrayList<Request>
     */
    public ArrayList<Request> getRequests() {
        return requests;
    }


    /*public void setBorrower(User user) {
        this.borrower = user;

    }*/

    public User getBorrower() {
        if (acceptedRequest != null) {
            return acceptedRequest.getRequester();
        }
        return null;
    }

    /**
     * should fail if acceptedRequest is not null.
     * @param request A request that has been accepted by owner
     */
    public void setAcceptedRequest(Request request) {
        if (acceptedRequest == null)
            acceptedRequest = request;
            status = "accepted";
    }

    /**
     * Getter for acceptedRequest
     * @return Request
     */
    public Request getAcceptedRequest() {
        return acceptedRequest;
    }

    /**
     * getter for the photograph
     *
     * @return ImageView
     */
    public ImageView getPhotograph() {
        return photograph;
    }

    /**
     * setter for photograph
     *
     * @param photograph ImageView
     */
    public void setPhotograph(ImageView photograph) {
        this.photograph = photograph;
    }

    /*
     * Uses the getters for each Request to fill in the views of a viewRequest layout
     * (Probably going to be some form of a custom adapter layout)
     */
    public void viewRequests() {

    }
}
