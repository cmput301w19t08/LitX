package ca.ualberta.cs.phebert.litx;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import static ca.ualberta.cs.phebert.litx.ProfileActivity.UID_IN;


import java.util.ArrayList;

/**
 * Displays the description of a book after a user selected the book to view in the previous
 * screen
 * @author sdupasqu, plontke
 * @version 1.0
 * @see MyBooksActivity
 * @see AddBookActivity
 * @see Book
 * @see ViewPhotoActivity
 * @see BookStatusActivity
 * @see Request
 * @see RequestListAdapter
 */
public class BookViewActivity extends AppCompatActivity {

    private Button delete;
    private Button edit;
    private Button request;
    private String uid = User.currentUser().getUserid();
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private Book book;

    private ImageView photo;

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
        recyclerView = (RecyclerView) findViewById(R.id.requestsRecycleView);
        photo = (ImageView) findViewById(R.id.bookImage);
        TextView changeImage = (TextView) findViewById(R.id.changeImage);

        // Receive the book object the user selected
        Intent intent = getIntent();
        String bookId = intent.getStringExtra("Book");
        Log.i("bookID", bookId);
        book = Book.findByDocId(bookId);

        // Set the adapter to the recycler view
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new RequestListAdapter(this, book.getRequests());

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                recyclerView.getContext(),1);
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setAdapter(adapter);

        loadImage(book);

        if (book.getOwner().equals(User.currentUser())){
            // A owner of the Book cannot request his own book
            request.setVisibility(View.GONE);
            // Find buttons in the layout
            delete = (Button) findViewById(R.id.deleteButton);
            edit = (Button) findViewById(R.id.editButtonID);

        /* When delete button is clicked remove the book from the database, then go back to MyBooks
         * screen
         */
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    book.delete(book);

                    finish();
                }
            });

            // Pass the book object into AddBook so the user can edit it
            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Pass Book object into AddBookActivity and start the activity
                    Intent intent = new Intent(BookViewActivity.this, AddBookActivity.class);
                    intent.putExtra("Book", book.getDocID());
                    finish();
                    startActivity(intent);
                }
            });
        } else {
            // Update views on the book
            book.setViews(book.getViews() + 1);
            book.push();

            // The Owner is not viewing the book he cannot delete or edit it just Request it
            edit.setVisibility(View.GONE);
            delete.setVisibility(View.GONE);
            changeImage.setText("Click the photo to view the image!");

            // Check if the book has already been requested by the person viewing it
            ArrayList<Request> bookRequests = book.getRequests();
            for (Request iterRequest : book.getRequests()) {
                if (uid.equals(iterRequest.getRequester().getUserid())) {
                    bookRequested();
                }
            }

            TextView ownerUsernameView = (TextView) findViewById(R.id.ownerViewID);
            String ownerUsername = book.getOwner().getUserName();
            ownerUsernameView.setText(ownerUsername);

            // View the owners profile
            ownerUsernameView.setOnClickListener(new TextView.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(BookViewActivity.this, ProfileActivity.class);
                    intent.putExtra(UID_IN, book.getOwner().getUserid());

                    startActivity(intent);
                }
            });

            // Request the book the user is viewing
            request.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(BookViewActivity.this, "Your Request has been sent",
                            Toast.LENGTH_SHORT).show();

                    bookRequested();
                    book.addRequest();

                    adapter.notifyDataSetChanged();
                }
            });
        }
        
        // Set an onClickListener for the photo that launches the view photo activity
        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BookViewActivity.this, ViewPhotoActivity.class);
                intent.putExtra("Book", book.getDocID());
                finish();
                startActivity(intent);
            }
        });
    }

    /**
     * When the activity starts, set the description of the book
     */
    @Override
    protected void onStart() {
        super.onStart();

        TextView textView = (TextView) findViewById(R.id.descriptionIDView);
        String description = "Title: " + book.getTitle() + "\n" + "Author: " + book.getAuthor()
                + "\n" + "ISBN: " + String.valueOf(book.getIsbn());
        textView.setText(description);
    }

    /**
     * Load the image of the book if it exists, otherwise make the default image the book icon
     * @param book the book that may have the image
     */
    private void loadImage(Book book) {
        // Load the image into the imageview if it exists
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        int iconId = this.getResources().getIdentifier("book_icon", "drawable", this.getPackageName());
        StorageReference pathReference = storageReference.child(book.getOwner().getUserid() + "/" + Long.toString(book.getIsbn()));

        pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String imageURL = uri.toString();
                GlideApp.with(BookViewActivity.this).load(imageURL).placeholder(iconId).into(photo);
            }
        });
    }

    /**
     * Changes the visibility of things on the screen, hides the request button and displays
     * a text view claiming the book has been requested
     */
    private void bookRequested() {
        request.setVisibility(View.GONE);
        TextView requested = (TextView) findViewById(R.id.requestedTextView);
        requested.setVisibility(View.VISIBLE);
    }
}
