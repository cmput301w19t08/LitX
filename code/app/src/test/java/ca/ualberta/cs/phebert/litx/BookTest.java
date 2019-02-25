package ca.ualberta.cs.phebert.litx;

import org.junit.Test;

import static org.junit.Assert.*;

public class BookTest {

    /**
     * Tests to see if setOwner works
     */
    @Test
    public void owner_isCorrect()
    {
        Owner owner = new Owner();
        Book book = new Book();
        assertNull(book.getOwner());
        book.setOwner(owner);
        assertEquals(owner, book.getOwner());
    }

    /**
     * Checks to see is available is correct
     */
    @Test
    public void available_isCorrect()
    {
        Book book = new Book();
        assertTrue(book.getAvailable());
        book.setAvailable(true);
        assertTrue(book.getAvailable());
        book.setAvailable(false);
        assertFalse(book.getAvailable());
        book.setAvailable(false);
        assertFalse(book.getAvailable());
        book.setAvailable(true);
        assertTrue(book.getAvailable());
    }

    /**
     * Tests to make sure available doesnt change any other attributes of book
     */
    @Test
    public void available_isIndependant()
    {
        Book book = new Book();
        Owner originalOwner = book.getOwner();
        String originalAuthor = book.getAuthor();
        long originalISBN = book.getISBN();
        book.setAvailable(false);
        assertEquals(originalOwner, book.getOwner());
        assertEquals(originalAuthor, book.getAuthor());
        assertEquals(originalISBN, book.getISBN());
    }

    /**
     * Tests to see if setAuthor is correct
     *
     */
    @Test
    public void setAuthorTest()
    {
        Book book  = new Book();
        long originalISBN = book.getISBN();
        assertNull(book.getAuthor());
        book.setAuthor("TestAuthor");
        assertEquals("TestAuthor", book.getAuthor());
    }

    /**
     * Tests to see if Author is independant
     */
    @Test
    public void setAuthorIndependent()
    {
        Book book  = new Book();
        Owner originalOwner = book.getOwner();
        Boolean available = book.getAvailable();
        long originalISBN = book.getISBN();
        String title = book.getTitle();
        book.setAuthor("NewAuthor");
        assertEquals(originalOwner, book.getOwner());
        assertEquals(originalISBN, book.getISBN());
        assertEquals(available, book.getAvailable());
        assertEquals(title, book.getTitle());
    }

    /**
     * Tests to see if setTitle is correct
     */
    @Test
    public void setTitleTest()
    {
        Book book = new Book();
        assertNull(book.getTitle());
        book.setTitle("testTitle");
        assertEquals("testTitle", book.getTitle())
    }

    /**
     * Tests to see if Title is independant
     */
    @Test
    public void setTitleIndependent()
    {
        Book book  = new Book();
        Owner originalOwner = book.getOwner();
        Boolean available = book.getAvailable();
        long originalISBN = book.getISBN();
        String author = book.getAuthor();
        book.setTitle("NewTitle");
        assertEquals(originalOwner, book.getOwner());
        assertEquals(originalISBN, book.getISBN());
        assertEquals(available, book.getAvailable());
        assertEquals(author, book.getAuthor());
    }

    /**
     * Tests to see if the setISBN is correct
     */
    @Test
    public void setISBNTest()
    {
        Book book = new Book();
        assertNull(book.getISBN());
        book.setISBN(1234);
        assertEquals(1234, book.getISBN());
    }

    /**
     * Tests to see if ISBN is independant
     */
    @Test
    public void setISBNIndependent()
    {
        Book book  = new Book();
        Owner originalOwner = book.getOwner();
        Boolean available = book.getAvailable();
        String title = book.getTitle();
        String author = book.getAuthor();
        book.setISBN(12345);
        assertEquals(originalOwner, book.getOwner());
        assertEquals(title, book.getTitle());
        assertEquals(available, book.getAvailable());
        assertEquals(author, book.getAuthor());
    }

    /**
     * Tests to see if setBorrower works
     */
    @Test
    public void setBorrowerTest()
    {
        Book book = new Book();
        User user = new User("usertest", "user@gmail.com", 123456789);
        assertNull(book.getBorrower());
        book.setBorrower(user);
        assertEquals(user, book.getBorrower());
    }

    /**
     * Tests to see if setBorrower is independant
     */
    @Test
    public void setBorrowerIndependent()
    {
        Book book  = new Book();
        User user = new User("userTest", "User@gmail.com", 123456789);
        Owner originalOwner = book.getOwner();
        Boolean available = book.getAvailable();
        long originalISBN = book.getISBN();
        String title = book.getTitle();
        String author = book.getAuthor();
        book.setBorrower(user);
        assertEquals(originalOwner, book.getOwner());
        assertEquals(originalISBN, book.getISBN());
        assertEquals(available, book.getAvailable());
        assertEquals(author, book.getAuthor());
        assertEquals(title, book.getTitle());
    }

    /**
     * Tests to see if the requestAccepted sets borrower to the proper user
     *  Tests to see if the requests array is now only the proper request, and only that request
     *  Sets status
     */
    @Test
    public void requestAcceptedTest()
    {
        Book book = new Book();
        Request request = new Request();
        User user = new User("TestUSer", "Test@gmail.com", 123456789);
        Request request1 = new Request();
        User user1 = new User("TestUSer1", "Test1@gmail.com", 987654321);
        // Add the requestors for the request
        request.setRequestor(user);
        request1.setRequestor(user1);
        // Add those requests to book.requests
        request.setBook(book);
        request1.setBook(book);
        //Accept request
        book.requestAccepted(request);
        //Check the borrower
        assertEquals(user, book.getBorrower());
        //Check the size of the arrayList requests
        assertEquals(1, book.getRequests().size());
        // Make sure it is the proper request
        assertEquals(book.getRequests().get(0), request);

    }

    /**
     * Tests to see if request resolved deletes the proper request
     * Deletes the request
     * Deletes the request in the requestors requests
     */
    @Test
    public void requestResolved()
    {
        Book book = new Book();
        Request request = new Request();
        User user = new User("TestUSer", "Test@gmail.com", 123456789);
        Request request1 = new Request();
        User user1 = new User("TestUSer1", "Test1@gmail.com", 987654321);
        // Add the requestors for the request
        request.setRequestor(user);
        Request request2 = request;
        request1.setRequestor(user1);
        // Add those requests to book.requests
        request.setBook(book);
        request1.setBook(book);
        book.requestResolved(request);
        //Make sure the array doesnt contain request
        assertFalse(book.getRequests().contains(request));
        // request should be the only request in myRequests
        // Therefore after deletion size should be 0
        assertEquals(user.myRequests.size(), 0);
        assertNull(request);
    }
}

