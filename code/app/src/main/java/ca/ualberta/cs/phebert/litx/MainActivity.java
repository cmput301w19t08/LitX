package ca.ualberta.cs.phebert.litx;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public static final String FilterMode = "ca.ualberta.cs.phebert.litx.FilterMode";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void filter(View v) {
        Intent intent = new Intent(this, BookStatusActivity.class);
        if(v.getId() == R.id.requests_home) {
            intent.putExtra(FilterMode,0);
        } else if(v.getId() == R.id.accept_home) {
            intent.putExtra(FilterMode,1);
        } else { // borrowed
            intent.putExtra(FilterMode,2);
        }
        startActivity(intent);
    }

    public void goToProfileView(View v) {
        Intent intent = new Intent(this,ProfileActivity.class);
        startActivity(intent);
    }

    public void getMyBooks(View v) {
        // Should not be BookViewActivity, need a new activity for MyBooks
        Intent intent = new Intent(this, MyBooksActivity.class);
        startActivity(intent);
    }

    // No method named this in UML, must be added
    public void exchangeBook(View v) {
        Intent intent = new Intent(this, ExchangeActivity.class);
        startActivity(intent);
    }

    public void searchForBooks(View v) {
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
    }
}
