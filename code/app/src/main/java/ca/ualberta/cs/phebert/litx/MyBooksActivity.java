package ca.ualberta.cs.phebert.litx;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

/**
 * MyBooksActivity displays the books this user owns, and allows the user to select a book to view
 * or add a new book
 * @author sdupasqu, plontke
 * @version 1.0
 * @see MainActivity, AddBookActivity, BookViewActivity, Book, BookListAdapter
 */
public class MyBooksActivity extends AppCompatActivity {

    private Button addNew;
    private Spinner mySpinner;
    private BookStatus filter;

    // Variables required to display books in the database
    private ArrayList<Book> filteredBooks = new ArrayList<Book>();
    BookListAdapter booksAdapter;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    /**
     * onCreate finds all the books in the database that the user owns and displays them in the
     * RecyclerView. It allows the user to add a new book to this list as well as select a book
     * to display
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_books);
        Book.getAll();
        mySpinner = (Spinner) findViewById(R.id.spinner);


        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(MyBooksActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        // Find the add new button
        addNew = (Button) findViewById(R.id.btnAddNew);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.spinnner_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mySpinner.setAdapter(adapter);
        mySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            // When an item is selected in the spinner the results in the RecyclerView should change
            // to show that
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        filter = BookStatus.all;
                        break;
                    case 1:
                        filter = BookStatus.available;
                        break;
                    case 2:
                        filter = BookStatus.requested;
                        break;
                    case 3:
                        filter = BookStatus.accepted;
                        break;
                    case 4:
                        filter = BookStatus.borrowed;
                        break;

                }
                filteredBooks = new ArrayList<>();
                query();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });





        addNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyBooksActivity.this, AddBookActivity.class);
                startActivity(intent);
            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();
        filteredBooks = new ArrayList<>();
        query();
    }

    /**
     * Query will be called after anything is selected in the spinner
     */
    public void query() {

        filteredBooks.clear();
        Log.d("LitX", Integer.toString(User.currentUser().getMyBooks().size()));
        Log.d("LitX","Querying myBooks");
        if(!User.isSignedIn()) return;
        //noinspection ConstantConditions
        for (Book book : User.currentUser().getMyBooks()) {
            Log.v("LitX",book.getTitle());
            Log.v("LitX", "filter is " + filter);
            if (filter == BookStatus.all) {
                filteredBooks.add(book);
            } else {
                if (book.getStatus() == filter) {
                    filteredBooks.add(book);
                }
            }
        }

        if(filteredBooks.size() > 0) {
            Log.d("LitX", filteredBooks.get(0).getTitle());
        }

        booksAdapter = new BookListAdapter(
                MyBooksActivity.this, filteredBooks, 0);
        recyclerView.setAdapter(booksAdapter);

//        booksAdapter.notifyDataSetChanged();

    }
}
