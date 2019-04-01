package ca.ualberta.cs.phebert.litx;

import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

/**
 * AddBookActivity takes the entered information and if valid, creates a new book object from them
 * once the okay button gets clicked. The book then gets added to the database.
 * @author sdupasqu
 * @version 1.0
 * @see MyBooksActivity
 * @see BookViewActivity
 * @see Book
 * @see ScanBookActivity
 */
public class AddBookActivity extends AppCompatActivity {
    private static final int image = 100;
    private Uri uri;

    private EditText titleView;
    private EditText ISBNView;
    private EditText authorView;

    private StorageReference pathReference;

    private Boolean photoExists = false;
    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

    private Button btnOkay;
    private Button cancel;
    private ImageView photo;
    private int iconId;

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
        photo = (ImageView) findViewById(R.id.addBookImage);
        cancel = (Button) findViewById(R.id.removePhotoButton);

        String id = ""; // To determine the document id in Firestore
        iconId = this.getResources().getIdentifier("book_icon", "drawable", this.getPackageName());

        try {
            // If the book is being edited then get the document id to change in the future
            Intent intent = getIntent();
            final Book book = Book.findByDocId(intent.getExtras().getString("Book"));
            loadImage(book);

            id = book.getDocID();

            //Fill the edit text boxes with the book information
            titleView.setText(book.getTitle());
            authorView.setText(book.getAuthor());
            ISBNView.setText(String.valueOf(book.getIsbn()));

        } catch (Exception e) {}

        // Make the document id final so the onClickListener can use it
        final String docID = id;
        btnOkay = (Button) findViewById(R.id.btnOkay);

        // When okay button is clicked do the following
        btnOkay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                    User u = User.currentUser();
                    Book b = new Book(u, author, title, isbn);

                    b.push();
                    if (photoExists && uri != null) {
                        addImage(b);
                    } else {
                        try {
                            pathReference.delete();
                        } catch(Exception e) {}
                        // Go back to MyBooksActivity after the book has been added
                        finish();
                    }
                }
                catch(Exception e) {
                    TextView invalid = (TextView) findViewById(R.id.invalidTextView);
                    invalid.setVisibility(View.VISIBLE);
                    Log.d("LitX", "fields not set properly", e);
                } // If fields are invalid do nothing
            }
        });

        photo.setOnClickListener(v -> {
            Intent photos = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
            startActivityForResult(photos, image);
        });

        cancel.setOnClickListener(v -> {
            cancel.setVisibility(View.GONE);
            photoExists = false;
            GlideApp.with(AddBookActivity.this).load(iconId).into(photo);
        });
    }

    /**
     * onActivityResult tries to get information from the last activity, in this case the last
     * activity will either be scanning for the isbn or getting a photo from the users device
     * @param requestCode number returned to indicate the activity that got a result
     * @param resultCode number indicating the result of performing the activity
     * @param data the intent that the data was retrieved from
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String TAG = "scan";
        if(requestCode == 155) {
            if(data != null) {

                String title = data.getStringExtra("Title");
                String ISBN = data.getStringExtra("ISBN");
                String author = data.getStringExtra("Author");
                ISBNView.setText(ISBN);
                titleView.setText(title);
                authorView.setText(author);

            } else {
                Log.w(TAG,"data is null");
            }
        }
        if (requestCode == image && resultCode == RESULT_OK) {
            uri = data.getData();
            if (uri != null) {
                cancel.setVisibility(View.VISIBLE);
                photoExists = true;
                Glide.with(this).load(uri).into(photo);
            }
        }
    }

    /**
     * the onClick listener for the scan button,
     * Starts a scanning activity so that the ISBN of a book mey be scanned.
     * @param v the scan button
     */
    public void scanISBN(View v) {
        Intent intent = new Intent(this, ScanBookActivity.class);
        startActivityForResult(intent,155);
    }

    /**
     * Loads an image from the database if it exists, otherwise the placeholder icon stays in the
     * imageview unless the user changes it
     * @param book the book that we are trying to find the image for
     */
    private void loadImage(Book book) {
        // Load the image into the imageview if it exists
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        pathReference = storageReference.child(book.getOwner().getUserid() + "/" + Long.toString(book.getIsbn()));
        pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                AddBookActivity.this.uri = uri;
                String imageURL = uri.toString();
                cancel.setVisibility(View.VISIBLE);
                photoExists = true;
                GlideApp.with(AddBookActivity.this).load(imageURL).placeholder(iconId).into(photo);
            }
        });
    }

    /**
     * sets up the UI to add an image.
     * @param book the book to which the image should be added.
     */
    private void addImage(Book book) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        pathReference = storageReference.child(book.getOwner().getUserid() + "/" + Long.toString(book.getIsbn()));
        UploadTask upload = pathReference.putFile(uri);

        // Hide buttons and make the progress bar visible while the photo uploads
        titleView.setVisibility(View.GONE);
        authorView.setVisibility(View.GONE);
        ISBNView.setVisibility(View.GONE);
        btnOkay.setVisibility(View.GONE);
        cancel.setVisibility(View.GONE);
        findViewById(R.id.btnISBN).setVisibility(View.GONE);
        photo.setVisibility(View.GONE);
        findViewById(R.id.changeImageTextView).setVisibility(View.GONE);
        findViewById(R.id.invalidTextView).setVisibility(View.GONE);

        findViewById(R.id.createBookProgress).setVisibility(View.VISIBLE);
        findViewById(R.id.creatingBookTextView).setVisibility(View.VISIBLE);

        // Once uploaded finish the activity
        upload.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                finish();
            }
        });
    }
}