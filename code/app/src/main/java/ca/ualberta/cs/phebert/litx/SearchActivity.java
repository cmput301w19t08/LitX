package ca.ualberta.cs.phebert.litx;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firestore.v1.StructuredQuery;

import java.util.ArrayList;

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
    }

    /**
     * Finds list of Books who match all keywords (separated by " ") to the search input
     * @param Original Full list of books on the Database
     * @param keywords List of words that the query must match
     * @return The list of selected book that match all keywords
     */
    ArrayList<Book> findBook(ArrayList<Book> Original, String... keywords) {
        boolean[] found = new boolean[Original.size()];
        outside: for(int i = 0; i < Original.size(); i++) {
            Book book = Original.get(i);
            for (String keyword : keywords) {
                for(String word: book.getTitle().split(" ")) {
                    if(word.equals(keyword)) {
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


    ArrayList<User> findUsers () {
        FindUser fuser = new FindUser("username", keywords);
        return fuser.getResults();
    }

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
        bookresults.clear();
        keywords = ((EditText)findViewById(R.id.input_search)).getText().toString();
        //FirebaseFirestore.getInstance().collection("User").get()
        FirebaseFirestore.getInstance().collection("Books").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(DocumentSnapshot ds : queryDocumentSnapshots.getDocuments()){
                            if(FirebaseAuth.getInstance().getCurrentUser() == null) return;
                            bookresults.add(new Book(ds.getString("owner"),
                                    ds.getString("author"),
                                    ds.getString("title"),
                                    ds.getLong("isbn"),
                                    ds.getString("ownerUid")));
                            bookresults.get(bookresults.size()-1).setStatus(ds.getString("status"));
                        }
                        bookresults = findBook(bookresults, keywords.split(" "));
                        updateRecycler();
                    }
                });
    }
}
