package ca.ualberta.cs.phebert.litx;

import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

/**
 * Activity for viewing a photo when selected from the view book activity. This activity implements
 * adding a photo as well as removing a photo
 * @author sdupasqu
 * @version 1.0
 * @see BookViewActivity
 */
public class ViewPhotoActivity extends AppCompatActivity {

    private static final int image = 10;
    Uri uri;

    private ImageView ivPhoto;
    private Button btn_add;
    private Button btn_delete;
    private Button btn_done;
    private Button btn_cancel;
    private ProgressBar progressBar;
    private TextView tvProgress;

    private int iconId;
    private Boolean cancel;

    private StorageReference storageRef;
    private StorageReference pathReference;

    /**
     * Sets onClickListeners for the buttons, loads the corresponding image to the ImageView if the
     * photo exists
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_photo);
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        cancel = false;

        // Find items on the activity
        btn_add = (Button) findViewById(R.id.addPhotoButton);
        btn_delete = (Button) findViewById(R.id.deletePhotoButton);
        btn_done = (Button) findViewById(R.id.donePhotoButton);
        btn_cancel = (Button) findViewById(R.id.cancelUploadButton);
        ivPhoto = (ImageView) findViewById(R.id.photoImageView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        tvProgress = (TextView) findViewById(R.id.progressTextView);
        storageRef = FirebaseStorage.getInstance().getReference();

        Intent intent = getIntent();
        if(intent.getExtras() == null) {
            // Book didn't pass, finish activity
            finish();
            return;
        }
        final Book book = Book.findByDocId(intent.getExtras().getString("Book"));

        // Find the images to load and load them
        pathReference = storageRef.child(book.getOwner().getUserid() + "/" + Long.toString(book.getIsbn()));
        iconId = this.getResources().getIdentifier("book_icon", "drawable", this.getPackageName());
        loadImage();

        if (!book.getOwner().getUserid().equals(uid)) {
            // Don't allow adding/deleting if the user accessing the photo doesn't own the book
            btn_delete.setVisibility(View.GONE);
            btn_add.setVisibility(View.GONE);
        }

        // Start the gallery on the users phone to select an image
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photos = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(photos, image);
            }
        });

        // Remove the file if it exists in the database
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Delete the file
                try {
                    pathReference.delete();
                    GlideApp.with(ViewPhotoActivity.this).load(iconId).into(ivPhoto);
                    Log.d("Success", "File has been deleted");
                } catch(Exception e) {
                    Log.d("Error", "Error occured, file could not be deleted!");
                }
            }
        });

        // Return to BookViewActivity since the user is done with adding photos
        btn_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent bookView = new Intent(ViewPhotoActivity.this, BookViewActivity.class);
                bookView.putExtra("Book", book.getDocID());
                finish();
                startActivity(bookView);
            }
        });

        // Set the cancel boolean to true to indicate the user wants to cancel the photo upload
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel = true;
            }
        });
    }

    /**
     * When the activity of finding the photo finishes, try to upload it to storage
     * @param request request code to ensure it was the photo finding activity
     * @param result result code to determine if it was successful
     * @param intent intent we get the data from
     */
    protected void onActivityResult(int request, int result, Intent intent) {
        super.onActivityResult(request, result, intent);
        if (result == RESULT_OK && request == image) {
            uri = intent.getData();
            if (uri != null) {
                // While the photo uploads we change the layout to show the upload progress
                UploadTask upload = pathReference.putFile(uri);
                btn_add.setVisibility(View.GONE);
                btn_done.setVisibility(View.GONE);
                btn_delete.setVisibility(View.GONE);
                btn_cancel.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                tvProgress.setVisibility(View.VISIBLE);

                // When the upload is done revert to the normal activity
                upload.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        showButtons();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    // Update the progress bar while the upload is ongoing
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        if (!cancel) {
                            long progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            progressBar.setProgress((int) progress);
                            tvProgress.setText("Uploading... " + Long.toString(progress) + "% done");
                        } else {
                            // User cancelled the upload
                            upload.cancel();
                            showButtons();
                            loadImage();
                            cancel = false;
                        }
                    }
                });
            }

            Glide.with(this).load(uri).into(ivPhoto);
        }
    }

    /**
     * If the image exists in storage then load it
     */
    private void loadImage() {
        // Load the image into the imageview if it exists
        pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String imageURL = uri.toString();
                GlideApp.with(ViewPhotoActivity.this).load(imageURL).placeholder(iconId).into(ivPhoto);
            }
        });
    }

    /**
     * Change the activity layout to the normal way of looking, ie no more progress bar and buttons
     * shown again
     */
    private void showButtons() {
        btn_add.setVisibility(View.VISIBLE);
        btn_delete.setVisibility(View.VISIBLE);
        btn_done.setVisibility(View.VISIBLE);
        btn_cancel.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        tvProgress.setVisibility(View.GONE);
    }
}