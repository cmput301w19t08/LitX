package ca.ualberta.cs.phebert.litx;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class AddBookActivity extends AppCompatActivity {
    EditText titleView;
    EditText ISBNView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);
        titleView=(EditText)findViewById(R.id.editTitle);
        ISBNView=(EditText)findViewById(R.id.editISBN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String TAG = "scan";
        //titleView.setText("" + resultCode);
        if(requestCode == 155) {
            if(data != null) {
                String title = data.getStringExtra("Title");
                String ISBN = data.getStringExtra("ISBN");
                ISBNView.setText(ISBN);
                titleView.setText(title);
                Log.d(TAG, title);
            } else {
                Log.w(TAG,"data is null");
            }
        }
    }

    public void scanISBN(View v) {
        Intent intent = new Intent(this, scan.class);
        startActivityForResult(intent,155);
    }




    public void onOkay(View view) {
        //TODO: Check input fields are valid, then create book object and pass it back
        EditText etTitle = (EditText) findViewById(R.id.editTitle);
        EditText etAuthor = (EditText) findViewById(R.id.editAuthor);
        EditText etISBN = (EditText) findViewById(R.id.editISBN);

        String title = etTitle.getText().toString();
        String author = etTitle.getText().toString();
        long isbn = Long.valueOf(etISBN.getText().toString());

        Log.d("TEST", "onOkay() returned: " + title);
    }
}
