package ca.ualberta.cs.phebert.litx;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import ca.ualberta.cs.phebert.litx.R;

/**
 * Displays all books the user has of the status they selected (Requested, Accepted, Borrowed)
 * @author plontke
 * @version 1.0
 * @see MainActivity
 * @see MapActivity
 * @see BookViewActivity
 * @see Book
 */
public class BookStatusActivity extends AppCompatActivity {
    public static final String FilterMode = "ca.ualberta.cs.phebert.litx.FilterMode";
    private ArrayList<Book> filteredBooks = new ArrayList<Book>();

    private BookListAdapter booksAdapter;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private int filter;
    private TextView message;

    /**
     * On Create method for the BookStatus Activity
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_status);
        Intent intent = getIntent();

        recyclerView = (RecyclerView) findViewById(R.id.status_recycler_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(BookStatusActivity.this);
        recyclerView.setLayoutManager(layoutManager);

        filter = intent.getIntExtra(MainActivity.FilterMode, 0);
        message = (TextView) findViewById(R.id.statusMessage);
        if (filter == 0){
            message.setText(getString(R.string.pending_requests));

        } else if (filter == 1){
            message.setText(getString(R.string.AcceptedRequests));

        }else {
            message.setText(getString(R.string.Borrowed_Books));
        }
        query();
    }

    /**
     * onStart method for the BookStatusActivity
     * query to find the data when the activity starts
     */
    @Override
    public void onStart(){
        super.onStart();
        query();
    }

    /**
     * queries the requests of the current user and sets filteredBooks to the
     *  appropriate requests depending on the filter
     */
    public void query(){
        filteredBooks.clear();
        Log.d("LitX","Querying myRequests");
        if(!User.isSignedIn()) return;
        //noinspection ConstantConditions
        if (this.filter == 0 ) {
            for (Request request : User.currentUser().getRequests()){
                if (request.getStatus() == RequestStatus.Pending) {
                    filteredBooks.add(request.getBook());
                }
                booksAdapter = new BookListAdapter(
                        BookStatusActivity.this, filteredBooks, 0);
            }

        }else if (this.filter == 1 ){
            for (Request request : User.currentUser().getRequests()){
                if (request.getStatus() == RequestStatus.Accepted && (request.getBook().getStatus()
                        != BookStatus.borrowed)){
                    filteredBooks.add(request.getBook());
                }
                booksAdapter = new BookListAdapter(
                        BookStatusActivity.this, filteredBooks, 1);
            }
        } else {
            for (Request request : User.currentUser().getRequests()){
                if (request.getBook().getStatus() == BookStatus.borrowed){
                    filteredBooks.add(request.getBook());
                }
                booksAdapter = new BookListAdapter(
                        BookStatusActivity.this, filteredBooks, 1);
            }
        }

        recyclerView.setAdapter(booksAdapter);
        booksAdapter.notifyDataSetChanged();
    }
}
