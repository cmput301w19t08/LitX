package ca.ualberta.cs.phebert.litx;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class ExchangeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exchange);
    }

    /*
     * On HandOver button press
     * Gets the ISBN bar code
     * Changes status from accepted to borrowed
     * for borrower and owner
     */
    public void lend (View v) {
        //TODO lending procedure
    }

    /*
     * On Recieve button press
     * Gets the ISBN bar code
     * Moves book back to available for owner (or increments qty)
     * Removes book from borrowing for borrower
     */
    public void retrieve (View v) {
        //TODO retrieval procedure
    }
}
