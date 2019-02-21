package ca.ualberta.cs.phebert.litx;

import org.junit.Test;

import static org.junit.Assert.*;

public class BookTest {
    @Test
    public void Owner_isCorrect()
    {
        Owner owner = new Owner();
        Book book = new Book();
        book.setOwner(owner);
        assertEquals(owner, book.getOwner());
    }
}
