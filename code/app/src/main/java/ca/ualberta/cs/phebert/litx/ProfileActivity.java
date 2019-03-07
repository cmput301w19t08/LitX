package ca.ualberta.cs.phebert.litx;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ProfileActivity extends AppCompatActivity {
    private View viewProfile;
    private View editProfile;
    private User currentUser;
    private boolean creating; // might be a bad idea to create profiles
    private TextView userView;
    private TextView emailView;
    private TextView phoneView;
    private TextView passwordEntry; // not on xml
    private EditText userEdit;
    private EditText emailEdit;
    private EditText phoneEdit;
    private EditText PasswordEdit; // not on xml

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewProfile = getLayoutInflater().inflate(R.layout.view_profile, null);
        editProfile = getLayoutInflater().inflate(R.layout.edit_profile, null);
        currentUser = new User("hi","example@example.com","7808994536");
        setContentView(editProfile); // findViewById only works for visible view (eg views in the content view.
        userEdit = findViewById(R.id.UserEdit);
        userEdit.setText(currentUser.getUserName());
        emailEdit = findViewById(R.id.emailEdit);
        emailEdit.setText(currentUser.getEmail());
        phoneEdit = findViewById(R.id.phoneEdit);
        phoneEdit.setText(currentUser.getPhoneNumber());
        setContentView(viewProfile);
        userView = findViewById(R.id.UserView);
        userView.setText(currentUser.getUserName());
        emailView = findViewById(R.id.emailView);
        emailView.setText(currentUser.getEmail());
        phoneView = findViewById(R.id.phoneView);
        phoneView.setText(currentUser.getPhoneNumber());
    }

    public void editProfile(View v) {
        setContentView(editProfile);
        creating = false;
    }

    public void addProfile(View v) {
        setContentView(editProfile);
        String[] packageName = getClass().getPackage().getName().split(".");
        String logtag = packageName[packageName.length - 1] + ".profileActivity";
        Log.d(logtag, "editProfile's class: " + editProfile.getClass().getName());
        creating = true;
    }

    public void profileDone(View v) {
        setContentView(viewProfile);
        // TODO handle profile creation.
        if (creating) {
            //check username for invalid
            //User email confirmation authentication
            currentUser = new User(userEdit.getText().toString(), emailEdit.getText().toString(), phoneEdit.getText().toString());
            userView.setText(userEdit.getText());
            emailView.setText(emailEdit.getText());
            phoneView.setText(phoneEdit.getText());
        } else {
            currentUser.setUserName(userEdit.getText().toString());
            if(currentUser.getUserName().equals(userEdit.getText().toString())) {
                userView.setText(userEdit.getText());
            } else {
                Toast.makeText(this,"User name already existed",Toast.LENGTH_SHORT).show();
            }
            if(emailView.getText() != emailEdit.getText()) {
                // TODO pull up the authentication fragment to reauthenticate
                // changing emails needs this as it changes the FirebaseUser object
                // there is no point in doing this if email does not change.
                emailView.setText(emailEdit.getText());
            }
            currentUser.setPhoneNumber(phoneEdit.getText().toString());
            phoneView.setText(phoneEdit.getText());
        }
    }
}