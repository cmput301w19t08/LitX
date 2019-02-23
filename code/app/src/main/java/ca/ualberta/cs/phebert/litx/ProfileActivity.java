package ca.ualberta.cs.phebert.litx;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.view.View;

public class ProfileActivity extends AppCompatActivity {
    private View viewProfile;
    private View editProfile;
    private boolean creating;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO properly inflate layouts
        viewProfile = getLayoutInflater().inflate(R.layout.view_profile,null); // null for now, thomas is adding an id to it
        editProfile = null; // R.layout
        setContentView(R.layout.view_profile);

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
    }
}
