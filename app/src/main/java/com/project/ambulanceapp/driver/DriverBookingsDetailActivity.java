package com.project.ambulanceapp.driver;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.ambulanceapp.ConnectionDetector;
import com.project.ambulanceapp.PromptDialog;
import com.project.ambulanceapp.R;
import com.project.ambulanceapp.customer.CustomerRequest;
import com.project.ambulanceapp.customer.MapsActivity;

public class DriverBookingsDetailActivity extends AppCompatActivity {

    private PromptDialog promptDialog;
    private ConnectionDetector connectionDetector;
    private DatabaseReference databaseReference;
    private TextView fname, lname, ambType, ambNum, notes, toDestination;
    private MaterialButton call, cancel, feedback, track;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_bookings_detail);

        if(getIntent() != null && getIntent().hasExtra("request_uid") && getIntent().hasExtra("request_date")) {

            String request_uid = getIntent().getStringExtra("request_uid");
            String request_date = getIntent().getStringExtra("request_date");

            initToolbar(request_date);
            initComponents(request_uid);

        } else {

            onBackPressed();

        }

    }

    private void initToolbar(String request_date){

//        Toolbar toolbar = findViewById(R.id.toolbar);
//        toolbar.setTitle(request_date);
//        toolbar.setTitleTextColor(Color.WHITE);
//        toolbar.setNavigationIcon(R.drawable.ic_back);

//        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(request_date);

//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                onBackPressed();
//                finish();
//
//            }
//        });

    }

    private void initComponents(final String request_uid) {

        promptDialog = new PromptDialog(this);
        connectionDetector = new ConnectionDetector(this);
        databaseReference = FirebaseDatabase.getInstance().getReference();

        fname = findViewById(R.id.fname);
        lname = findViewById(R.id.lname);
        ambType = findViewById(R.id.ambType);
        ambNum = findViewById(R.id.ambNum);
        notes = findViewById(R.id.notes);
        toDestination = findViewById(R.id.toDestination);

        call = findViewById(R.id.call);
        cancel = findViewById(R.id.cancel);
        feedback = findViewById(R.id.feedback);
        track = findViewById(R.id.track);

        databaseReference.child("requests").child(request_uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()) {
                    CustomerRequest request = dataSnapshot.getValue(CustomerRequest.class);
                    notes.setText(request.getRequest_notes());
                    toDestination.setText(request.getTo_destination());
                    getDriverDetails(request.getDriver_uid());

                    if(request.isCancel_status()) {
                        cancel.setEnabled(false);
                        cancel.setText(getString(R.string.cancelled));
                        cancel.setBackgroundTintList(ContextCompat.getColorStateList(DriverBookingsDetailActivity.this, R.color.grey_40));
                    } else {
                        cancel.setEnabled(true);
                        cancel.setText(getString(R.string.cancel_booking));
                        cancel.setBackgroundTintList(ContextCompat.getColorStateList(DriverBookingsDetailActivity.this, R.color.secondaryDarkColor));

                    }

                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            cancelBookingRequest(dataSnapshot);
                        }
                    });

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.i("error", databaseError.getMessage());
                promptDialog.showToast(getApplicationContext(), getString(R.string.error_occurred));
            }
        });

        track.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                intent.putExtra("request_uid", request_uid);
                startActivity(intent);
            }
        });

    }

    private void getDriverDetails(String driver_uid) {

        databaseReference.child("users").child("drivers").child(driver_uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()) {

                    fname.setText(PromptDialog.toCamelCase(String.valueOf(dataSnapshot.child("fname").getValue())));
                    lname.setText(PromptDialog.toCamelCase(String.valueOf(dataSnapshot.child("lname").getValue())));
                    ambNum.setText(String.valueOf(dataSnapshot.child("vehicle_number").getValue()).toUpperCase());
                    ambType.setText(String.valueOf(dataSnapshot.child("vehicle_type").getValue()));

                    call.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            promptDialog.makeCall(getApplicationContext(), String.valueOf(dataSnapshot.child("phone").getValue()));
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void cancelBookingRequest(DataSnapshot dataSnapshot) {
        promptDialog.startLoading();
        DatabaseReference cancelBookingRef = dataSnapshot.getRef().child("cancel_status");
        cancelBookingRef.setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    promptDialog.stopLoading();
                    promptDialog.showToast(getApplicationContext(), getString(R.string.booking_success));
//                    showAlert();

                } else {
                    promptDialog.stopLoading();
                    promptDialog.showToast(getApplicationContext(), getString(R.string.error_occurred));
                }
            }
        });
    }

    private void showAlert() {
        AlertDialog dialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
        builder.setTitle(R.string.app_name);
        builder.setCancelable(false);
        builder.setMessage(getString(R.string.booking_success));
        builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
              dialog.dismiss();
            }
        });
        dialog = builder.create();
//        dialog.getWindow().getAttributes().windowAnimations = R.style.AlertScale;
        dialog.show();
    }

}
