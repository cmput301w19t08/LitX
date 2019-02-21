package ca.ualberta.cs.phebert.litx;

public class Search {
    private User borrower;
    private ArrayList<Book> results;
    private int numResults; //number of results we get, may not be necessary
    private String authorKeyWord;
    private String titleKeyWord;
    private int ISBNKeyWord;

    /*
     * This should be called by the constructor. Checks either a database with all books
     * or all the books of all the owners.
     */
    public ArrayList<Book> findBooks() {
        return books;
    }

    // Runs a layout that displays all the books in results using their getter methods
    public void displayResults() {

    }
}
