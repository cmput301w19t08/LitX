package ca.ualberta.cs.phebert.litx;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * AddBookActivity takes the entered information and if valid, creates a new book object from them
 * once the okay button gets clicked. The book then gets added to the database.
 * @author sdupasqu
 * @version 1.0
 * @see MyBooksActivity, ViewBookActivity, Book
 */
public class AddBookActivity extends AppCompatActivity {
    EditText titleView;
    EditText ISBNView;
    EditText authorView;
    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

    private Button btnOkay;

    /**
     * onCreate automatically fills in the book information if it is being edited, otherwise
     * it leaves the fields blank and the user can provide new information. From this activity,
     * the user can scan the ISBN of a book to auto-fill the fields. Once the okay button is clicked
     * a new book gets added to the database
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);
        titleView=(EditText)findViewById(R.id.editTitle);
        ISBNView=(EditText)findViewById(R.id.editISBN);
        authorView=(EditText)findViewById(R.id.editAuthor);
        String id = ""; // To determine the document id in Firestore

        // Get edittexts
        final EditText etTitle = (EditText) findViewById(R.id.editTitle);
        final EditText etAuthor = (EditText) findViewById(R.id.editAuthor);
        final EditText etISBN = (EditText) findViewById(R.id.editISBN);

        try {
            // If the book is being edited then get the document id to change in the future
            Intent intent = getIntent();
            final Book book = Book.findByDocId(intent.getExtras().getString("Book"));
            id = book.getDocID();

            //Fill the edit text boxes with the book information
            etTitle.setText(book.getTitle());
            etAuthor.setText(book.getAuthor());
            etISBN.setText(String.valueOf(book.getIsbn()));

        } catch (Exception e) {}

        // Make the document id final so the onClickListener can use it
        final String docID = id;
        btnOkay = (Button) findViewById(R.id.btnOkay);

        // When okay button is clicked do the following
        btnOkay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: Check input fields are valid, then create book object and pass it back
                EditText etTitle = (EditText) findViewById(R.id.editTitle);
                EditText etAuthor = (EditText) findViewById(R.id.editAuthor);
                EditText etISBN = (EditText) findViewById(R.id.editISBN);

                String title = etTitle.getText().toString();
                String author = etAuthor.getText().toString();
                try {
                    // Make sure isbn can be of type long
                    long isbn = Long.valueOf(etISBN.getText().toString());

                    // Ensure the fields have actual values
                    if (title.equals("") || author.equals("") ||
                            (String.valueOf(isbn).length() != 13 && String.valueOf(isbn).length() != 10)) {
                        throw new Exception("Invalid fields");
                    }

                    if(!User.isSignedIn()) return;
                    //TO DO: User should be the one using the app, not newly created user
                    User u = User.currentUser();
                    Book b = new Book(u, author, title, isbn);

                    b.push();
                    // Go back to MyBooksActivity after the book has been added
                    finish();
                }
                catch(Exception e) {
                    Log.d("LitX", "fields not set properly", e);
                } // If fields are invalid do nothing
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String TAG = "scan";
        //titleView.setText("" + resultCode);
        if(requestCode == 155) {
            if(data != null) {
                //try {

                    String title = data.getStringExtra("Title");
                    String ISBN = data.getStringExtra("ISBN");
                    String author = data.getStringExtra("Author");
                    ISBNView.setText(ISBN);
                    titleView.setText(title);
                    authorView.setText(author);
                //} catch (Exception e){
                    //If the book is not in the WorldLibrary database print only the ISBN
                //    String ISBN = data.getStringExtra("ISBN");
                 //   ISBNView.setText(ISBN);
                //}

            } else {
                Log.w(TAG,"data is null");
            }
        }
    }
    public void scanISBN(View v) {
        Intent intent = new Intent(this, ScanBookActivity.class);
        startActivityForResult(intent,155);

        //This is how the Map Object is to be used!
        /*
        try {
            Intent intent = new Intent(this, MapObject.class);
            //double RichardISBN=Double.valueOf(ISBNView.getText().toString());
            double RichardISBN=50;
            intent.putExtra("LONG",-97.432404);
            intent.putExtra("LAT",RichardISBN);
            intent.putExtra("MOVABLE",Boolean.TRUE);
            startActivity(intent);



        } catch (Exception e) {}
        */





    }
}