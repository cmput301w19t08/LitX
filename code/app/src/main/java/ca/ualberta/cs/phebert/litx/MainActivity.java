package ca.ualberta.cs.phebert.litx;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Map;

public class MainActivity extends AppCompatActivity {
    public static final String FilterMode = "ca.ualberta.cs.phebert.litx.FilterMode";
    static Thread loader;

    void getAllData() {
        User.getAll();
        Book.getAll();
        Request.getAll();
        Intent serviceIntent = new Intent(this, NotificationIntentService.class);
        serviceIntent.putExtra("NOTIFICATION_INTENT_SERVICE", "MainActivity");
        startService(serviceIntent);
        // this should be enough if requests weren't empty\
        Map<String, Request>  db =  Request.getAll(); // this should be enough if requests weren't empty
        for (Map.Entry<String, Request> entry : db.entrySet()) {
            Log.v("LitX.REQUEST", entry.getValue().getRequester().getUserName());
            if (entry.getValue().getRequester().getUserName().equals(User.currentUser().getUserName())) {
                if (entry.getValue().getRequestSeen() == Boolean.FALSE) {
                    Request request = entry.getValue();
                    request.generateNotification(this);
                    request.setRequestSeen(Boolean.TRUE);
                    request.toMap();
                    request.selfPush();
                }
            }
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Request.createNotificationChannel(this);

        if(!User.isSignedIn()) {
            goToProfileView(null);
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        if (User.isSignedIn()) {
            Log.i("LitX Thread", "thread is running loader");
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

    @Override
    protected void onStop() {
       /* for(Request request :Request.getAll().values()) {
            request.generateNotification(this);
            break;
        }*/
        super.onStop();
    }
}
