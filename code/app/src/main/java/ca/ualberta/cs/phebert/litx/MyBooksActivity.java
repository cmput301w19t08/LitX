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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

/**
 * MyBooksActivity displays the books this user owns, and allows the user to select a book to view
 * or add a new book
 * @author sdupasqu
 * @version 1.0
 * @see MainActivity, AddBookActivity, BookViewActivity, Book, BookListAdapter
 */
public class MyBooksActivity extends AppCompatActivity {

    private Button addNew;
    private Spinner mySpinner;
    private String filter;

    // Variables required to display books in the database
    private ArrayList<Book> myBooks = new ArrayList<Book>();
    BookListAdapter adapter;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;



    private FirebaseFirestore firestore;

    /**
     * onCreate finds all the books in the database that the user owns and displays them in the
     * RecyclerView. It allows the user to add a new book to this list as well as select a book
     * to display
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_books);
        mySpinner = (Spinner) findViewById(R.id.spinner);

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
                switch (position){
                    //
                    case 0: filter = "All";
                            break;
                    // Case 1 is available
                    case 1: filter = "Available";
                            break;
                    // case 2 is Requested
                    case 2: filter = "Requested";
                            break;
                    // Case 3 is Accepted
                    case 3: filter = "Accepted";
                            break;
                    // Case 4 is borrowed
                    case 4: filter = "Borrowed";
                            break;

                }
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

//        //Testing purposes only, following two lines need to be removed
//        User u = new User("John", "n", 123);
//        /*Book book = new Book(u.getUserName(), "Author", "Title", 1234567890);
//        myBooks.add(book);
//        Book b = new Book(u.getUserName(), "Me", "Fuck 301", 1234567899);
//        myBooks.add(b);*/

    /**
     * Query will be called after anything is selected in the spinner
     */
    public void query() {
            firestore = FirebaseFirestore.getInstance();
            final User u = new User("John", "n", 123);
            final ArrayList<Book> newBooks = new ArrayList<Book>();
            firestore.collection("Books").whereEqualTo("owner", u.getUserName()).get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Map<String, Object> temp = document.getData();
                                    Book book = new Book(temp.get("owner").toString(),
                                            temp.get("author").toString(),
                                            temp.get("title").toString(),
                                            Long.valueOf(temp.get("isbn").toString()));
                                    if (temp.get("acceptedRequest") != null){
                                        book.setStatus("accepted");
                                    } else {
                                        book.setStatus("available");
                                    }
// TODO: Need to figure out how to get the request back to set properly for looks
//TODO: Set all book information
//                                book.setPhotograph(temp.get("photograph").toString());
//                                book.setAcceptedRequest(temp.get("acceptedRequest").toString());
//                                book.setAvailable(temp.get("available").toString());
//                                book.setBorrower(temp.get("borrower").toString());
//                                book.setRequests(temp.get("requests").toString());

                                    if (filter.equals("All")) {
                                        newBooks.add(book);
                                    } else if (filter.equals("Available")) {
                                        boolean available = (boolean) temp.get("available");
                                        if (available) {
                                            newBooks.add(book);
                                        }
                                    } else if (filter.equals("Requested")) {
                                        if (temp.get("requests") != null) {
                                            newBooks.add(book);
                                        }
                                    } else if (filter.equals("Accepted")) {
                                        if (temp.get("acceptedRequest") != null) {
                                            newBooks.add(book);
                                    } else if (filter.equals("Borrowed")) {
                                        if (temp.get("borrower") != null) {
                                            newBooks.add(book);
                                            }
                                        }
                                    }
                                }

                                recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
                                recyclerView.setHasFixedSize(true);
                                layoutManager = new LinearLayoutManager(MyBooksActivity.this);
                                recyclerView.setLayoutManager(layoutManager);
                                BookListAdapter adapter = new BookListAdapter(
                                        MyBooksActivity.this, newBooks);
                                recyclerView.setAdapter(adapter);
                                adapter.notifyDataSetChanged();

                            }
                        }
                    });
        }


    }
}
