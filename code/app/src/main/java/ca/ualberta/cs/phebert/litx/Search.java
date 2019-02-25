package ca.ualberta.cs.phebert.litx;

import java.util.ArrayList;

public interface Search {
    ArrayList<Book> find(ArrayList<Book> Original, String[] keywords);
}
