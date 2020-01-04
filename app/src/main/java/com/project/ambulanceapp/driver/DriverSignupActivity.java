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
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.project.ambulanceapp.ConnectionDetector;
import com.project.ambulanceapp.MainActivity;
import com.project.ambulanceapp.PromptDialog;
import com.project.ambulanceapp.R;
import com.project.ambulanceapp.customer.Customer;
import com.project.ambulanceapp.customer.HomeActivity;

public class DriverSignupActivity extends AppCompatActivity {

    private TextInputLayout emailLyt, passwordLyt, firstNameLyt, lastNameLyt, phoneLyt, vehicleTypeLyt, vehicleNumberLyt;
    private ConnectionDetector connectionDetector;
    private PromptDialog promptDialog;
    private RelativeLayout lyt_parent;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference mDatabase;
    private MaterialSpinner spinner;
    private String vehicleType = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_driver_signup);

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
        vehicleNumberLyt = findViewById(R.id.vehicleNumLyt);
        vehicleTypeLyt = findViewById(R.id.vehicleTypeLyt);
        spinner = findViewById(R.id.spinner);
        MaterialButton loginBtn = findViewById(R.id.loginBtn);
        MaterialButton signupBtn = findViewById(R.id.signupBtn);

        spinner.setItems("Basic", "Advanced", "Transport", "Neonatal", "Mortuary/Hearse", "Air");
        spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                vehicleType = item.toString();
            }
        });

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String fname = firstNameLyt.getEditText().getText().toString();
                final String lname = lastNameLyt.getEditText().getText().toString();
                final String email = emailLyt.getEditText().getText().toString();
                String password = passwordLyt.getEditText().getText().toString();
                final String phone = phoneLyt.getEditText().getText().toString();
                final String vehicleNumber = vehicleNumberLyt.getEditText().getText().toString();

                if(validate(fname, lname, email, password, phone, vehicleType, vehicleNumber)) {

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
                                                    writeNewUser(authResult.getUser().getUid(), fname, lname, email,
                                                            phone, vehicleType, vehicleNumber);
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
                        = new Intent(getApplicationContext(),
                        MainActivity.class);
                startActivity(intent);
            }
        });

    }

    private boolean validate(String fname, String lname, String eMail, String pwd, String phone,
                             String vehicleType, String vehicleNumber) {

        if(eMail.isEmpty() && pwd.isEmpty() && fname.isEmpty() && lname.isEmpty()) {

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

        if(vehicleNumber.isEmpty()) {
            Snackbar.make(lyt_parent, getText(R.string.vnumber_required), Snackbar.LENGTH_LONG).show();
            return false;
        }

        if(vehicleType.isEmpty()) {
            Snackbar.make(lyt_parent, getText(R.string.vtype_required), Snackbar.LENGTH_LONG).show();
            return false;
        }


        return true;

    }

    private void writeNewUser(String userId, String fname, String lname, String email, String phone,
                              String vehicleType, String vehicleNumber) {

        Driver driver = new Driver(fname, lname, email, userId, phone, vehicleType, vehicleNumber);

        mDatabase.child("users").child("drivers").child(userId).setValue(driver).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    promptDialog.stopLoading();
                    firstNameLyt.getEditText().setText("");
                    lastNameLyt.getEditText().setText("");
                    emailLyt.getEditText().setText("");
                    passwordLyt.getEditText().setText("");
                    phoneLyt.getEditText().setText("");
                    vehicleNumberLyt.getEditText().setText("");

                    promptDialog.setLoginType(getApplicationContext(), "driver");
                    Intent intent
                            = new Intent(getApplicationContext(),
                            DriverHomeActivity.class);
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
