package com.project.ambulanceapp.customer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentActivity;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.ambulanceapp.ConnectionDetector;
import com.project.ambulanceapp.GPSTracker;
import com.project.ambulanceapp.PromptDialog;
import com.project.ambulanceapp.R;

import java.sql.Connection;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    private GoogleMap mMap;
    private GPSTracker gpsTracker;
    private double mLatitude;
    private double drivermLatitude;
    private double mLongitude;
    private double drivermLongitude;
    private DatabaseReference databaseReference;
    private ConnectionDetector connectionDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        if(getIntent() != null && getIntent().hasExtra("request_uid")) {

            String request_uid = getIntent().getStringExtra("request_uid");

            initToolbar();
            initComponents(request_uid);

        } else {
            onBackPressed();
        }


    }

    private void initToolbar() {

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Tracking");
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

    private void initComponents(String request_uid) {

        gpsTracker = new GPSTracker(this);
        connectionDetector = new ConnectionDetector(this);
        databaseReference = FirebaseDatabase.getInstance().getReference();

        if (gpsTracker.canGetLocation()) {

            mLongitude = gpsTracker.getLongitude();
            mLatitude = gpsTracker.getLatitude();

        } else {

            gpsTracker.showSettingsAlert();
        }

        databaseReference.child("requests").child(request_uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.getValue() != null) {

                    Log.i("data", "$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
                    Log.i("data", String.valueOf(dataSnapshot.getValue()));

                    drivermLatitude = Double.valueOf(String.valueOf(dataSnapshot.child("driver_lat").getValue()));
                    drivermLongitude = Double.valueOf(String.valueOf(dataSnapshot.child("driver_lon").getValue()));

                    Toast.makeText(getApplicationContext(), String.valueOf(dataSnapshot.getValue()), Toast.LENGTH_LONG);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng position = new LatLng(mLatitude, mLongitude);
        mMap.addMarker(new MarkerOptions().position(position).title(gpsTracker.getCompleteAddressString(mLatitude, mLongitude)));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(position));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(16), 2000, null);

        LatLng driverPosition = new LatLng(drivermLatitude, drivermLongitude);
        mMap.addMarker(new MarkerOptions().position(driverPosition).title(gpsTracker.getCompleteAddressString(drivermLatitude, drivermLongitude)));

    }

    @Override
    public void onStart() {
        super.onStart();
        gpsTracker.getLocation();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted.
                gpsTracker.getLocation();
            } else {
                // Permission denied.
                gpsTracker.showSettingsAlert();
            }
        }
    }


}
