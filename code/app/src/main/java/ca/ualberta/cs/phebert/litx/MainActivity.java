package ca.ualberta.cs.phebert.litx;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    public static final String FilterMode = "ca.ualberta.cs.phebert.litx.FilterMode";
    static Thread loader;

    void getAllData() {
        User.getAll();
         // this should be enough if requests weren't empty
        Book.getAll();
        Request.getAll();


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(!User.isSignedIn()) {
            goToProfileView(null);
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        if (User.isSignedIn()) {
            loader = new Thread(this::getAllData);
            loader.start();
        }
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
