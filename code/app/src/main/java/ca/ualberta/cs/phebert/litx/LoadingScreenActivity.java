package ca.ualberta.cs.phebert.litx;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

/**
 * Creates a simple splash screen that gets displayed while we retrieve information from the
 * database, without this the screen just appears blank while starting up
 * @author sdupasqu
 * @version 1.0
 * @see MainActivity
 */
public class LoadingScreenActivity extends Activity {

    /**
     * Start the main activity as soon as loading is done and finish this activity
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
