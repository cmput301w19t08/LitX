package ca.ualberta.cs.phebert.litx;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;

import ca.ualberta.cs.phebert.litx.Models.Book;
import ca.ualberta.cs.phebert.litx.Models.Request;
import ca.ualberta.cs.phebert.litx.Models.User;
import ca.ualberta.cs.phebert.litx.Observers.BookObserver;


public class BookStatusActivity extends AppCompatActivity implements BookObserver {
    public static final String FilterMode = "ca.ualberta.cs.phebert.litx.FilterMode";
    private ArrayList<Book> filteredBooks = new ArrayList<Book>();

    private BookListAdapter booksAdapter;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private BookStatus filter;
    private TextView message;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_status);
        Intent intent = getIntent();

        recyclerView = (RecyclerView) findViewById(R.id.status_recycler_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(BookStatusActivity.this);
        recyclerView.setLayoutManager(layoutManager);

        try {
            filter = BookStatus.valueOf(intent.getStringExtra(MainActivity.FilterMode));
        } catch(IllegalArgumentException e) {
            Log.e("Litx","bad intent",e);
            filter = BookStatus.available;
        }
        message = (TextView) findViewById(R.id.statusMessage);
        if (filter == BookStatus.requested){
            message.setText(getString(R.string.pending_requests));
        } else if (filter == BookStatus.accepted){
            message.setText(getString(R.string.AcceptedRequests));
        }else if( filter != BookStatus.available){
            message.setText(getString(R.string.Borrowed_Books));
        }



        query();

    }
    @Override
    public void onStart(){
        super.onStart();
        query();
    }

    public void query(){
        filteredBooks.clear();
        Log.d("LitX","Querying myRequests");
        if(!User.isSignedIn()) return;
        //noinspection ConstantConditions
        for (Book book : Book.getAll().values()){
            if (book.getStatus() == filter) {
                filteredBooks.add(book);
            }
        }

        booksAdapter = new BookListAdapter(
                BookStatusActivity.this, filteredBooks, 1);
        recyclerView.setAdapter(booksAdapter);

        booksAdapter.notifyDataSetChanged();
    }

    @Override
    public void onUpdate(Book book) {
        if(book.getStatus() == filter) {
            if(!filteredBooks.contains(book)) {
                filteredBooks.add(book);
            }
        }
        booksAdapter.notifyDataSetChanged();
    }
}
