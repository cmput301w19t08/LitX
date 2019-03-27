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

    private static final int image = 100;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_photo);
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        cancel = false;

        btn_add = (Button) findViewById(R.id.addPhotoButton);
        btn_delete = (Button) findViewById(R.id.deletePhotoButton);
        btn_done = (Button) findViewById(R.id.donePhotoButton);
        btn_cancel = (Button) findViewById(R.id.cancelUploadButton);
        ivPhoto = (ImageView) findViewById(R.id.photoImageView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        tvProgress = (TextView) findViewById(R.id.progressTextView);
        storageRef = FirebaseStorage.getInstance().getReference();

        Intent intent = getIntent();
        final Book book = (Book) intent.getExtras().getSerializable("Book");

        pathReference = storageRef.child(book.getOwnerUid() + "/" + Long.toString(book.getIsbn()));
        iconId = this.getResources().getIdentifier("book_icon", "drawable", this.getPackageName());
        load_image();

        if (!book.getOwnerUid().equals(uid)) {
            // Don't allow adding/deleting if the user accessing the photo doesn't own the book
            btn_delete.setVisibility(View.GONE);
            btn_add.setVisibility(View.GONE);
        }

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photos = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(photos, image);
            }
        });

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

        btn_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent view_book = new Intent(ViewPhotoActivity.this, BookViewActivity.class);
                view_book.putExtra("Book", book);
                finish();
                startActivity(view_book);
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel = true;
            }
        });
    }

    protected void onActivityResult(int request, int result, Intent intent) {
        super.onActivityResult(request, result, intent);
        if (result == RESULT_OK && request == image) {
            uri = intent.getData();
            if (uri != null) {
                UploadTask upload = pathReference.putFile(uri);
                btn_add.setVisibility(View.GONE);
                btn_done.setVisibility(View.GONE);
                btn_delete.setVisibility(View.GONE);
                btn_cancel.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                tvProgress.setVisibility(View.VISIBLE);

                upload.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        Log.d("Upload", "Completed");
                        show_buttons();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        if (!cancel) {
                            long progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            progressBar.setProgress((int) progress);
                            tvProgress.setText("Uploading... " + Long.toString(progress) + "% done");
                        } else {
                            upload.cancel();
                            show_buttons();
                            load_image();
                            cancel = false;
                        }
                    }
                });
            }

            Glide.with(this).load(uri).into(ivPhoto);
        }
    }

    private void load_image() {
        // Load the image into the imageview if it exists
        pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String imageURL = uri.toString();
                GlideApp.with(ViewPhotoActivity.this).load(imageURL).placeholder(iconId).into(ivPhoto);
            }
        });
    }

    private void show_buttons() {
        btn_add.setVisibility(View.VISIBLE);
        btn_delete.setVisibility(View.VISIBLE);
        btn_done.setVisibility(View.VISIBLE);
        btn_cancel.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        tvProgress.setVisibility(View.GONE);
    }
}