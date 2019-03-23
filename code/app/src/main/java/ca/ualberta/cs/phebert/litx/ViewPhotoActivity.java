package ca.ualberta.cs.phebert.litx;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * Activity for viewing a photo when selected from the view book activity. This activity implements
 * adding a photo as well as removing a photo
 * @author sdupasqu
 * @version 1.0
 * @see BookViewActivity
 */
public class ViewPhotoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_photo);

        Intent intent = getIntent();
        final Book book = (Book) intent.getExtras().getSerializable("Book");

        Button btn_add = (Button) findViewById(R.id.addPhotoButton);
        Button btn_delete = (Button) findViewById(R.id.deletePhotoButton);
        Button btn_done = (Button) findViewById(R.id.donePhotoButton);

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Working", "true");
            }
        });

        btn_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent view_book = new Intent(ViewPhotoActivity.this, BookViewActivity.class);
                view_book.putExtra("Book", book);
                startActivity(view_book);
            }
        });
    }
}
