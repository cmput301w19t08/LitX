package ca.ualberta.cs.phebert.litx;
import android.widget.ImageView;

import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class OwnerTest {
    /**
     * Should we construct the book before passing it to add book?
     * will maybe make it easier to test
     */
    @Test
    public void addBookTest()
    {
        Owner owner = new Owner();
        assertNull(owner.getMyBooks());
        owner.addBook("author", "title", 1234);
        // addbook will construct a book and add it to myBooks
        assertEquals(1, owner.getMyBooks().size());
    }

    /**
     * Will test to see if the book is properly deleted
     */
    @Test
    public void deleteBookTest()
    {
        Owner owner = new Owner();
        assertNull(owner.getMyBooks());
        Book book = new Book();
        book.setAuthor("Author");
        book.setTitle("Title");
        book.setISBN(1234);
        owner.addBook("Auhtor", "Title", 1234);
        owner.deleteBook(book);
        assertTrue(owner.getMyBooks().isEmpty());

    }

    /**
     * Tests the editBookTest method
     */
    @Test
    public void editBookTest()
    {
        Owner owner = new Owner();
        owner.addBook("auhtor", "title", 1234);
        Book book = owner.getMyBooks().get(0);
        owner.editBook(book, "Author", "Title", 12345);
        // Since the books values should be directly changed dont need to reassign book
        assertEquals("Author", book.getAuthor());
        assertEquals("Title", book.getAuthor());
        assertEquals(12345, book.getISBN());
    }

    /**
     * Tests the acceptRequest method
     */
    @Test
    public void acceptRequestTest()
    {
        Owner owner = new Owner();
        Request request = new Request();
        User user = new User("test", "test@test.com", 18005555);
        Book book  = new Book();
        owner.acceptRequest(request, null);
        // Make sure both of the arraylist sizes are empty to start
        assertEquals(0, user.viewRequests().size());
        assertEquals(0, book.getRequests().size());
        // set the book and requestor of the request
        request.setBook(book);
        request.setRequestor(user);
        // Make sure that both of the arraylists of requests now contain the request
        assertTrue(user.viewRequests().contains(request));
        assertTrue(book.getRequests().contains(request));
        request.accepted();
        // After request accepted Accepted requests in user should have the request
        assertTrue(user.viewAcceptedRequests().contains(request));
        // The size of Requests should be updated to 1
        assertEquals(1, book.getRequests().size());
        // Borrower for book should be updated to user
        assertEquals(user, book.getBorrower());
        // The books status should be 'accepted'
        assertEquals("accepted", book.getStatus());
    }

    /**
     * TODO
     * Not sure how to implement this Test
     */
    @Test
    public void declineRequestTest()
    {

    }

    /*
     * Test for handOver
     *
     */
    @Test
    public void handOverTest()
    {
        Owner owner = new Owner();
        owner.addBook("author", "title", 1234);
        Book book = owner.getMyBooks().get(0);
        owner.handOver(1234);
        assertEquals("borrowed", book.getStatus());
    }

    /**
     * Test for receiveReturn, availability of book to True
     */
    @Test
    public void recieveReturnTest()
    {
        Owner owner = new Owner();
        owner.addBook("author", "title", 1234);
        Book book = owner.getMyBooks().get(0);
        owner.receiveReturn(1234);
        assertTrue(book.getAvailable());

    }


    /**
     * TODO
     * Need to figure out how the Photograph constructor will be implemented
     *
     */
    @Test
    public void addPhotographTest()
    {
        Owner owner = new Owner();
        owner.addBook("author", "title", 1234);
        Book book = owner.getMyBooks().get(0);
        ImageView image = new ImageView();
        owner.addPhotograph(book, image);
        Photograph photograph = new Photograph();
        assertEquals(image, photograph.image);
    }

    /**
     * Tests to see if the photgraph was set to Null
     */
    @Test
    public void removePhotographTest()
    {
        Owner owner = new Owner();
        owner.addBook("author", "title", 1234);
        Book book = owner.getMyBooks().get(0);
        owner.removePhotograph(book);
        assertNull(book.getPhotograph());
    }

    /**
     * Tests to see if the requests that are returned are the same
     *
     */
    @Test
    public void viewBooksRequestsTest()
    {
        Owner owner = new Owner();
        owner.addBook("author", "title", 1234);
        Book book = owner.getMyBooks().get(0);
        assertEquals(owner.getMyBooks(), book.getRequests());
    }
}

