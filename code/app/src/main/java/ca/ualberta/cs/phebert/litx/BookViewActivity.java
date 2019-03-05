package ca.ualberta.cs.phebert.litx;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * superclass for myBooks and for MyBooksActivity and searchActivity.
 */
public class BookViewActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_book);
    }
}
