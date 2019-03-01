package ca.ualberta.cs.phebert.litx.annotations;

import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import ca.ualberta.cs.phebert.litx.R;

public class BookStatusActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_status);
        //TODO: Set context
    }
}
