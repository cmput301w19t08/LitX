package ca.ualberta.cs.phebert.litx;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.local.QueryData;

import java.util.Map;

/**
 * Displays the description of a book after a user selected the book to view in the previous
 * screen
 * @author sdupasqu
 * @version 1.0
 * @see MyBooksActivity, AddBookActivity, Book
 */
public class BookViewActivity extends AppCompatActivity {

    private Button delete;
    private Button edit;
    private Button request;

    private String OWNER_USERNAME = "OWNER_USERNAME_FOR_PROFILE";
    private String OWNER_EMAIL = "OWNER_EMAIL_FOR_PROFILE";
    private String OWNER_PHONENUMBER = "OWNER_PHONENUMBER_FOR_PROFILE";
    private FirebaseFirestore firestore;

    /**
     * onCreate sets the description of the book selected, and sets onClickListeners to determine
     * whether the user has decided to delete or edit the book
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_book);

        // Receive the book object the user selected
        Intent intent = getIntent();
        final Book book = (Book) intent.getExtras().getSerializable("Book");
//        String previousActivityName = intent.getStringExtra("ACTIVITY_NAME");

        // Set descriptiption of book in the textview
        ImageView image = (ImageView) findViewById(R.id.bookImage);
        TextView textView = (TextView) findViewById(R.id.descriptionIDView);
        String description = "Title: " + book.getTitle() + "\n" + "Author: " + book.getAuthor()
                + "\n" + "ISBN: " + String.valueOf(book.getIsbn());
        textView.setText(description);
        image = book.getPhotograph();


        firestore = FirebaseFirestore.getInstance();

        delete = (Button) findViewById(R.id.deleteButtonID);
        edit = (Button) findViewById(R.id.editButtonID);
        request = (Button) findViewById(R.id.requestButton);

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
                                  //  Toast.makeText(BookViewActivity.this, "HEY", Toast.LENGTH_SHORT).show();

                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Map<String, Object> temp = document.getData();
                                        intent.putExtra(OWNER_USERNAME, temp.get("userName").toString());
                                        intent.putExtra(OWNER_EMAIL, temp.get("email").toString());
                                        intent.putExtra(OWNER_PHONENUMBER, temp.get("phoneNumber").toString());
                                    }
                                    startActivity(intent);
                                }

                            }
                        });
            }
        });






        if (book.getOwner().equals(FirebaseAuth.getInstance().getCurrentUser().getDisplayName())) {
            // Find buttons in the layout
            request.setVisibility(View.GONE);




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

            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Pass Book object into AddBookActivity and start the activity
                    Intent intent = new Intent(BookViewActivity.this, AddBookActivity.class);
                    intent.putExtra("Book", book);
                    startActivity(intent);
                }
            });
        } else {
            delete.setVisibility(View.GONE);
            edit.setVisibility(View.GONE);

            request.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO initialize a request then make it send notification and ect
                    Toast.makeText(BookViewActivity.this, "Request has Been Sent",
                            Toast.LENGTH_SHORT).show();
                }
            });


        }
    }
}
