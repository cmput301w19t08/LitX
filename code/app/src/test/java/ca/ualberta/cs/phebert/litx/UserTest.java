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
        User user = new User("user", "user@ualberta.ca", 123456789);
        // check to see that there is no requests
        assertEquals(0, user.viewRequests().size());
        Request request = new Request();
        Request request1 = new Request();
        //Will add request to requests
        request.setRequestor(user);
        request1.setRequestor(user);
        //ensure the requests were added
        assertEquals(2, user.viewRequests().size());
        user.removeRequest(request);
        assertEquals(1, user.viewRequests().size());
        //Ensure that the proper request was deleted
        assertTrue(user.viewRequests().contains(request1));

    }

    /**
     * Tests the succesfulRequest method
     *
     */
    @Test
    public void succesfulRequestTest()
    {
        User user = new User("user", "user@ualberta.ca", 123456789);
        //ensure that accepted requests is zero
        assertEquals(0, user.acceptedRequests.size());
        Request request = new Request();
        user.successfulRequest(request);
        assertTrue(user.acceptedRequests.contains(request));
    }

    /**
     * TODO returnBookTest, searchForBookTest, FindUserTest, viewPickupLocationTest
     *
     *
     */
}
