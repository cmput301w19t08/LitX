package ca.ualberta.cs.phebert.litx;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class ExchangeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: Set content
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
