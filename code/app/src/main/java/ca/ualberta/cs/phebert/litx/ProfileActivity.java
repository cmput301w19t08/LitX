package ca.ualberta.cs.phebert.litx;

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

import ca.ualberta.cs.phebert.litx.Models.User;
import ca.ualberta.cs.phebert.litx.Observers.UserObserver;

public class ProfileActivity extends AppCompatActivity implements UserObserver {
    private static final String LOG_TAG = "litX.ProfileActivity";
    public static final String UID_IN = "UserUID";
    private View viewProfile;
    private View editProfile;
    private User currentUser;
    private boolean creating; // might be a bad idea to create profiles
    private TextView userView;
    private TextView emailView;
    private TextView phoneView;
    private EditText userEdit;
    private EditText emailEdit;
    private EditText phoneEdit;


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
        userView = findViewById(R.id.UserView);
        emailView = findViewById(R.id.emailView);
        phoneView = findViewById(R.id.phoneView);

        Intent intent = getIntent();
        if (intent.hasExtra(UID_IN)) {
            currentUser = User.findByUid(intent.getStringExtra(UID_IN));
            onUpdate(currentUser);
            TextView editButton = (TextView) findViewById(R.id.EditButton);
            TextView addAccButton = (TextView) findViewById(R.id.addAccButton);
            // Only the owner would be able to edit or add
            editButton.setVisibility(View.INVISIBLE);
            addAccButton.setVisibility(View.INVISIBLE);
        } else {
            if (User.isSignedIn()) {
                currentUser = User.currentUser();
                assert currentUser != null;
                currentUser.addObserver(this);
                onUpdate(currentUser); // might as well.
            } else {
                userView.setText("???");
                emailView.setText("???");
                phoneView.setText("???");
            }
        }
    }

    public void editProfile(View v) {
        setContentView(editProfile);
        creating = false;
    }

    public void addProfile(View v) {
        setContentView(editProfile);
        creating = true;
    }

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

    @Override
    public void onBackPressed() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            finishAffinity();
        } else {
            finish();
        }
    }


    @Override
    public void onUpdate(User user) {
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