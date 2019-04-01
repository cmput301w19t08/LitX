package ca.ualberta.cs.phebert.litx;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

/**
 * This Activity is used to display a {@link User user's} information.
 * @author phebert
 * @version 1.0
 * @see MainActivity
 */
public class ProfileActivity extends AppCompatActivity implements UserObserver {
    private static final String LOG_TAG = "litX.ProfileActivity";
    public static final String UID_IN = "UserUID";
    private View viewProfile;
    private View editProfile;
    private User currentUser;
    private boolean creating;
    private TextView userView;
    private TextView emailView;
    private TextView phoneView;
    private EditText userEdit;
    private EditText emailEdit;
    private EditText phoneEdit;


    /**
     * Starts the application by loading into memory all the views in both layouts.
     */
    @SuppressLint("InflateParams")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewProfile = getLayoutInflater().inflate(R.layout.view_profile, null);
        editProfile = getLayoutInflater().inflate(R.layout.edit_profile, null);
        setContentView(editProfile); // findViewById only works for visible view (eg views in the content view.
        userEdit = findViewById(R.id.UserEdit);
        emailEdit = findViewById(R.id.emailEdit);
        phoneEdit = findViewById(R.id.phoneEdit);
        setContentView(viewProfile);
        TextView editButton = (TextView) findViewById(R.id.EditButton);
        TextView addAccButton = (TextView) findViewById(R.id.addAccButton);
        userView = findViewById(R.id.UserView);
        emailView = findViewById(R.id.emailView);
        phoneView = findViewById(R.id.phoneView);

        Intent intent = getIntent();
        if (intent.hasExtra(UID_IN)) {
            currentUser = User.findByUid(intent.getStringExtra(UID_IN));
            onUserUpdated(currentUser);
            // Only the owner would be able to edit or add
            editButton.setVisibility(View.GONE);
            addAccButton.setVisibility(View.GONE);
        } else {
            if (User.isSignedIn()) {
                addAccButton.setVisibility(View.GONE);
                currentUser = User.currentUser();
                assert currentUser != null;
                currentUser.addObserver(this);
                onUserUpdated(currentUser); // might as well.
            } else {
                editButton.setVisibility(View.GONE);
                userView.setText("???");
                emailView.setText("???");
                phoneView.setText("???");
            }
        }
    }

    /**
     * Swaps the layout to the layout used to edit a user.
     * @param v view for the button
     */
    public void editProfile(View v) {
        setContentView(editProfile);
        creating = false;
    }

    /**
     * Swaps the layout to the layout used to create a user or sign in.
     * @param v view for the button
     */
    public void addProfile(View v) {
        setContentView(editProfile);
        creating = true;
    }

    /**
     * Swaps the layout to the one used to view a user and updates the user.
     * @param v
     */
    public void profileDone(View v) {
        setContentView(viewProfile);
        if (currentUser == null) {
            if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                currentUser = new User();
            }
        }
        currentUser.setUserName(userEdit.getText().toString());
        if (currentUser.getUserName().equals(userEdit.getText().toString())) {
            userView.setText(userEdit.getText());
        } else {
            Toast.makeText(this, "Setting the username failed", Toast.LENGTH_SHORT)
                    .show();
            Log.w(LOG_TAG, "Setting username failed");
        }
        currentUser.setEmail(emailEdit.getText().toString());
        if (currentUser.getEmail().equals(emailEdit.getText().toString())) {
            emailView.setText(emailEdit.getText());
        }
        currentUser.setPhoneNumber(phoneEdit.getText().toString());
        phoneView.setText(phoneEdit.getText());

        if (creating) {
            User.logIn(
                    userView.getText().toString(),
                    emailView.getText().toString(),
                    phoneView.getText().toString(),
                    this);
        }

        currentUser.sync();
    }

    /**
     * If the user is not signed in, this is technically the first activity they saw,
     * so when the back button is pressed and the user is not logged in,
     * finish the application.
     */
    @Override
    public void onBackPressed() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            finishAffinity();
        } else {
            finish();
        }
    }


    /**
     * updates the user's information in real time.
     * @param user User that is getting updated
     */
    @Override
    public void onUserUpdated(User user) {
        if(user == currentUser) {
            userEdit.setText(currentUser.getUserName());
            userView.setText(currentUser.getUserName());
            emailEdit.setText(currentUser.getEmail());
            emailView.setText(currentUser.getEmail());
            phoneEdit.setText(currentUser.getPhoneNumber());
            phoneView.setText(currentUser.getPhoneNumber());
        }
    }
}