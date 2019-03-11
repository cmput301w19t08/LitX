package ca.ualberta.cs.phebert.litx;

import android.widget.ImageView;

import java.io.Serializable;
import java.util.ArrayList;

public class Book implements Serializable {
    private String author;
    private String title;
    private long isbn;
    private Status status;


    enum Status {
        Available,
        Borrowed,
        Accepted,
        Requested
    }
    // For testing the adapter
    //private User borrower;

    private String owner;

    private ArrayList<Request> requests;
    private Request acceptedRequest;

    private ImageView photograph;

    public Book(String owner, String author, String title, long isbn) {
        this.owner = owner;
        this.author = author;
        this.title = title;
        this.isbn = isbn;
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

    /**
     * getter for status
     *
     * @return String
     */
    public String getStatus() {
        return this.status.toString();
    }

    /**
     * Sets this.status to status
     *
     * @param status String one of accepted, available, borrowed, requested
     */
    public void setStatus(String status) {
        if (status.equals("accepted")) {
            this.status = Status.Accepted;
        } else if (status.equals("available")) {
            this.status = Status.Available;
        } else if (status.equals("borrowed")) {
            this.status = Status.Borrowed;
        } else {
            this.status = Status.Requested;
        }

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
        return this.status == Status.Available;
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


    /**
     * Getter for borrower, if no acceptedRequest then returns null
     *
     * @returns User if there acceptedRequest else returns null
     */
    public User getBorrower() {
        if (acceptedRequest != null) {
            return acceptedRequest.getRequestor();
        }
        return null;
    }

    /**
     * should fail if acceptedRequest is not null.
     *
     * @param request A request that has been accepted by owner
     */
    public void setAcceptedRequest(Request request) {
        if (acceptedRequest == null)
            acceptedRequest = request;
        status = Status.Accepted;
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
}
