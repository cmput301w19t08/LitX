package ca.ualberta.cs.phebert.litx;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * @Author plontke
 * @version 1
 *
 */
public class ExchangeActivity extends AppCompatActivity {


    private Button owner;
    private Button borrower;
    private Button handOver;
    private Button recieve;
    int HAND_OVER_OWNER = 2;
    int HAND_OVER_BORROWER = 3;
    int RECIEVE_BORROWER = 0;
    int RECIEVE_OWNER = 1;
    String isbn;
    Boolean isOwner;

    /**
     * onCreate for Exchange Activity
     * @param savedInstanceState
     */
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


        handOver.setVisibility(View.GONE);
        recieve.setVisibility(View.GONE);
        borrower.setVisibility(View.VISIBLE);
        owner.setVisibility(View.VISIBLE);
        Toast.makeText(ExchangeActivity.this,
                "Please Select if You Are the Owner or Borrower of the Book", Toast.LENGTH_LONG).show();


        owner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isOwner = true;
                pickType();
            }
        });

        borrower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isOwner = false;
                pickType();
            }
        });

    }

    /**
     * This is a function that is called in the onClick Listeners for each of the buttons
     * Depending on what was picked it will start the ScanActivity for result using the proper request
     * Code
     */
    public void pickType(){
        Intent intent = new Intent(ExchangeActivity.this, ScanBookActivity.class);
        handOver.setVisibility(View.VISIBLE);
        recieve.setVisibility(View.VISIBLE);
        borrower.setVisibility(View.GONE);
        owner.setVisibility(View.GONE);
        Toast.makeText(ExchangeActivity.this,
                "Select If You Are Receving or Handing a Book Over", Toast.LENGTH_SHORT).show();

        handOver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOwner) {
                    startActivityForResult(intent, HAND_OVER_OWNER);
                } else {
                    startActivityForResult(intent, HAND_OVER_BORROWER);
                }
            }
        });

        recieve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOwner) {
                    startActivityForResult(intent, RECIEVE_OWNER);
                } else {
                    startActivityForResult(intent, RECIEVE_BORROWER);
                }
            }
        });
    }

    /**
     * After scanning the Isbn this checks to see if the returned ISBN is compatible with the
     * what the user picked
     * @param requestCode int, recieved from scan, 0 RECEIVE_BORROWER, 1 RECEIVE_OWNER,
     *  2 HAND_OVER_OWNER, 3 HAND_OVER_BORROWER
     * @param resultCode int, if the activity result was ok
     * @param data Intent, carries the data from Scan Activity
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        Boolean found = false;
        owner.setVisibility(View.VISIBLE);
        borrower.setVisibility(View.VISIBLE);
        handOver.setVisibility(View.GONE);
        recieve.setVisibility(View.GONE);
        Log.d("LitxExchange", Integer.toString(requestCode));
        if (requestCode == RECIEVE_OWNER && resultCode == RESULT_OK){
            isbn = data.getStringExtra("ISBN");
            for(Book book : User.currentUser().getMyBooks()) {
                Log.d("LitxExchange", book.getStatus().toString());
                if (Long.toString(book.getIsbn()).equals(isbn) && book.getStatus() ==
                        BookStatus.pending ){
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
                    request.getBook().setStatus("pending");
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

        } else if (requestCode == HAND_OVER_OWNER && resultCode == RESULT_OK){
            isbn = data.getStringExtra("ISBN");
            Log.d("LitxExchange", "MyBooks Size" +
                    Integer.toString(User.currentUser().getMyBooks().size()));
            for (Book book : User.currentUser().getMyBooks()){
                Log.d("LitxExchange", book.getTitle());
                Log.d("LitxExchange", book.getStatus().toString());
                if (Long.toString(book.getIsbn()).equals(isbn) &&
                        book.getStatus() == BookStatus.accepted){
                    found = true;
                    book.setStatus("pending");
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

        } else if (requestCode == RECIEVE_BORROWER && resultCode == RESULT_OK){
            isbn = data.getStringExtra("ISBN");
            for (Request request : User.currentUser().getRequests()){
                if (Long.toString(request.getBook().getIsbn()).equals(isbn) &&
                        request.getBook().getStatus() == BookStatus.pending){
                    found = true;
                    request.getBook().setStatus("borrowed");
                    request.getBook().push();
                    Toast.makeText(ExchangeActivity.this, "Book has Been Recieved",
                            Toast.LENGTH_LONG).show();
                }
            }
            if (!found) {
                Toast.makeText(ExchangeActivity.this,
                        "The scanned ISBN Did Not Match Any Requested Books",
                        Toast.LENGTH_LONG).show();
            }
        }else {
            Toast.makeText(ExchangeActivity.this, "Error Scanning Book",
                    Toast.LENGTH_SHORT).show();
        }
    }
}
