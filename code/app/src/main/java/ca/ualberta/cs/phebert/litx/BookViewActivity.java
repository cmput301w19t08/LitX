package ca.ualberta.cs.phebert.litx;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import java.util.Map;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

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


    private String OWNER_USERNAME = "OWNER_USERNAME_STRING";
    private String OWNER_EMAIL = "OWNER_EMAIL_STRING";
    private String OWNER_PHONENUMBER = "OWNER_PHONENUMBER_STRING";

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
        String bookId = intent.getExtras().getString("Book");
        final Book book = Book.findByDocId(bookId);

        load_image(book);

        if (book.getOwner().getUserid().equals(uid)){
            // A owner of the Book cannot request his own book
            request.setVisibility(View.GONE);
            // Find buttons in the layout
            delete = (Button) findViewById(R.id.deleteButton);
            edit = (Button) findViewById(R.id.editButtonID);

        /* When delete button is clicked remove the book from the database, then go back to MyBooks
        screen
         */
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO: Authentication of deleting book in database
                    book.delete();

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

            TextView ownerUsernameView = (TextView) findViewById(R.id.ownerViewID);
            String ownerUsername = book.getOwner();
            ownerUsernameView.setText(ownerUsername);

            ownerUsernameView.setOnClickListener(new TextView.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(BookViewActivity.this, ProfileActivity.class);
                    intent.putExtra(OWNER_USERNAME, ownerUsername);

                    FirebaseFirestore fireData = FirebaseFirestore.getInstance();
                    fireData.collection("Users").whereEqualTo("userName", ownerUsername).get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            Map<String, Object> temp = document.getData();
                                            intent.putExtra(OWNER_USERNAME, temp.get("userName").toString());
                                            intent.putExtra(OWNER_EMAIL, temp.get("email").toString());
                                            intent.putExtra(OWNER_PHONENUMBER, temp.get("phoneNumber").toString());
                                        }
                                        // starts profileActivity
                                        startActivity(intent);
                                    }

                                }
                            });
                }
            });
            // Owner can not request their own books
            if (book.getOwner().equals(FirebaseAuth.getInstance().getCurrentUser().getDisplayName())) {
                // Find buttons in the layout
                request.setVisibility(View.GONE);
            }

            request.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //book.
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

    private void load_image(Book book) {
        // Load the image into the imageview if it exists
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        try {
            int iconId = this.getResources().getIdentifier("book_icon", "drawable", this.getPackageName());
            StorageReference pathReference = storageReference.child(book.getOwner().getUserid() + "/" + Long.toString(book.getIsbn()));
            //StorageReference pathReference = storageReference.child("sdupasqu-1.png");
            GlideApp.with(this)
                    .load(pathReference)
                    .placeholder(iconId)
                    .into(photo);
        } catch (Exception e) {}
    }
}
