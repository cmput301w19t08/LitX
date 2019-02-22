package ca.ualberta.cs.phebert.litx;

import java.util.ArrayList;

public class Book {
    private String author;
    private String title;
    private long ISBN;
    private String status;
    private Boolean available;
    private Owner owner;
    private ArrayList<Request> requests;
    private User borrower;
    private Photograph photograph;

    public void setOwner(Owner setowner) {

    }

    public Owner getOwner() {
        return owner;
    }

    /*
     * For updating status and availability
     */
    public void setStatus(String setstatus) {

    }

    public Boolean getAvailable() {
        return available;
    }

    /*
     * Uses the value of status to determine what the availability should be
     */
    public void setAvailable(Boolean available) {

    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {

    }

    public long getISBN() {
        return ISBN;
    }

    public void setISBN(long ISBN) {

    }

    public ArrayList<Request> getRequests() {
        return requests;
    }

    public void setBorrower(User user) {

    }

    public User getBorrower() {
        return borrower;
    }
    /*
     * Should empty the request array so that only one remains
     * Updates borrower to be the new requestor of the Request object passed in.
     * Sets own status to be accepted
     */
    public void requestAccepted(Request arequest) {

    }

    /*
     * Removes Request from requests and calls delete in Request
     */
    public void requestResolved(Request request) {

    }

    public Photograph getPhotograph() {
        return photograph;
    }

    public void setPhotograph(Photograph photograph) {
        this.photograph = photograph;
    }

    /*
     * Uses the getters for each Request to fill in the views of a viewRequest layout
     * (Probably going to be some form of a custom adapter layout)
     */
    public void viewRequests() {

    }
}
