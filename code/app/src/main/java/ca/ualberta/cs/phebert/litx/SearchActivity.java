package ca.ualberta.cs.phebert.litx;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firestore.v1.StructuredQuery;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity  {

    private ArrayList<Book> bookresults;
    String keywords;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_view);
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


    /*ArrayList<User> findUser (ArrayList<User> userlist, String username, boolean isexact) {
        if(isexact) {

        }
    }*/

    /*
     * Takes Search string and handles results
     */
    public void searchPress (View v) {
        keywords = ((EditText)findViewById(R.id.input_search)).getText().toString();
        //FirebaseFirestore.getInstance().collection("User").get()
        FirebaseFirestore.getInstance().collection("Books").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(DocumentSnapshot ds : queryDocumentSnapshots.getDocuments()){
                            bookresults.add(new Book(new User(),
                                    ds.getString("author"),
                                    ds.getString("title"),
                                    ds.getLong("isbn")));

                            if(ds.getBoolean("available")) {
                                bookresults.get(bookresults.size()-1).setAcceptedRequest(null);
                            } else {
                                //bookresults.get(bookresults.size()-1).setAcceptedRequest();
                            }
                        }
                        bookresults = findBook(bookresults, keywords.split(" "));
                    }
                });
    }
}
