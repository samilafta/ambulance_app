package com.project.ambulanceapp.customer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.RelativeLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.project.ambulanceapp.ConnectionDetector;
import com.project.ambulanceapp.MainActivity;
import com.project.ambulanceapp.PromptDialog;
import com.project.ambulanceapp.R;

public class SignupActivity extends AppCompatActivity {

    private TextInputLayout emailLyt, passwordLyt, firstNameLyt, lastNameLyt, phoneLyt;
    private ConnectionDetector connectionDetector;
    private PromptDialog promptDialog;
    private RelativeLayout lyt_parent;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_signup);

        initComponents();

    }

    private void initComponents() {

        FirebaseApp.initializeApp(this);
        firebaseAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        connectionDetector = new ConnectionDetector(this);
        promptDialog = new PromptDialog(this);

        lyt_parent = findViewById(R.id.lyt_parent);
        emailLyt = findViewById(R.id.emailLyt);
        phoneLyt = findViewById(R.id.phoneLyt);
        passwordLyt = findViewById(R.id.passwordLyt);
        lastNameLyt = findViewById(R.id.lnameLyt);
        firstNameLyt = findViewById(R.id.fnameLyt);
        MaterialButton loginBtn = findViewById(R.id.loginBtn);
        MaterialButton signupBtn = findViewById(R.id.signupBtn);

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String fname = firstNameLyt.getEditText().getText().toString();
                final String lname = lastNameLyt.getEditText().getText().toString();
                final String email = emailLyt.getEditText().getText().toString();
                String password = passwordLyt.getEditText().getText().toString();
                final String phone = phoneLyt.getEditText().getText().toString();

                if(validate(fname, lname, email, password, phone)) {

                    promptDialog.startLoading();

                    if(connectionDetector.isNetworkAvailable()) {

                        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                task.addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                    @Override
                                    public void onSuccess(final AuthResult authResult) {

                                        String mName = fname + " " + lname;
                                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                .setDisplayName(mName).build();

                                        authResult.getUser().updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()) {
                                                    writeNewUser(authResult.getUser().getUid(), fname, lname, email, phone);
                                                }
                                                else {
                                                    Snackbar.make(lyt_parent, getString(R.string.error_occurred), Snackbar.LENGTH_LONG).show();
                                                }
                                            }
                                        });

                                    }
                                });
                                task.addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                        promptDialog.stopLoading();
                                        Snackbar.make(lyt_parent, e.getMessage(), Snackbar.LENGTH_LONG).show();

                                    }
                                });

                            }
                        });

                    } else {
                        Snackbar.make(lyt_parent, getString(R.string.no_internet), Snackbar.LENGTH_LONG).show();
                    }
                }

            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent
                        = new Intent(SignupActivity.this,
                        MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    private boolean validate(String fname, String lname, String eMail, String pwd, String phone) {
        firstNameLyt.setError(null);
        firstNameLyt.setErrorEnabled(false);
        lastNameLyt.setError(null);
        lastNameLyt.setErrorEnabled(false);
        emailLyt.setError(null);
        emailLyt.setErrorEnabled(false);
        phoneLyt.setError(null);
        phoneLyt.setErrorEnabled(false);
        passwordLyt.setError(null);
        passwordLyt.setErrorEnabled(false);

        if(eMail.isEmpty() && pwd.isEmpty() && fname.isEmpty() && lname.isEmpty() && phone.isEmpty()) {

            Snackbar.make(lyt_parent, getText(R.string.fields_required), Snackbar.LENGTH_LONG).show();
            return false;
        }

        if(fname.isEmpty()) {
            Snackbar.make(lyt_parent, getText(R.string.fname_required), Snackbar.LENGTH_LONG).show();
            return false;
        }

        if(fname.length() < 3) {
            Snackbar.make(lyt_parent, getText(R.string.fname_length), Snackbar.LENGTH_LONG).show();
            return false;
        }

        if(lname.isEmpty()) {
            Snackbar.make(lyt_parent, getText(R.string.lname_length), Snackbar.LENGTH_LONG).show();
            return false;
        }

        if(lname.length() < 3) {
            Snackbar.make(lyt_parent, getText(R.string.pwd_length), Snackbar.LENGTH_LONG).show();
            return false;
        }

        if(eMail.isEmpty()) {
            Snackbar.make(lyt_parent, getText(R.string.email_required), Snackbar.LENGTH_LONG).show();
            return false;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(eMail).matches()){
            Snackbar.make(lyt_parent, getText(R.string.email_invalid), Snackbar.LENGTH_LONG).show();
            return false;
        }

        if(pwd.isEmpty()) {
            Snackbar.make(lyt_parent, getText(R.string.pwd_required), Snackbar.LENGTH_LONG).show();
            return false;
        }

        if(pwd.length() < 8) {
            Snackbar.make(lyt_parent, getText(R.string.pwd_length), Snackbar.LENGTH_LONG).show();
            return false;
        }

        if(phone.isEmpty()) {
            Snackbar.make(lyt_parent, getText(R.string.phone_required), Snackbar.LENGTH_LONG).show();
            return false;
        }

        if(phone.length() != 10) {
            Snackbar.make(lyt_parent, getText(R.string.phone_length), Snackbar.LENGTH_LONG).show();
            return false;
        }

        return true;

    }

    private void writeNewUser(String userId, String fname, String lname, String email, String phone) {
        Customer user = new Customer(fname, lname, email, userId, phone);

        mDatabase.child("users").child("customers").child(userId).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    promptDialog.stopLoading();
                    firstNameLyt.getEditText().setText("");
                    lastNameLyt.getEditText().setText("");
                    emailLyt.getEditText().setText("");
                    passwordLyt.getEditText().setText("");
                    phoneLyt.getEditText().setText("");

                    promptDialog.setLoginType(getApplicationContext(), "customer");
                    Intent intent
                            = new Intent(SignupActivity.this,
                            HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
//                    promptDialog.showAlert(SignupActivity.this, "Signup Successful. Please Login");

                } else {
                    Snackbar.make(lyt_parent, getString(R.string.error_occurred), Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

}
