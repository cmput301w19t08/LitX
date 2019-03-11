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
        User user = new User("user", "user@ualberta.ca", "(123) 456 7890");
        User user1 = new User("user1", "user1@ualberta.ca", "(234) 567 8901");
        user1.setUserName("newUser");
        assertEquals("newUser", user1.getUserName());
        // May have to be changed depending on how we implement it
        user1.setUserName("user");
        assertEquals("newUser",user1.getUserName());
    }

    /**
     * Tests to make sure the setUserName is independent
     */
    @Test
    public void setUserNameIndependance()
    {
        User user = new User("user", "user@ualberta.ca", "(123) 456 7890");
        String email = user.getEmail();
        String phone = user.getPhoneNumber();
        user.setUserName("NewUser");
        assertEquals(phone, user.getPhoneNumber());
        assertEquals(email, user.getEmail());

    }

    /**
     * Tests the setEmail method
     * Does not need to see if email is unique
     */
    @Test
    public void setEmailTest()
    {
        User user = new User("user", "user@ualberta.ca", "(123) 456 7890");
        assertEquals("user@ualberta.ca", user.getEmail());
        user.setEmail("newEmail@email.com");
        assertEquals("newEmail@email.com", user.getEmail());

    }

    /**
     * Tests to make sure the setEmail is independent
     */
    @Test
    public void setEmailIndependance()
    {
        User user = new User("user", "user@ualberta.ca", "(123) 456 7890");
        String username = user.getUserName();
        String phone = user.getPhoneNumber();
        user.setEmail("NewEmail@email.com");
        assertEquals(username, user.getUserName());
        assertEquals(phone, user.getPhoneNumber());
    }

    /**
     * Tests the setPhoneNumber method
     */
    @Test
    public void setPhoneNumberTest()
    {
        User user = new User("user", "user@ualberta.ca", "(123) 456 7890");
        assertEquals("(123) 456 7890", user.getPhoneNumber());
        user.setPhoneNumber("987654321");
        assertEquals("987654321", user.getPhoneNumber());
    }

    /**
     * Tests to make sure the setUserName is independent
     */
    @Test
    public void setPhoneNumberIndependance()
    {
        User user = new User("user", "user@ualberta.ca", "(123) 456 7890");
        String username = user.getUserName();
        String email = user.getEmail();
        user.setUserName("NewUser");
        assertEquals(username, user.getUserName());
        assertEquals(email, user.getEmail());

    }


    /**
     * Tests the removeRequest method
     * Should remove the request from the owners requests
     * Throws an exception due to function returning null
     */
    @Test
    public void removeRequestTest()
    {
        User owner = new User("owner", "owner@ualberta.ca", "(123) 456 7890");
        User requestor = new User("requestor", "requestor@gmail.com", "9876543");
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
     * Throws an exception at line 94 due to return of the method being null
     */
    @Test
    public void succesfulRequestTest()
    {

        User owner = new User("owner", "owner@ualberta.ca", "123456789");
        User requestor = new User("requestor", "requestor@gmail.com", "9876543");
        Book book = new Book(owner, "Author", "Title", 12345);
        //ensure that accepted requests is zero
        assertNull(owner.getAcceptedRequests());
        Request request = new Request(book, owner, requestor);
        owner.successfulRequest(request);
        assertTrue(owner.getAcceptedRequests().contains(request));
        assertEquals(owner.getAcceptedRequests().size(), 1);
    }

    /**
     * Tests the setMyLocation method
     */

    @Test
    public void setMyLocationTest()
    {
        User user = new User("User", "user@gmail.com", "1223456789");
        assertNull(user.getMyLocation());
        Coordinate coordinate = new Coordinate(24,24);
        user.setMyLocation(24, 24);
        assertEquals(coordinate, user.getMyLocation());
    }

    /**
     * Tests the addBook method
     * Owners myBooks should be increased in size by one
     * Throws a exception due to the function returning null
     */
    @Test
    public void addBookTest()
    {
        User  owner = new User("owner", "owner@gmail.com", "12345678");
        assertNull(owner.getMyBooks());
        owner.addBook("author", "title", 1234);
        // addbook will construct a book and add it to myBooks
        assertEquals(1, owner.getMyBooks().size());
    }

    /**
     * Will test to see if the book is properly deleted
     * Makes sure that owners myBook is deleted
     * throws exception due to function returning null
     */
    @Test
    public void deleteBookTest()
    {
        User owner = new User("owner", "owner@gmail.com", "123456789");
        assertNull(owner.getMyBooks());
        owner.addBook("author", "title", 1234);
        Book book = owner.getMyBooks().get(0);
        owner.deleteBook(book);
        assertTrue(owner.getMyBooks().isEmpty());
    }


}
