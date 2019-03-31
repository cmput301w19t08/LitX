package ca.ualberta.cs.phebert.litx;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Map;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public static final String FilterMode = "ca.ualberta.cs.phebert.litx.FilterMode";
    private RecyclerView recyclerView;
    private ArrayList<Book> books;
    private ArrayList<Book> booksToShow;
    private TopTenAdapter adapter;
    private RecyclerView.LayoutManager manager;
    static Thread loader;

    void getAllData() {
        Request.getAll(); // this should be enough if requests weren't empty
        User.getAll();
        Book.getAll();
        Request.getAll();
        Intent serviceIntent = new Intent(this, NotificationIntentService.class);
        serviceIntent.putExtra("NOTIFICATION_INTENT_SERVICE", "MainActivity");
        startService(serviceIntent);
        // this should be enough if requests weren't empty\
        Map<String, Request>  db =  Request.getAll(); // this should be enough if requests weren't empty
        for (Map.Entry<String, Request> entry : db.entrySet()) {
            Log.v("LitX.REQUEST", entry.getValue().getRequester().getUserName());
            if (entry.getValue().getRequester().getUserName().equals(User.currentUser().getUserName())) {
                if (entry.getValue().getRequestSeen() == Boolean.FALSE) {
                    Request request = entry.getValue();
                    request.generateNotification(this);
                    request.setRequestSeen(Boolean.TRUE);
                    request.toMap();
                    request.selfPush();
                }
            }
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Request.createNotificationChannel(this);

        if(!User.isSignedIn()) {
            goToProfileView(null);
        }
        recyclerView = findViewById(R.id.top10list_home);
        manager = new LinearLayoutManager(MainActivity.this);
        recyclerView.setHasFixedSize(true);
        books = new ArrayList<>();
        booksToShow = new ArrayList<>(3);
        books.addAll(Book.getAll().values());

        Log.i("ARRAYLIST SIZE bookstoShow", Integer.toString(booksToShow.size()));

        Log.i("ARRAYLIST SIZE books", Integer.toString(books.size()));

        int index,i;
        Book comparisonBook;
        for (int j = 0; j < 10; j++) {
            comparisonBook = books.get(0);
            index = 0;
            for (i = 0; i < books.size(); i++) {
                if (comparisonBook.getBorrows() <= books.get(i).getBorrows()) {
                    Log.i("ARRABook author is ", books.get(i).getAuthor());
                    if (doesNotAlreadyContain(booksToShow, books.get(i))==1) {
                        comparisonBook = books.get(i);
                        index = i;
                    }
                }
            }
            booksToShow.add(comparisonBook);
            Log.i("ARRAYLIST SIZE bookstoShow", Integer.toString(booksToShow.size()));




        }
        adapter = new TopTenAdapter(this, booksToShow);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(manager);
    }

    public int doesNotAlreadyContain(ArrayList<Book> bookList, Book currentBook) {
        for (int r = 0; r < bookList.size(); r++) {
            if (bookList.get(r).getAuthor().equals(currentBook.getAuthor())) {
                return 0;
            }
        }
        return 1;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (User.isSignedIn()) {
            Log.i("LitX Thread", "thread is running loader");
            loader = new Thread(this::getAllData);
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

    @Override
    protected void onStop() {
       /* for(Request request :Request.getAll().values()) {
            request.generateNotification(this);
            break;
        }*/
        super.onStop();
    }
}
