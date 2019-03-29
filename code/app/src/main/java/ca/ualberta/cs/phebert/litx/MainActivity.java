package ca.ualberta.cs.phebert.litx;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public static final String FilterMode = "ca.ualberta.cs.phebert.litx.FilterMode";
    private RecyclerView recyclerView;
    private ArrayList<Book> books;
    private ArrayList<Book> booksToShow;
    private TopTenAdapter adapter;
    private RecyclerView.LayoutManager manager;

    void getAllData() {
        Request.getAll(); // this should be enough if requests weren't empty
        Book.getAll();
        User.getAll();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(!User.isSignedIn()) {
            goToProfileView(null);
        }
        recyclerView = findViewById(R.id.top10list_home);
        books = new ArrayList<>();
        booksToShow = new ArrayList<>();
        manager = new LinearLayoutManager(MainActivity.this);
        recyclerView.setHasFixedSize(true);
        books.addAll(Book.getAll().values());
        int compareValue = 0;
        for (Book book : books) {
            if ((book.getViews() >= compareValue) && (booksToShow.size()<=2)) {
                booksToShow.add(book);
            }
        }


        adapter = new TopTenAdapter(this, booksToShow);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(manager);



    }
    @Override
    public void onStart() {
        super.onStart();
        if (User.isSignedIn()) {
            Thread loader = new Thread(this::getAllData);
            loader.start();
        }
    }

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

    public void goToProfileView(View v) {
        Intent intent = new Intent(this,ProfileActivity.class);
        startActivity(intent);
    }

    public void getMyBooks(View v) {
        // Should not be BookViewActivity, need a new activity for MyBooks
        Intent intent = new Intent(this, MyBooksActivity.class);
        startActivity(intent);
    }

    // No method named this in UML, must be added
    public void exchangeBook(View v) {
        Intent intent = new Intent(this, ExchangeActivity.class);
        startActivity(intent);
    }

    public void searchForBooks(View v) {
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
    }
}
