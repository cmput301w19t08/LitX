package ca.ualberta.cs.phebert.litx;
import org.junit.Test;




import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class RequestTest{

    /**
     * Tests the accept method Request
     */
    @Test
    public void acceptedTest(){
        User owner = new User("Test", "Test@gmail.com", 123457678);
        Book book = new Book( owner, "Auhtor", "Title", 12345);
        User requestor = new User("requestor", "requestor@gmail.com", 9876541);
        Request request = new Request(book,owner, requestor);

        // Make sure that both of the arraylists of requests now contain the request
        assertTrue(requestor.viewRequests().contains(request));
        assertTrue(book.getRequests().contains(request));
        request.accept();
        // After request accepted Accepted requests in user should have the request
        assertTrue(requestor.getAcceptedRequests().contains(request));
        // Borrower for book should be updated to user
        assertEquals(requestor, book.getBorrower());
        assertEquals(request, book.getAcceptedRequest());
        // The books status should be 'accepted'
        assertEquals("Accepted", request.getStatus());
    }

    /**
     * This will test resolved and since delete is called in resolved then it will also
     * test delete method
     * AcceptedRequest book --> Null
     * Request.getStatus --> Resolved
     **/
    @Test
    public void resolvedTest(){
        User owner = new User("Test", "Test@gmail.com", 123457678);
        Book book = new Book( owner, "Auhtor", "Title", 12345);
        User requestor = new User("requestor", "requestor@gmail.com", 9876541);
        Request request = new Request(book,owner, requestor);

        assertEquals(0, requestor.viewRequests().size());
        assertEquals(0, book.getRequests().size());
        // Make sure that both of the arraylists of requests now contain the request
        assertTrue(owner.viewRequests().contains(request));
        assertTrue(book.getRequests().contains(request));
        request.resolve();
        // Make sure that accepted request is null
        assertNull(book.getAcceptedRequest());
        //Check to see if the request is still in User.requests
        assertEquals("Resolved", request.getStatus());
        // Check to see if request has been
        assertFalse(book.getRequests().contains(request));
    }


}