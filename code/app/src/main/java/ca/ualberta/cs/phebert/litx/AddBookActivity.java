package ca.ualberta.cs.phebert.litx;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class AddBookActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);
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
