package ca.ualberta.cs.phebert.litx;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

/**
 * AddBookActivity takes the entered information and if valid, creates a new book object from them
 * once the okay button gets clicked. The book then gets added to the database.
 * @author sdupasqu
 * @version 1.0
 * @see MyBooksActivity, ViewBookActivity, Book
 */
public class AddBookActivity extends AppCompatActivity {

    private Button btnOkay;

    // Database variables
    private FirebaseFirestore firestore;
    //private FirebaseAuth auth;

    /**
     * onCreate allows the user
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);

        String id = ""; // To determine the document id in Firestore

        final EditText etTitle = (EditText) findViewById(R.id.editTitle);
        final EditText etAuthor = (EditText) findViewById(R.id.editAuthor);
        final EditText etISBN = (EditText) findViewById(R.id.editISBN);

        try {
            Intent intent = getIntent();
            final Book book = (Book) intent.getExtras().getSerializable("Book");
            id = book.getDocID();

            //Fill the edit text boxes with the book information
            etTitle.setText(book.getTitle());
            etAuthor.setText(book.getAuthor());
            etISBN.setText(String.valueOf(book.getIsbn()));

        } catch (Exception e) {}

        final String docID = id;
        Log.d("New", id);
        btnOkay = (Button) findViewById(R.id.btnOkay);

        btnOkay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = etTitle.getText().toString();
                String author = etAuthor.getText().toString();
                try {
                    long isbn = Long.valueOf(etISBN.getText().toString());

                    if (title.equals("") || author.equals("") ||
                            (String.valueOf(isbn).length() != 13 && String.valueOf(isbn).length() != 10)) {
                        throw new Exception("Invalid fields");
                    }

                    firestore = FirebaseFirestore.getInstance();
                    User u = new User("John", "n", 123);
                    //TODO: User should be the one using the app, not newly created user
                    Book b = new Book(u.getUserName(), author, title, isbn);

                    //TODO: Authentication for the user adding a books
                    //TODO: Add book to owners list of books as well
                    if (docID.equals("")) {
                        // Create a new book and add it to firestore
                        firestore.collection("Books").document().set(b);
                    } else {
                        // Update the firestore document since the book already exists
                        firestore.collection("Books").document(docID).set(b);
                    }

                    Intent intent = new Intent(AddBookActivity.this, MyBooksActivity.class);
                    startActivity(intent);
                }
                catch(Exception e) {}
            }
        });
    }
}
