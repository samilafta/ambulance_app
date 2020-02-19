package com.project.ambulanceapp.customer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.ambulanceapp.ConnectionDetector;
import com.project.ambulanceapp.PromptDialog;
import com.project.ambulanceapp.R;

public class BookingsDetailActivity extends AppCompatActivity {

    private PromptDialog promptDialog;
    private ConnectionDetector connectionDetector;
    private DatabaseReference databaseReference;
    private TextView fname, lname, ambType, ambNum, notes, toDestination;
    private MaterialButton call, cancel, feedback, track;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookings_detail);

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

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(request_date);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationIcon(R.drawable.ic_back);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onBackPressed();
                finish();

            }
        });

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
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()) {
                    notes.setText(PromptDialog.toCamelCase(String.valueOf(dataSnapshot.child("request_notes").getValue())));
                    toDestination.setText(PromptDialog.toCamelCase(String.valueOf(dataSnapshot.child("to_destination").getValue())));
                    getDriverDetails(String.valueOf(dataSnapshot.child("driver_uid").getValue()));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.i("error", databaseError.getMessage());
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
                            makeCall(String.valueOf(dataSnapshot.child("phone").getValue()));
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void makeCall(String phone_num) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phone_num));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

}
