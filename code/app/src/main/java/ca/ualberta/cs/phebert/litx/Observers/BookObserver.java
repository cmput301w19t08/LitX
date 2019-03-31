package ca.ualberta.cs.phebert.litx.Observers;

import ca.ualberta.cs.phebert.litx.Models.Book;

public interface BookObserver {
    void onUpdate(Book book);
}
