package ca.ualberta.cs.phebert.litx;

import java.util.ArrayList;

// could be a class with a proper implementation
public interface Search {
    ArrayList<Book> find(ArrayList<Book> Original, String[] keywords);
}
