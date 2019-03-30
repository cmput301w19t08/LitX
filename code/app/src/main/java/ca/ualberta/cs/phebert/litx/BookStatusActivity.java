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


public class BookStatusActivity extends AppCompatActivity {
    public static final String FilterMode = "ca.ualberta.cs.phebert.litx.FilterMode";
    private ArrayList<Book> filteredBooks = new ArrayList<Book>();

    private BookListAdapter booksAdapter;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private int filter;
    private TextView message;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_status);
        Intent intent = getIntent();
        int filter = intent.getIntExtra(FilterMode, 0);
        if (filter == 0){
            // TODO Show the requested that arent accepted
        } else if (filter == 1) {
            //TODO Show the Books that have been accpeted
        } else{
            // TODO Show the books the user is borrowing
        }

        recyclerView = (RecyclerView) findViewById(R.id.status_recycler_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(BookStatusActivity.this);
        recyclerView.setLayoutManager(layoutManager);


        Intent intent = getIntent();
        int filter = intent.getIntExtra(MainActivity.FilterMode, 0);
        message = (TextView) findViewById(R.id.statusMessage);
        if (filter == 0){
            message.setText("Pending Requests");
            this.filter = filter;
        } else if (filter == 1){
            message.setText("Accepted Requests");
            this.filter = filter;
        }else {
            message.setText("Borrowed Books");
            this.filter = filter;
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
        if (this.filter == 0 ) {
            for (Request request : User.currentUser().getRequests()){
                if (request.getStatus() == RequestStatus.Pending) {
                    filteredBooks.add(request.getBook());
                }
            }

        }else if (this.filter == 1 ){
            for (Request request : User.currentUser().getRequests()){
                if (request.getStatus() == RequestStatus.Accepted && (request.getBook().getStatus()
                        != BookStatus.borrowed)){
                    filteredBooks.add(request.getBook());
                }
            }
        }else {
            for (Request request : User.currentUser().getRequests()){
                if (request.getBook().getStatus() == BookStatus.borrowed){
                    filteredBooks.add(request.getBook());
                }
            }

        }
        booksAdapter = new BookListAdapter(
                BookStatusActivity.this, filteredBooks);
        recyclerView.setAdapter(booksAdapter);

        booksAdapter.notifyDataSetChanged();
    }
}
