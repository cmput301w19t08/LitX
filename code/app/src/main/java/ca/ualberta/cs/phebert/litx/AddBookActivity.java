package ca.ualberta.cs.phebert.litx;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class AddBookActivity extends AppCompatActivity {

    private Button btnOkay;

    // Database variables
    private FirebaseFirestore firestore;
    //private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);

        btnOkay = (Button) findViewById(R.id.btnOkay);

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
                    long isbn = Long.valueOf(etISBN.getText().toString());

                    if (title.equals("") || author.equals("") ||
                            (String.valueOf(isbn).length() != 13 && String.valueOf(isbn).length() != 10)) {
                        throw new Exception("Invalid fields");
                    }

                    //TODO: User should be the one using the app, not newly created user
                    User u = new User("John", "n", 123);
                    Book book = new Book(u.getUserName(), title, author, isbn);

                    // Write to database
                    firestore = FirebaseFirestore.getInstance();
                    /*Map<String, String> bookMap = new HashMap<>();

                    bookMap.put("Title", title);
                    bookMap.put("Author", author);
                    bookMap.put("ISBN", isbn);*/

                    //TODO: Authentication for the user adding a book
                    firestore.collection("Books").document().set(book);

                    Intent intent = new Intent(AddBookActivity.this, MyBooksActivity.class);
                    startActivity(intent);
                }
                catch(Exception e) {}
            }
        });
    }
}
