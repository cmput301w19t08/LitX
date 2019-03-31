package ca.ualberta.cs.phebert.litx;

import org.junit.Test;

import ca.ualberta.cs.phebert.litx.Models.Book;
import ca.ualberta.cs.phebert.litx.Models.Request;
import ca.ualberta.cs.phebert.litx.Models.User;

import static org.junit.Assert.*;

public class BookTest {

    /**
     * Tests to see if setOwner works
     */
    @Test
    public void owner_isCorrect()
    {
        User owner = new User("owner", "owner@gmail.com", 123456789);
        Book book = new Book(owner,"Author", "Title", 12345);
        assertEquals(owner, book.getOwner());
        User owner1 = new User("owner1", "owner1@gmail.com", 988765421);
        book.setOwner(owner1);
        assertEquals(owner, book.getOwner());
    }

    /**
     * Checks to see is available is correct
     */
    @Test
    public void available_isCorrect()
    {
        User owner = new User("owner", "owner@gmail.com", 123456789);
        User requestor = new User("requestor", "requestor@gmail.com", 98765);
        Book book = new Book(owner,"Author", "Title", 12345);
        Request request = new Request(book, owner, requestor);
        assertTrue(book.isAvailable());
        // Set the accepted request to request
        book.setAcceptedRequest(request);
        assertFalse(book.isAvailable());
    }

    /**
     * Tests to make sure available doesnt change any other attributes of book
     */
    @Test
    public void available_isIndependant()
    {

        User owner = new User("owner", "owner@gmail.com", 123456789);
        User requestor = new User("requestor", "requestor@gmail.com", 98765);
        Book book = new Book(owner,"Author", "Title", 12345);
        Request request = new Request(book, owner, requestor);
        String originalAuthor = book.getAuthor();
        long originalISBN = book.getIsbn();
        book.setAcceptedRequest(request);
        assertEquals(owner, book.getOwner());
        assertEquals(originalAuthor, book.getAuthor());
        assertEquals(originalISBN, book.getIsbn());
    }


    /**
     * Tests to see if setBorrower works
     */
    @Test
    public void setBorrowerTest()
    {
        User owner = new User("owner", "owner@gmail.com", 123456789);
        User requestor = new User("requestor", "requestor@gmail.com", 98765);
        Book book = new Book(owner,"Author", "Title", 12345);
        assertNull(book.getBorrower());
        book.setBorrower(requestor);
        assertEquals(requestor, book.getBorrower());
    }

    /**
     * Tests to see if setBorrower is independant
     */
    @Test
    public void setBorrowerIndependent()
    {
        User owner = new User("owner", "owner@gmail.com", 123456789);
        User requestor = new User("requestor", "requestor@gmail.com", 98765);
        Book book = new Book(owner,"Author", "Title", 12345);
        Boolean available = book.isAvailable();
        long originalISBN = book.getIsbn();
        String title = book.getTitle();
        String author = book.getAuthor();
        book.setBorrower(requestor);
        assertEquals(owner, book.getOwner());
        assertEquals(originalISBN, book.getIsbn());
        assertEquals(available, book.isAvailable());
        assertEquals(author, book.getAuthor());
        assertEquals(title, book.getTitle());
    }

    /**
     * Tests to see if the requestAccepted sets borrower to the proper user
     * Accepted request should equal request
     */
    @Test
    public void requestAcceptedTest()
    {
        User owner = new User("owner", "owner@gmail.com", 123456789);
        User requestor = new User("requestor", "requestor@gmail.com", 98765);
        Book book = new Book(owner,"Author", "Title", 12345);
        Request request = new Request(book, owner, requestor);
        User requestor1 = new User("TestUSer1", "Test1@gmail.com", 987654321);
        Request request1 = new Request(book, owner, requestor1 );
        //Accept request
        book.setAcceptedRequest(request);
        //Check the borrower
        assertEquals(requestor, book.getBorrower());
        // Make sure it is the proper request
        assertEquals(book.getAcceptedRequest(), request);

    }


}

