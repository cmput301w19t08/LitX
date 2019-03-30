package ca.ualberta.cs.phebert.litx;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Collection;

public class SearchActivity extends AppCompatActivity  {

    private ArrayList<Book> bookresults;
    String keywords;
    BookListAdapter adapter;
    RecyclerView recycler;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

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
            for (String keyword : keywords) {
                for(String word: book.getTitle().split(" ")) {
                    if(word.toLowerCase().equals(keyword.toLowerCase()) && book.isAvailable()) {
                        found[i] = true;
                        continue outside;
                    }
                }
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

    /* // if we want to search for users
    ArrayList<User> findUsers () {
        FindUser fuser = new FindUser();
        return fuser.findUserByName(keywords);
    }
    */

    protected void updateRecycler () {
        adapter = new BookListAdapter(SearchActivity.this, bookresults);
        recycler.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    /*
     * Takes Search string and handles results
     */

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
