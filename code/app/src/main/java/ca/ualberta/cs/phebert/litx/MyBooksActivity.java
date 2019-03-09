package ca.ualberta.cs.phebert.litx;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MyBooksActivity extends AppCompatActivity {

    private Button view;
    private Button addNew;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_books);

        view = (Button) findViewById(R.id.testButton);
        addNew = (Button) findViewById(R.id.btnAddNew);

        addNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyBooksActivity.this, AddBookActivity.class);
                startActivity(intent);
            }
        });

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyBooksActivity.this, BookViewActivity.class);
                startActivity(intent);
            }
        });
    }
}
