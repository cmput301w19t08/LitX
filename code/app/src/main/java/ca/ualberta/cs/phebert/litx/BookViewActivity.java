package ca.ualberta.cs.phebert.litx;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * superclass for myBooks and for MyBooksActivity and searchActivity.
 */
public class BookViewActivity extends AppCompatActivity {

    private Button delete;
    private Button edit;

    private FirebaseFirestore firestore;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_book);
        Intent intent = getIntent();
        final Book book = (Book) intent.getExtras().getSerializable("Book");

        ImageView image = (ImageView) findViewById(R.id.bookImage);
        TextView textView = (TextView) findViewById(R.id.descriptionIDView);
        String description = "Title: " + book.getTitle() + "\n" + "Author: " + book.getAuthor()
                + "\n" + "ISBN: " + String.valueOf(book.getIsbn());
        textView.setText(description);
        image = book.getPhotograph();

        delete = (Button) findViewById(R.id.deleteButtonID);
        edit = (Button) findViewById(R.id.editButtonID);

        firestore = FirebaseFirestore.getInstance();

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: Authentication of deleting book in database
                firestore.collection("Books").document(book.getDocID()).delete();

                Intent intent = new Intent(BookViewActivity.this, MyBooksActivity.class);
                startActivity(intent);
            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: pass Book object into AddBookActivity
                Intent intent = new Intent(BookViewActivity.this, AddBookActivity.class);
                intent.putExtra("Book", book);
                startActivity(intent);
            }
        });
    }
}
