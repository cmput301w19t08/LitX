package ca.ualberta.cs.phebert.litx;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MyBooksActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_books);
    }

    public void addNew(View view) {
        Intent intent = new Intent(this, AddBookActivity.class);
        startActivity(intent);
    }
}
