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
    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

    private Button btnOkay;

    // Database variables
    private FirebaseFirestore firestore;
    //private FirebaseAuth auth;

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
        String id = ""; // To determine the document id in Firestore

        // Get edittexts
        final EditText etTitle = (EditText) findViewById(R.id.editTitle);
        final EditText etAuthor = (EditText) findViewById(R.id.editAuthor);
        final EditText etISBN = (EditText) findViewById(R.id.editISBN);

        try {
            // If the book is being edited then get the document id to change in the future
            Intent intent = getIntent();
            final Book book = (Book) intent.getExtras().getSerializable("Book");
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
                    // Create a new book object with those fields
                    firestore = FirebaseFirestore.getInstance();
                    //TO DO: User should be the one using the app, not newly created user
                    User u = User.currentUser();
                    Book b = new Book(u, author, title, isbn);

                    //TODO: Authentication for the user adding a books
                    //TODO: Add book to owners list of books as well
                    // If document id exists overwrite the book in there, if it does not exist
                    // create a new document to store the book

                    // TODO put this portion into the book class, change to Map
                    if (docID.equals("")) {
                        // Create a new book and add it to firestore
                        firestore.collection("Books").document().set(b);
                    } else {
                        // Update the firestore document since the book already exists
                        firestore.collection("Books").document(docID).set(b);
                    }
                    // Go back to MyBooksActivity after the book has been added
                    Intent intent = new Intent(AddBookActivity.this, MyBooksActivity.class);
                    startActivity(intent);
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
                String title = data.getStringExtra("Title");
                String ISBN = data.getStringExtra("ISBN");
                ISBNView.setText(ISBN);
                titleView.setText(title);
                Log.d(TAG, title);
            } else {
                Log.w(TAG,"data is null");
            }
        }
    }

    public void scanISBN(View v) {
        Intent intent = new Intent(this, ScanBookActivity.class);
        startActivityForResult(intent,155);
    }
}
