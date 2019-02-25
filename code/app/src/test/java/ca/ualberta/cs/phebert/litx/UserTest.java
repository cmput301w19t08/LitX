package ca.ualberta.cs.phebert.litx;

import org.junit.Test;
import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class UserTest {
    /**
     * Tests the setuserName method
     * Should set a username to a new username if that new username is unique
     */
    @Test
    public void setUserNameTest()
    {
        User user = new User("user", "user@ualberta.ca", 123456789);
        User user1 = new User("user1", "user1@ualberta.ca", 234567890);
        user1.setUserName("newUser");
        assertEquals("newUser", user1.getUserName());
        // May have to be changed depending on how we implement it
        user1.setUserName("user");
        assertEquals("newUser",user1.getUserName());
    }

    /**
     * Tests the setEmail method
     * Does not need to see if email is unique
     */
    @Test
    public void setEmailTest()
    {
        User user = new User("user", "user@ualberta.ca", 123456789);
        assertEquals("user@ualberta.ca", user.getEmail());
        user.setEmail("newEmail@email.com");
        assertEquals("newEmail@email.com", user.getEmail());

    }

    /**
     * Tests the setPhoneNse unque phone number
     */
    @Test
    public void setPhoneNumberTest()
    {
        User user = new User("user", "user@ualberta.ca", 123456789);
        assertEquals(123456789, user.getPhoneNumber());
        user.setPhoneNumber(987654321);
        assertEquals(987654321, user.getPhoneNumber());
    }

    /**
     * Tests the editProfile method
     * Unsure how to get the data from editTexts to test this method
     *
     */
    @Test
    public void editProfileTest()
    {

    }

    /**
     * Tests the removeRequest method
     *
     */
    @Test
    public void removeRequestTest()
    {
        User owner = new User("owner", "owner@ualberta.ca", 123456789);
        User requestor = new User("requestor", "requestor@gmail.com", 9876543);
        Book book = new Book(owner, "Author", "Title", 12345);
        Book book1 = new Book(owner, "Author1", "Title1", 12346);
        // check to see that there is no requests
        assertEquals(0, owner.viewRequests().size());
        Request request = new Request(book, owner, requestor);
        Request request1 = new Request(book1, owner, requestor);
        //ensure the requests were added
        assertEquals(2, owner.viewRequests().size());
        // Remove the request
        owner.removeRequest(request);
        // Ensure that the requests now only contains one request
        assertEquals(1, owner.viewRequests().size());
        //Ensure that the proper request was deleted
        assertTrue(owner.viewRequests().contains(request1));
    }

    /**
     * Tests the succesfulRequest method
     *
     */
    @Test
    public void succesfulRequestTest()
    {

        User owner = new User("owner", "owner@ualberta.ca", 123456789);
        User requestor = new User("requestor", "requestor@gmail.com", 9876543);
        Book book = new Book(owner, "Author", "Title", 12345);
        //ensure that accepted requests is zero
        assertEquals(owner.getAcceptedRequests().size(), 0);
        Request request = new Request(book, owner, requestor);
        owner.successfulRequest(request);
        assertTrue(owner.getAcceptedRequests().contains(request));
        assertEquals(owner.getAcceptedRequests().size(), 1);
    }




    @Test
    public void addBookTest()
    {
        User  owner = new User("owner", "owner@gmail.com", 12345678);
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
        User owner = new User("owner", "owner@gmail.com", 123456789);
        assertNull(owner.getMyBooks());
        owner.addBook("author", "title", 1234);
        Book book = owner.getMyBooks().get(0);
        owner.deleteBook(book);
        assertTrue(owner.getMyBooks().isEmpty());
    }


}
