package ca.ualberta.cs.phebert.litx;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * superclass for myBooks and for MyBooksActivity and searchActivity.
 */
public class BookViewActivity extends AppCompatActivity {

    private Button delete;
    private Button edit;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_book);

        delete = (Button) findViewById(R.id.deleteButtonID);
        edit = (Button) findViewById(R.id.editButtonID);

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: delete Book object from database
                Intent intent = new Intent(BookViewActivity.this, MyBooksActivity.class);
                startActivity(intent);
            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: pass Book object into AddBookActivity
                Intent intent = new Intent(BookViewActivity.this, AddBookActivity.class);
                startActivity(intent);
            }
        });
    }
}
