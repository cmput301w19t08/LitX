package ca.ualberta.cs.phebert.litx;

import android.widget.ImageView;

import java.io.Serializable;
import java.util.ArrayList;

public class Book implements Serializable {
    private String author;
    private String title;
    private long isbn;
    // For testing the adapter
    //private User borrower;
    private String owner;
    private String docID; // Document ID in Firestore

    private ArrayList<Request> requests;
    private Request acceptedRequest;

    private ImageView photograph;

    public Book(String owner, String author, String title, long isbn) {
        this.owner = owner;
        this.author = author;
        this.title = title;
        this.isbn = isbn;
    }

    public Book() {} // For firestore

    public String getDocID() { return docID; }

    public void setDocID(String newDocID) { this.docID = newDocID; }

    public void setOwner(String owner) { this.owner = owner; }

    public String getOwner() { return owner; }

    public Boolean isAvailable() {
        return acceptedRequest == null;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) { this.author = author; }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) { this.title = title; }

    public long getIsbn() {
        return isbn;
    }

    public void setIsbn(long isbn) {
        this.isbn = isbn;
    }


    public ArrayList<Request> getRequests() {
        return requests;
    }


    /*public void setBorrower(User user) {
        this.borrower = user;

    }*/

    public User getBorrower() {
        if(acceptedRequest != null) {
            return acceptedRequest.getRequestor();
        }
        return null;
    }

    /**
     * should fail if acceptedRequest is not null.
     * @param request A request that has been accepted by owner
     */
    public void setAcceptedRequest(Request request) {
        if(acceptedRequest == null) acceptedRequest = request;
    }

    public Request getAcceptedRequest() {
        return acceptedRequest;
    }

    public ImageView getPhotograph() {
        return photograph;
    }

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
