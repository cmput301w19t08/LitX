package ca.ualberta.cs.phebert.litx;

import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * AddBookActivity takes the entered information and if valid, creates a new book object from them
 * once the okay button gets clicked. The book then gets added to the database.
 * @author sdupasqu
 * @version 1.0
 * @see MyBooksActivity, ViewBookActivity, Book
 */
public class AddBookActivity extends AppCompatActivity {
    private static final int image = 100;
    private Uri uri;

    EditText titleView;
    EditText ISBNView;
    EditText authorView;

    private StorageReference pathReference;

    private Boolean photoExists = false;
    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

    private Button btnOkay;
    private Button cancel;
    private ImageView photo;
    private TextView changeImage;
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
        changeImage = (TextView) findViewById(R.id.changeImageTextView);
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

                    if (photoExists) {
                        if (uri != null) {
                            addImage(b);
                        }
                    } else {
                        try {
                            pathReference.delete();
                        } catch(Exception e) {}
                    }

                    b.push();
                    // Go back to MyBooksActivity after the book has been added
                    finish();
                }
                catch(Exception e) {
                    TextView invalid = (TextView) findViewById(R.id.invalidTextView);
                    invalid.setVisibility(View.VISIBLE);
                    Log.d("LitX", "fields not set properly", e);
                } // If fields are invalid do nothing
            }
        });

        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photos = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(photos, image);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel.setVisibility(View.GONE);
                photoExists = false;
                GlideApp.with(AddBookActivity.this).load(iconId).into(photo);
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
        if (requestCode == image && resultCode == RESULT_OK) {
            uri = data.getData();
            if (uri != null) {
                cancel.setVisibility(View.VISIBLE);
                photoExists = true;
                Glide.with(this).load(uri).into(photo);
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

    private void loadImage(Book book) {
        // Load the image into the imageview if it exists
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        pathReference = storageReference.child(book.getOwner().getUserid() + "/" + Long.toString(book.getIsbn()));
        pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String imageURL = uri.toString();
                cancel.setVisibility(View.VISIBLE);
                photoExists = true;
                GlideApp.with(AddBookActivity.this).load(imageURL).placeholder(iconId).into(photo);
            }
        });
    }

    private void addImage(Book book) {
        pathReference.putFile(uri);
    }
}