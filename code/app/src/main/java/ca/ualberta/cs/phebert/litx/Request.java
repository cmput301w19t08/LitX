package ca.ualberta.cs.phebert.litx;

public class Request {
    private User requestor;
    private Owner bookOwner;
    private Book book;
    private Map map; // Should be set to null by constructor

    // Should be called in the constructor
    public void notifyOwner(Owner owner) {

    }

    public void setBookOwner(Owner bookOwner) {

    }

    public Owner getBookOwner() {
        return bookOwner;
    }

    public void setBook(Book book) {

    }

    public Book getBook(){
        return book;
    }

    public void setRequestor(User requestor) {

    }

    public User getRequestor() {
        return requestor;
    }

    /*
     * This first calls succesfulRequest in User (our requestor) then acceptedRequest in Book
     */
    public void accepted() {

    }

    /*
     * This calls requestResolved in Book
     */
    public void resolved() {

    }

    /*
     * Calls removeRequest in User(requestor) then deletes this object
     */
    public void delete() {

    }

    /*
     * Adds a map which is assigned to map
     */
    public void addMap(Coordinate coordinate) {

    }

    public Map getMap() {
        return map;
    }
}
