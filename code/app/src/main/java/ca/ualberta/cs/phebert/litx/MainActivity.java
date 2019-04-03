package ca.ualberta.cs.phebert.litx;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.storage.FirebaseStorage;

import java.util.Map;
import java.util.ArrayList;

import static com.google.common.primitives.UnsignedInts.min;

/**
 * Main screen of the app, lots of navigation done from here, displays the top 10 books
 * @author sdupasqu, plontke, zkist, phebert
 * @version 1.0
 * @see ProfileActivity
 * @see MyBooksActivity
 * @see BookStatusActivity
 * @see SearchActivity
 * @see ExchangeActivity
 * @see Book
 * @see User
 * @see TopTenAdapter
 */
public class MainActivity extends AppCompatActivity {
    public static final String FilterMode = "ca.ualberta.cs.phebert.litx.FilterMode";
    private RecyclerView recyclerView;
    private ArrayList<Book> books;
    private ArrayList<Book> booksToShow;
    private TopTenAdapter adapter;
    private RecyclerView.LayoutManager manager;
    static Thread loader;

    /**
     * Loads all data from the database
     */
    void getAllData() {
        FirebaseFirestore.getInstance().enableNetwork();
        Request.getAll(); // this should be enough if requests weren't empty
        User.getAll();
        Book.getAll();
        Intent serviceIntent = new Intent(this, NotificationIntentService.class);
        serviceIntent.putExtra("NOTIFICATION_INTENT_SERVICE", "MainActivity");
        startService(serviceIntent);

    }

    /**
     * Either loads the top 10 books or if it is a new user makes them sign in
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Request.createNotificationChannel(this);

        if (!User.isSignedIn()) {
            goToProfileView(null);
        } else {
            top10Generate();
        }
    }

    /**
     * Populates the top10 Adapter
     */
    public void top10Generate() {
        int i, topNumber;
        Book comparisonBook;
        recyclerView = findViewById(R.id.top10list_home);
        manager = new LinearLayoutManager(MainActivity.this);
        recyclerView.setHasFixedSize(true);
        books = new ArrayList<>();
        booksToShow = new ArrayList<>(3);
        books.addAll(Book.getAll().values());
        topNumber = min(10, books.size());

        for (int j = 0; j < topNumber; j++) {
            comparisonBook = books.get(j);
            for (i = 0; i < books.size(); i++) {
                if (comparisonBook.getBorrows() <= books.get(i).getBorrows()) {
                    if (doesNotAlreadyContain(booksToShow, books.get(i)) == Boolean.FALSE) {
                        comparisonBook = books.get(i);
                    }
                }
            }
            booksToShow.add(comparisonBook);
        }
        adapter = new TopTenAdapter(this, booksToShow);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(manager);
    }

    /**
     * Ensures the bookList does not already contain the currentBook
     * @param bookList list of books to check
     * @param currentBook book to try to find in the bookList
     * @return Boolean is the book in the list or not
     */
    public Boolean doesNotAlreadyContain(ArrayList<Book> bookList, Book currentBook) {
        for (int r = 0; r < bookList.size(); r++) {
            if (bookList.get(r).getTitle().equals(currentBook.getTitle())) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }

    /**
     * Load all data for the user
     */
    @Override
    public void onStart() {
        super.onStart();
        if (User.isSignedIn()) {
            Log.i("LitX Thread", "thread is running loader");
            loader = new Thread(this::getAllData);
            loader.start();
        }
    }

    /**
     * Depending on which button was clicked from Requested, Accepted, and Borrowing pass in the
     * filter so the status activity knows which status to get
     * @param v Used to determine which button was pressed
     */
    public void filter(View v) {
        Intent intent = new Intent(this, BookStatusActivity.class);
        if(v.getId() == R.id.requests_home) {
            intent.putExtra(FilterMode,0);
        } else if(v.getId() == R.id.accept_home) {
            intent.putExtra(FilterMode,1);
        } else { // borrowed
            intent.putExtra(FilterMode,2);
        }
        startActivity(intent);
    }

    /**
     * Starts the profile activity
     * @param v view used for button press
     */
    public void goToProfileView(View v) {
        Intent intent = new Intent(this,ProfileActivity.class);
        startActivity(intent);
    }

    /**
     * Starts my books activity
     * @param v view used for button press
     */
    public void getMyBooks(View v) {
        // Should not be BookViewActivity, need a new activity for MyBooks
        Intent intent = new Intent(this, MyBooksActivity.class);
        startActivity(intent);
    }

    // No method named this in UML, must be added

    /**
     * Starts the exchange activity
     * @param v view used for button press
     */
    public void exchangeBook(View v) {
        Intent intent = new Intent(this, ExchangeActivity.class);
        startActivity(intent);
    }

    /**
     * Starts the search activity
     * @param v view used for button press
     */
    public void searchForBooks(View v) {
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
    }
}
