package ca.ualberta.cs.phebert.litx;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.FirebaseAuth;

public class ProfileActivity extends AppCompatActivity {
    private static final String LOG_TAG = "litX.ProfileActivity";
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
        setContentView(editProfile); // findViewById only works for visible view (eg views in the content view.
        userEdit = findViewById(R.id.UserEdit);
        emailEdit = findViewById(R.id.emailEdit);
        phoneEdit = findViewById(R.id.phoneEdit);
        setContentView(viewProfile);
        userView = findViewById(R.id.UserView);
        emailView = findViewById(R.id.emailView);
        phoneView = findViewById(R.id.phoneView);
        if (currentUser != null) {
            userEdit.setText(currentUser.getUserName());
            userView.setText(currentUser.getUserName());
            emailEdit.setText(currentUser.getEmail());
            emailView.setText(currentUser.getEmail());
            phoneEdit.setText(currentUser.getPhoneNumber());
            phoneView.setText(currentUser.getPhoneNumber());
        } else {
            userView.setText("???");
            emailView.setText("???");
            phoneView.setText("???");
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
            // this whole block is based on Authenticate with Firebase Using Email Link in Android,
            // https://firebase.google.com/docs/auth/android/email-link-auth,
            //
            ActionCodeSettings settings = ActionCodeSettings.newBuilder()
                    .setHandleCodeInApp(true)
                    .setAndroidPackageName("ca.ualberta.cs.phebert.litx", true, "1")
                    // package name, install if does not exist, min package version
                    .build();
            FirebaseAuth.getInstance()
                    .sendSignInLinkToEmail(emailEdit.getText().toString(), settings)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(LOG_TAG, "Email sent.");
                                Toast.makeText(ProfileActivity.this,
                                        "We have sent you an email to verify.",
                                        Toast.LENGTH_LONG)
                                        .show();
                            }
                        }
                    });

        }
    }

    @Override
    public void onBackPressed() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            finishAffinity();
        } else {
            finish();
        }
    }
}