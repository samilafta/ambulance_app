package com.project.ambulanceapp.driver;

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
import com.google.firebase.auth.FirebaseUser;
import com.project.ambulanceapp.ConnectionDetector;
import com.project.ambulanceapp.PromptDialog;
import com.project.ambulanceapp.R;
import com.project.ambulanceapp.customer.HomeActivity;

public class DriverLoginActivity extends AppCompatActivity {

    private TextInputLayout emailLyt, passwordLyt;
    private ConnectionDetector connectionDetector;
    private PromptDialog promptDialog;
    private RelativeLayout lyt_parent;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_driver_login);

        initComponents();
    }

    private void initComponents() {

        FirebaseApp.initializeApp(this);
        firebaseAuth = FirebaseAuth.getInstance();

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Intent intent
                            = new Intent(DriverLoginActivity.this,
                            HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }
        };

        connectionDetector = new ConnectionDetector(this);
        promptDialog = new PromptDialog(this);

        lyt_parent = findViewById(R.id.lyt_parent);
        emailLyt = findViewById(R.id.emailLyt);
        passwordLyt = findViewById(R.id.passwordLyt);
        MaterialButton loginBtn = findViewById(R.id.loginBtn);
        MaterialButton signupBtn = findViewById(R.id.signupBtn);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = emailLyt.getEditText().getText().toString();
                String password = passwordLyt.getEditText().getText().toString();

                if(validate(email, password)) {

                    promptDialog.startLoading();

                    if(connectionDetector.isNetworkAvailable()) {

                        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                promptDialog.stopLoading();

                                task.addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                    @Override
                                    public void onSuccess(AuthResult authResult) {

                                        promptDialog.setLoginType(getApplicationContext(), "driver");
                                        Intent intent
                                                = new Intent(getApplicationContext(),
                                                DriverHomeActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);

                                    }
                                });
                                task.addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

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

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent
                        = new Intent(getApplicationContext(),
                        DriverSignupActivity.class);
                startActivity(intent);
            }
        });

    }

    private boolean validate(String eMail, String pwd) {
        emailLyt.setError(null);
        emailLyt.setErrorEnabled(false);
        passwordLyt.setError(null);
        passwordLyt.setErrorEnabled(false);

        if(eMail.isEmpty() && pwd.isEmpty()) {

            Snackbar.make(lyt_parent, getText(R.string.fields_required), Snackbar.LENGTH_LONG).show();
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

        return true;

    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    public void onStop(){
        super.onStop();
        if(authStateListener != null){
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }

}
