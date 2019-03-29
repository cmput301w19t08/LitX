package ca.ualberta.cs.phebert.litx;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import static ca.ualberta.cs.phebert.litx.ProfileActivity.UID_IN;


/**
 * Displays the description of a book after a user selected the book to view in the previous
 * screen
 * @author sdupasqu, plontke
 * @version 1.0
 * @see MyBooksActivity, AddBookActivity, Book
 */
public class BookOwnerViewActivity extends AppCompatActivity {

    private Button delete;
    private Button edit;
    private Button request;
    private TextView header;
    private TextView owner;
    private TextView ownerusername;
    private ImageView photo;
    private String uid = User.currentUser().getUserid();
    private ArrayList<User> owners;
    private ArrayList<User> requestors;

    private UserListAdapter useradapter;
    RecyclerView recycler;
    RecyclerView.LayoutManager layoutManager;

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
        recycler = (RecyclerView) findViewById(R.id.requestsRecycleView);
        ownerusername = (TextView) findViewById(R.id.ownerViewID);

        owners = new ArrayList<>();
        requestors = new ArrayList<>();

        // Receive the book object the user selected
        Intent intent = getIntent();
        String bookId = intent.getExtras().getString("Book");
        final Book book = Book.findByDocId(bookId);

        load_image(book);

        if (book.getOwner().getUserid().equals(uid)){
            loadOwnersView(book);
        } else {
            loadRequestsView(book);
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
                Intent intent = new Intent(BookOwnerViewActivity.this, ViewPhotoActivity.class);
                intent.putExtra("Book", book);
                startActivity(intent);
            }
        });

    }

    private void loadOwnersView (Book book) {
        // A owner of the Book cannot request his own book
        edit.setVisibility(View.VISIBLE);
        delete.setVisibility(View.VISIBLE);
        request.setVisibility(View.GONE);
        ownerusername.setVisibility(View.GONE);

        /* When delete button is clicked remove the book from the database, then go back to MyBooks
        screen
         */
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: Authentication of deleting book in database
                book.delete();

                Intent intent = new Intent(BookOwnerViewActivity.this, MyBooksActivity.class);
                startActivity(intent);
            }
        });

        // Set description of book in the textview

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Pass Book object into AddBookActivity and start the activity
                Intent intent = new Intent(BookOwnerViewActivity.this, AddBookActivity.class);
                intent.putExtra("Book", book);
                startActivity(intent);
            }
        });
    }

    private void loadRequestsView (Book book) {
        // If the Owner is not viewing the book he cannot delete or edit it just Request it
        edit.setVisibility(View.GONE);
        delete.setVisibility(View.GONE);
        request.setVisibility(View.VISIBLE);
        ownerusername.setVisibility(View.VISIBLE);

        String ownername = book.getOwner().getUserName();
        ownerusername.setText(ownername);

        ownerusername.setOnClickListener(new TextView.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(BookOwnerViewActivity.this, ProfileActivity.class);
                intent.putExtra(UID_IN, book.getOwner().getUserid());

                startActivity(intent);
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
                //book.addRequest();
                Toast.makeText(BookOwnerViewActivity.this, "Your Request has been sent",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void load_image(Book book) {
        // Load the image into the imageview if it exists
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        int iconId = this.getResources().getIdentifier("book_icon", "drawable", this.getPackageName());
        StorageReference pathReference = storageReference.child(book.getOwner().getUserid() + "/" + Long.toString(book.getIsbn()));

        pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String imageURL = uri.toString();
                GlideApp.with(BookOwnerViewActivity.this).load(imageURL).placeholder(iconId).into(photo);
            }
        });
    }
}
