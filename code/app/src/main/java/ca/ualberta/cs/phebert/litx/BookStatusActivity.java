package ca.ualberta.cs.phebert.litx;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import ca.ualberta.cs.phebert.litx.R;

public class BookStatusActivity extends AppCompatActivity {
    public static final String FilterMode = "ca.ualberta.cs.phebert.litx.FilterMode";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_status);
        Intent intent = getIntent();
        int filter = intent.getIntExtra(FilterMode, 0);
        if (filter == 0){
            // TODO Show the requested that arent accepted
        } else if (filter == 1) {
            //TODO Show the Books that have been accpeted
        } else{
            // TODO Show the books the user is borrowing
        }
    }
}
