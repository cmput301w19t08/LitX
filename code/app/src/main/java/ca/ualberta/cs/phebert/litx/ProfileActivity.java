package ca.ualberta.cs.phebert.litx;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class ProfileActivity extends AppCompatActivity {
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
        setContentView(R.layout.view_profile);
        userView = findViewById(R.id.UserView);
        userView.setText(currentUser.getUserName());
        emailView = findViewById(R.id.emailView);
        emailView.setText(currentUser.getEmail());
        phoneView = findViewById(R.id.phoneView);
        phoneView.setText(currentUser.getPhoneNumber());
        userEdit = findViewById(R.id.UserEdit);
        userEdit.setText(currentUser.getUserName());
        emailEdit = findViewById(R.id.emailEdit);
        emailEdit.setText(currentUser.getEmail());
        phoneEdit = findViewById(R.id.phoneEdit);
        phoneEdit.setText(currentUser.getPhoneNumber());
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
        // TODO handle profile creation.
        if (creating) {

        } else {
            userView.setText(userEdit.getText());
            emailView.setText(emailEdit.getText());
            phoneView.setText(phoneView.getText());
        }
    }
}