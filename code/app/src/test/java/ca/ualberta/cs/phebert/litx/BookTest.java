package ca.ualberta.cs.phebert.litx;

import org.junit.Test;

import static org.junit.Assert.*;

public class BookTest {
    @Test
    public void owner_isCorrect()
    {
        Owner owner = new Owner();
        Book book = new Book();
        assertNull(book.getOwner());
        book.setOwner(owner);
        assertEquals(owner, book.getOwner());
    }

    @Test
    public void available_isCorrect()
    {
        Book book = new Book();
        assertTrue(book.getAvailable())
        book.setAvailable(true);
        assertTrue(book.getAvailable());
        book.setAvailable(false);
        assertFalse(book.getAvailable());
        book.setAvailable(false);
        assertFalse(book.getAvailable());
        book.setAvailable(true);
        assertTrue(book.getAvailable());
    }

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
}
