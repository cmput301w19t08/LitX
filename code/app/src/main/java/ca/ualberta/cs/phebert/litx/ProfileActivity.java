package ca.ualberta.cs.phebert.litx;

import android.content.Intent;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity implements UserObserver {
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
    private FirebaseUser user;
    private EditText PasswordEdit; // not on xml

    private String OWNER_USERNAME = "OWNER_USERNAME_FOR_PROFILE";
    private String OWNER_EMAIL = "OWNER_EMAIL_FOR_PROFILE";
    private String OWNER_PHONENUMBER = "OWNER_PHONENUMBER_FOR_PROFILE";


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

        Intent intent = getIntent();
        if (intent != null) {
            userView = findViewById(R.id.UserView);
            emailView = findViewById(R.id.emailView);
            phoneView = findViewById(R.id.phoneView);
            userView.setText(intent.getStringExtra(OWNER_USERNAME));
            emailView.setText(intent.getStringExtra(OWNER_EMAIL));
            phoneView.setText(intent.getStringExtra(OWNER_PHONENUMBER));

        } else {
            userView = findViewById(R.id.UserView);
            emailView = findViewById(R.id.emailView);
            phoneView = findViewById(R.id.phoneView);
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                currentUser = new User(FirebaseAuth.getInstance().getCurrentUser());
                user = FirebaseAuth.getInstance().getCurrentUser();

                currentUser.addObserver(this);
                onUserUpdated(currentUser); // might as well.
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

    // TODO on edit update in database
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
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(userView.getText().toString()).build();
            user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Log.d(LOG_TAG, "User display name added");
                    }
                }
            });
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
            FirebaseAuth.getInstance()
                    .signInWithEmailAndPassword(emailView.getText().toString(),"a").addOnSuccessListener(res -> {
                        Log.v(LOG_TAG,"signed in");
//                        finish();
            }).addOnFailureListener(e -> {
                e.printStackTrace();
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(emailView.getText().toString(),"a23b45")
                        .addOnSuccessListener(res ->{
                            HashMap<String, Object> user = new HashMap<>();
                            user.put("userName", userView.getText().toString());
                            user.put("email",  emailView.getText().toString());
                            user.put("phoneNumber", phoneView.getText().toString());
                            FirebaseFirestore.getInstance()
                                    .collection(User.USER_COLLECTION)
                                    .document(res.getUser().getUid())
                                    .set(user)
                                    .addOnSuccessListener(ign -> {

                                    })
                                    .addOnFailureListener(e2 -> Log.wtf("LitX.User", e));
                            Log.d(LOG_TAG,"signed up");

                        }).addOnFailureListener(e2 -> {
                            Log.e(LOG_TAG, "could not sign up", e2);
                });
            });
          UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                  .setDisplayName(userView.getText().toString()).build();
          user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
              @Override
              public void onComplete(@NonNull Task<Void> task) {
                  if (task.isSuccessful()){
                      Log.d(LOG_TAG, "User display name added");
                  }
              }
          });
          finish();

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