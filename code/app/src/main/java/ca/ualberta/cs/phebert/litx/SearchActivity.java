package ca.ualberta.cs.phebert.litx;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Allows users to search all books for the book they want, they can search by title, author, isbn
 * or a combination of them
 * @author phebert, sdupasqu, thryniw
 * @version 1.0
 * @see MainActivity, ViewBookActivity, BookListAdapter
 */
public class SearchActivity extends AppCompatActivity  {

    private ArrayList<Book> bookresults;
    String keywords;
    BookListAdapter adapter;
    RecyclerView recycler;
    RecyclerView.LayoutManager layoutManager;

    /**
     * Initializes the search, default is nothing searched however if the user clicked a book from
     * main activity the search auto fills that
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Intent intent = getIntent();
        if (intent.getStringExtra("BOOK_NAME") != null){
            EditText editText = findViewById(R.id.input_search);
            editText.setText(intent.getStringExtra("BOOK_NAME"));
        }
        bookresults = new ArrayList<>();
        recycler = (RecyclerView) findViewById(R.id.search_results);
        recycler.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(SearchActivity.this);
        recycler.setLayoutManager(layoutManager);
        searchPress(null);
    }

    /**
     * Finds list of Books who match all keywords (separated by " ") to the search input
     * @param keywords List of words that the query must match
     * @return The list of selected book that match all keywords
     */
    ArrayList<Book> findBook(String... keywords) {
        ArrayList<Book> Original = new ArrayList<>(Book.getAll().values());
        boolean[] found = new boolean[Original.size()];
        outside: for(int i = 0; i < Original.size(); i++) {
            Book book = Original.get(i);
            int count = 0; // How many keywords does the book contain?
            int wordCount = 0;
            for (String keyword : keywords) {
                wordCount += 1;
                for(String word: book.getTitle().split(" ")) {
                    if(word.toLowerCase().equals(keyword.toLowerCase()) && book.isAvailable()) {
                        count += 1;
                    }
                }
                for (String word: book.getAuthor().split(" ")) {
                    if(word.toLowerCase().equals(keyword.toLowerCase()) && book.isAvailable()) {
                        count += 1;
                    }
                }
                for (String word: Long.toString(book.getIsbn()).split(" ")) {
                    if(word.toLowerCase().equals(keyword.toLowerCase()) && book.isAvailable()) {
                        count += 1;
                    }
                }
            }
            if (count >= wordCount) {
                found[i] = true;
            }
        }
        ArrayList<Book> ans = new ArrayList<>();
        for (int i = 0; i < Original.size(); i++) {
            if(found[i]) {
                ans.add(Original.get(i));
            }
        }
        return ans;
    }

    /**
     * Updates the adapter to match the search
     */
    protected void updateRecycler () {
        adapter = new BookListAdapter(SearchActivity.this, bookresults, 0);
        recycler.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    /**
     * Actions on the press of the Search Button
     * Get all the books on database
     * Get filtered list
     * Update the view
     * @param v
     */
    public void searchPress (View v) {
        keywords = ((EditText)findViewById(R.id.input_search)).getText().toString();
        bookresults.clear();
        if(keywords.isEmpty()) {
            Log.d("LitX", "search was empty, showing all books");

            for (Book book : (Book.getAll().values())) {
                if (book.isAvailable()){
                    bookresults.add(book);
                }
            }
        } else {
            Log.d("LitX", "searching for keywords");
            bookresults = findBook(keywords.split(" "));
        }
        updateRecycler();
    }
}
