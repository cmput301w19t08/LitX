package ca.ualberta.cs.phebert.litx;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.InputStream;

/**
 * Displays the description of a book after a user selected the book to view in the previous
 * screen
 * @author sdupasqu, plontke
 * @version 1.0
 * @see MyBooksActivity, AddBookActivity, Book
 */
public class BookViewActivity extends AppCompatActivity {

    private Button delete;
    private Button edit;
    private Button request;
    private ImageView photo;
    private String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

    private FirebaseFirestore firestore;
    private StorageReference storageReference;

    /**
     * onCreate sets the description of the book selected, and sets onClickListeners to determine
     * whether the user has decided to delete or edit the book
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_book);
        request = (Button) findViewById(R.id.viewRequest);
        delete = (Button) findViewById(R.id.deleteButton);
        edit = (Button) findViewById(R.id.editButtonID);
        photo = (ImageView) findViewById(R.id.bookImage);

        // Receive the book object the user selected
        Intent intent = getIntent();
        final Book book = (Book) intent.getExtras().getSerializable("Book");

        // Load the image into the imageview if it exists
        storageReference = FirebaseStorage.getInstance().getReference();
        try {
            StorageReference pathReference = storageReference.child(book.getOwnerUid() + "/" + Long.toString(book.getIsbn()) + ".png");
            Log.d("Reference", pathReference.toString());
            GlideApp.with(this).load(pathReference).into(photo);
        } catch (Exception e) {}


        if (book.getOwnerUid().equals(uid)){
            // A owner of the Book cannot request his own book
            request.setVisibility(View.GONE);
            // Find buttons in the layout
            delete = (Button) findViewById(R.id.deleteButton);
            edit = (Button) findViewById(R.id.editButtonID);

            firestore = FirebaseFirestore.getInstance();

        /* When delete button is clicked remove the book from the database, then go back to MyBooks
        screen
         */
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO: Authentication of deleting book in database
                    firestore.collection("Books").document(book.getDocID()).delete();

                    Intent intent = new Intent(BookViewActivity.this, MyBooksActivity.class);
                    startActivity(intent);
                }
            });

        // Set description of book in the textview

            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Pass Book object into AddBookActivity and start the activity
                    Intent intent = new Intent(BookViewActivity.this, AddBookActivity.class);
                    intent.putExtra("Book", book);
                    startActivity(intent);
                }
            });
        }else {
            // The Owner is not viewing the book he cannot delete or edit it just Request it
            edit.setVisibility(View.GONE);
            delete.setVisibility(View.GONE);
            request.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(BookViewActivity.this, "Your Request has been sent",
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
        // Set descriptiption of book in the textview

        TextView textView = (TextView) findViewById(R.id.descriptionIDView);
        String description = "Title: " + book.getTitle() + "\n" + "Author: " + book.getAuthor()
                + "\n" + "ISBN: " + String.valueOf(book.getIsbn());
        textView.setText(description);

        // Set an onClickListener for the photo that launches the view photo activity
        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BookViewActivity.this, ViewPhotoActivity.class);
                intent.putExtra("Book", book);
                startActivity(intent);
            }
        });

    }
}
