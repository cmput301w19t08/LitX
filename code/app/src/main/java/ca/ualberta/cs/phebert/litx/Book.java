package ca.ualberta.cs.phebert.litx;

import android.widget.ImageView;

import java.util.ArrayList;

public class Book {
    private String author;
    private String title;
    private long isbn;

    private User owner;

    private ArrayList<Request> requests;
    private Request acceptedRequest;

    private ImageView photograph;

    public Book(User owner, String Author, String title, long isbn) {

    }

    public void setOwner(User setowner) {

    }

    public User getOwner() {
        return owner;
    }

    public Boolean isAvailable() {
        return acceptedRequest == null;
    }

    public String getAuthor() {
        return author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {

    }

    public long getIsbn() {
        return isbn;
    }

    public void setIsbn(long isbn) {

    }

    public ArrayList<Request> getRequests() {
        return requests;
    }


    public void setBorrower(User user) {

    }

    public User getBorrower() {
        if(acceptedRequest != null) {
            return acceptedRequest.getRequestor();
        }
        return null;
    }

    /**
     * should fail if acceptedRequest is not null.
     * @param request
     */
    public void setAcceptedRequest(Request request) {

    }

    public Request getAcceptRequest() {
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
