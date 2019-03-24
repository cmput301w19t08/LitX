package ca.ualberta.cs.phebert.litx;

import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

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

    private StorageReference storageRef;
    private FirebaseUser owner = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_photo);

        storageRef = FirebaseStorage.getInstance().getReference();

        Intent intent = getIntent();
        final Book book = (Book) intent.getExtras().getSerializable("Book");

        Button btn_add = (Button) findViewById(R.id.addPhotoButton);
        Button btn_delete = (Button) findViewById(R.id.deletePhotoButton);
        Button btn_done = (Button) findViewById(R.id.donePhotoButton);

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photos = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(photos, image);
            }
        });

        btn_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String isbn = Long.toString(book.getIsbn());
                StorageReference storageReference = storageRef.child(/*book.getOwnerUid() + "/" +*/ Long.toString(book.getIsbn()) + ".png");

                if (uri != null) {
                    storageReference.putFile(uri);
                }

                Intent view_book = new Intent(ViewPhotoActivity.this, BookViewActivity.class);
                view_book.putExtra("Book", book);
                startActivity(view_book);
            }
        });
    }

    protected void onActivityResult(int request, int result, Intent intent) {
        super.onActivityResult(request, result, intent);
        if (result == RESULT_OK && request == image) {
            uri = intent.getData();
            ImageView ivPhoto = (ImageView) findViewById(R.id.photoImageView);

            Glide.with(this).load(uri).into(ivPhoto);
        }
    }
}
