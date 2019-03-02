package ca.ualberta.cs.phebert.litx;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.view.View;
import android.widget.TextView;

public class ProfileActivity extends AppCompatActivity {
    private View viewProfile;
    private View editProfile;
    private User currentUser;
    private boolean creating; // might be a bad idea to create profiles

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewProfile = getLayoutInflater().inflate(R.layout.view_profile, null);
        editProfile = getLayoutInflater().inflate(R.layout.edit_profile, null);
        setContentView(R.layout.view_profile);
        ((TextView) findViewById(R.id.UserView)).setText(currentUser.getUserName());
        ((TextView) findViewById(R.id.emailView)).setText(currentUser.getEmail());
        ((TextView) findViewById(R.id.phoneView)).setText(currentUser.getPhoneNumber());
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
        setContentView(editProfile);
        // TODO handle profile editing/creation.
        if (creating) {

        } else {

        }
    }
}