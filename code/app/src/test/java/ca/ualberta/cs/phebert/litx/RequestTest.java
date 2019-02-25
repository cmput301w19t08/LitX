package ca.ualberta.cs.phebert.litx;
import org.junit.Test;




import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class RequestTest{

    @Test
    public void setBookOwnerTest(){
        Owner owner = new Owner();
        Request request = new Request();
        // The owner should not be set
        assertNull(request.getBookOwner());
        request.setBookOwner(owner);
        // Now the owner should be the owner we supplied
        assertEquals(owner, request.getBookOwner());
    }

    @Test
    public void setBookTest(){
        Book book = new Book();
        Request request = new Request();
        // The book should not be set
        assertNull(request.getBook());
        request.setBook(book);
        // Test to see if the requests book was set
        assertEquals(book, request.getBook());
    }

    @Test
    public void setRequestorTest(){
        User user = new User("Test", "test@test.ca", 1800555555);
        Request request = new Request();
        // the requestor should not be set
        assertNull(request.getRequestor());
        request.setRequestor(user);
        // Now make sure the requestor was properly set
        assertEquals(user, request.getRequestor());
    }

    public void acceptedTest(){
        Book book = new Book();
        User user = new User("Test", "test@test.ca", 1800555555);
        // Make sure both of the arraylist sizes are empty to start
        assertEquals(0, user.viewRequests().size());
        assertEquals(0, book.getRequests().size());
        Request request = new Request();
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
     * This will test resolved and since delete is called in resolved then it will also
     * test delete method
     **/
    public void resolvedTest(){

        Request request = new Request();
        Book book = new Book();
        User user = new User("Test", "test@test.ca", 1800555555);
        assertEquals(0, user.viewRequests().size());
        assertEquals(0, book.getRequests().size());
        // set the book and requestor of the request
        request.setBook(book);
        request.setRequestor(user);
        // Make sure that both of the arraylists of requests now contain the request
        assertTrue(user.viewRequests().contains(request));
        assertTrue(book.getRequests().contains(request));
        // Have to make a second request equal the first one since the first will be deleted
        Request request2 = request;
        request.resolved();

        // Check to see if the request is still in books.requests
        assertFalse(book.getRequests().contains(request2));
        //Check to see if the request is still in User.requests
        assertFalse(user.viewRequests().contains(request2));
        // Check to see if request has been
        assertNull(request);
    }

    /**
     * This will test Addmap(Coordinate coordinate)
     */
    public void addMapTest() {
        Request request = new Request();
        Coordinate coordinate = new Coordinate();
        request.addMap(coordinate);
        assertEquals(request.getMap().getLocation(), coordinate);

    }
}