package ca.ualberta.cs.phebert.litx;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListAdapter;

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

        // Find the add new button
        addNew = (Button) findViewById(R.id.btnAddNew);

        //Testing purposes only, following line needs to be removed
        User u = new User("John", "n", 123);
        firestore = FirebaseFirestore.getInstance();

        // Get the collection of books that this user owns
        //TODO: Load myBooks from owners list instead of querying every time
        firestore.collection("Books").whereEqualTo("owner", u.getUserName()).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                /* Get all requirements for one book object, then create that
                                 * object and set all variables to the required things
                                 */
                                Map<String, Object> temp = document.getData();
                                Book book = new Book(temp.get("owner").toString(), temp.get("author").toString(),
                                        temp.get("title").toString(), Long.valueOf(temp.get("isbn").toString()));

                                //TODO: Set all book information
                                /*book.setPhotograph(temp.get("photograph").toString());
                                book.setAcceptedRequest(temp.get("acceptedRequest").toString());
                                book.setAvailable(temp.get("available").toString());
                                book.setBorrower(temp.get("borrower").toString());
                                book.setRequests(temp.get("requests").toString());*/
                                book.setDocID(document.getId());

                                myBooks.add(book);
                            }

                            /* Once all books have been added to the list, set the adapter and
                             * display the array in the RecyclerView
                             */
                            recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
                            recyclerView.setHasFixedSize(true);
                            layoutManager = new LinearLayoutManager(MyBooksActivity.this);
                            recyclerView.setLayoutManager(layoutManager);
                            BookListAdapter adapter = new BookListAdapter(MyBooksActivity.this, myBooks);
                            recyclerView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        }
                    }
                });

        // When Add New button is clicked, got to the addBookActivity class to add a new book
        addNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyBooksActivity.this, AddBookActivity.class);
                startActivity(intent);
            }
        });
    }
}
