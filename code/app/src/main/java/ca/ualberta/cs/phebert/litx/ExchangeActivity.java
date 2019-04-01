package ca.ualberta.cs.phebert.litx;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class ExchangeActivity extends AppCompatActivity {


    private Button owner;
    private Button borrower;
    private Button handOver;
    private Button recieve;
    int HAND_OVER_OWNER = 2;
    int HAND_OVER_BORROWER = 3;
    int RECIEVE = 1;
    String isbn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exchange);
        handOver = (Button) findViewById(R.id.hand_over_book);
        recieve = (Button) findViewById(R.id.recieve_book);
        borrower = (Button) findViewById(R.id.pick_borrower);
        owner = (Button) findViewById(R.id.pick_owner);
        borrower.setVisibility(View.GONE);
        owner.setVisibility(View.GONE);

        Intent intent = new Intent(ExchangeActivity.this, ScanBookActivity.class);

        handOver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handOver.setVisibility(View.GONE);
                recieve.setVisibility(View.GONE);
                borrower.setVisibility(View.VISIBLE);
                owner.setVisibility(View.VISIBLE);

                Toast.makeText(ExchangeActivity.this,
                        "Please Select if You are a Borrower or Owner", Toast.LENGTH_LONG)
                        .show();
            }
        });
        recieve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(intent, RECIEVE);
            }
        });

        owner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(intent, HAND_OVER_OWNER);
            }
        });

        borrower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(intent, HAND_OVER_BORROWER);
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        Boolean found = false;
        Log.d("LitxExchange", Integer.toString(requestCode));
        if (requestCode == RECIEVE && resultCode == RESULT_OK){
            isbn = data.getStringExtra("ISBN");
            for(Book book : User.currentUser().getMyBooks()) {
                Log.d("LitxExchange", book.getStatus().toString());
                if (Long.toString(book.getIsbn()).equals(isbn) && book.getStatus() ==
                        BookStatus.returned ){
                    book.setStatus("available");
                    book.push();
                    found = true;
                    Toast.makeText(ExchangeActivity.this, "Book Has Been Set to Available",
                            Toast.LENGTH_SHORT).show();
                }

            }
            if (!found){
                Toast.makeText(ExchangeActivity.this,
                        "The Scanned ISBN did not Match any Receivable Books",
                        Toast.LENGTH_LONG).show();
            }
            // Accepted --> owner is handing over. Borrowed --> borrower is handing over
        } else if (requestCode == HAND_OVER_BORROWER && resultCode == RESULT_OK){

            isbn = data.getStringExtra("ISBN");
            for (Request request : User.currentUser().getRequests()){
//                Log.d("LitxExchange", request.getBook().getAcceptedRequest().getRequester().getUserid());
                if(request.getStatus() == RequestStatus.Accepted &&
                        Long.toString(request.getBook().getIsbn()).equals(isbn)){
                    found = true;
                    request.getBook().setStatus("returned");
                    request.getBook().push();
                    Toast.makeText(ExchangeActivity.this, "Book Has Been Returned",
                            Toast.LENGTH_LONG).show();
                }
            }
            if (!found){
                Toast.makeText(ExchangeActivity.this,
                        "The Scanned ISBN Does Not Match Any Returnable Books",
                        Toast.LENGTH_LONG).show();
            }

        } else {
            isbn = data.getStringExtra("ISBN");
            Log.d("LitxExchange", "MyBooks Size" + Integer.toString(User.currentUser().getMyBooks().size()));
            for (Book book : User.currentUser().getMyBooks()){
                Log.d("LitxExchange", book.getTitle());
                Log.d("LitxExchange", book.getStatus().toString());
                if (Long.toString(book.getIsbn()).equals(isbn) &&
                        book.getStatus() == BookStatus.accepted){
                    found = true;
                    book.setStatus("borrowed");
                    book.push();
                    Toast.makeText(ExchangeActivity.this,
                            "Book has Been handed over to be borrowed", Toast.LENGTH_LONG)
                            .show();
                }
            }
            if (!found) {
                Toast.makeText(ExchangeActivity.this,
                        "Scanned ISBN Did Not Match Any Borrowed Books", Toast.LENGTH_LONG)
                        .show();
            }

        }
    }
}
