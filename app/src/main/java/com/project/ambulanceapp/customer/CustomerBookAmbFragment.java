package com.project.ambulanceapp.customer;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.project.ambulanceapp.ConnectionDetector;
import com.project.ambulanceapp.GPSTracker;
import com.project.ambulanceapp.PromptDialog;
import com.project.ambulanceapp.R;
import com.project.ambulanceapp.SpinnerAdapter;
import com.project.ambulanceapp.driver.Driver;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import static androidx.constraintlayout.widget.Constraints.TAG;


public class CustomerBookAmbFragment extends Fragment {

    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    private double mLatitude;
    private double mLongitude;
    private GPSTracker gpsTracker;

    private List<Driver> driversList;
    private TextView tvLocation;
    private TextView tvDate;
    private Spinner driverSpinner;
    private String vehicleType = "";
    private String selDriverId = "";
    private PromptDialog promptDialog;
    private String selected_dateTime = "";
    private TextInputLayout notesLyt;
    private RelativeLayout lyt_parent;
    private ConnectionDetector connectionDetector;
    private FirebaseAuth firebaseAuth;

    private DatabaseReference mDatabase;

    public CustomerBookAmbFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview = inflater.inflate(R.layout.fragment_customer_book_amb, container, false);

        initComponents(rootview);

        return rootview;
    }

    @Override
    public void onStart() {
        super.onStart();
        gpsTracker.getLocation();

    }

    private void initComponents(View v) {

        gpsTracker = new GPSTracker(getContext());
        mDatabase = FirebaseDatabase.getInstance().getReference();
        promptDialog = new PromptDialog(getActivity());
        connectionDetector = new ConnectionDetector(getContext());
        firebaseAuth = FirebaseAuth.getInstance();

        LinearLayout dateTimeLyt = v.findViewById(R.id.dateTimeLyt);
        tvDate = v.findViewById(R.id.tvdate);
        TextView tvTime = v.findViewById(R.id.tvTime);
        tvTime.setVisibility(View.GONE);
        tvLocation = v.findViewById(R.id.currentLocation);
        notesLyt = v.findViewById(R.id.notesLyt);
        MaterialButton nextBtn = v.findViewById(R.id.nextBtn);
        lyt_parent = v.findViewById(R.id.lyt_parent);
        MaterialSpinner spinner = v.findViewById(R.id.spinner);
        spinner.setItems("Basic", "Advanced", "Transport", "Neonatal", "Mortuary/Hearse", "Air");
        driverSpinner = v.findViewById(R.id.driverSpinner);

        Places.initialize(getContext(), getString(R.string.apiKey));
        PlacesClient placesClient = Places.createClient(getContext());

        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));
        autocompleteFragment.setCountry("GH");
        autocompleteFragment.setTypeFilter(TypeFilter.ADDRESS);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i(TAG, "########################################");
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });

        if (gpsTracker.canGetLocation()) {

            mLongitude = gpsTracker.getLongitude();
            mLatitude = gpsTracker.getLatitude();

            String address = gpsTracker.getCompleteAddressString(mLatitude, mLongitude);
            tvLocation.setText(address);


        } else {

            gpsTracker.showSettingsAlert();
        }

        dateTimeLyt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePicker();
            }
        });

        spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                vehicleType = item.toString();
                getDriversList(item.toString());

            }
        });

        tvLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String date_selected = tvDate.getText().toString();
                String notes = notesLyt.getEditText().getText().toString();


                if(validate_data(date_selected, notes, vehicleType, selDriverId, mLatitude, mLongitude)) {

                    if(connectionDetector.isNetworkAvailable()) {

                        sendRequest(date_selected, mLatitude, mLongitude, vehicleType, selDriverId, notes);

                    } else {
                        Snackbar.make(lyt_parent, getString(R.string.no_internet), Snackbar.LENGTH_LONG);
                    }

                }

            }
        });

    }

    private boolean validate_data(String date_selected, String notes, String vehicleType, String selDriverId,
                                  double mLatitude, double mLongitude) {

        if(date_selected.isEmpty() && vehicleType.isEmpty() && selDriverId.isEmpty()) {

            Snackbar.make(lyt_parent, getString(R.string.fields_required), Snackbar.LENGTH_LONG).show();
            return false;
        }

        if(date_selected.isEmpty()) {
            Snackbar.make(lyt_parent, getString(R.string.date_required), Snackbar.LENGTH_LONG).show();
            return false;
        }

        if(vehicleType.isEmpty()) {
            Snackbar.make(lyt_parent, getString(R.string.vtype_required), Snackbar.LENGTH_LONG).show();
            return false;
        }

        if(selDriverId.isEmpty()) {
            Snackbar.make(lyt_parent, getString(R.string.select_driver_required), Snackbar.LENGTH_LONG).show();
            return false;
        }

        if(mLatitude == 0) {
            Snackbar.make(lyt_parent, getString(R.string.no_location), Snackbar.LENGTH_LONG).show();
            return false;
        }

        if(mLongitude == 0) {
            Snackbar.make(lyt_parent, getString(R.string.no_location), Snackbar.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    private void datePicker() {
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        selected_dateTime = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;

                        timePicker();

                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    private void timePicker() {
        final Calendar c = Calendar.getInstance();
        int mHour = c.get(Calendar.HOUR_OF_DAY);
        int mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {
                        String sel_time = selected_dateTime + " " + hourOfDay + ":" + minute + ":00";
//                        tvTime.setText(sel_time);
                        tvDate.setText(PromptDialog.convertToDate(sel_time));

                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();
    }

    private void getDriversList(String ambType) {

        driversList = new ArrayList<>();
        final DatabaseReference driversRef = mDatabase.child("users").child("drivers");
        final Query data = driversRef.orderByChild("vehicle_type").equalTo(ambType);
        data.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.getValue() != null) {
                    for (DataSnapshot driverSnapshot : dataSnapshot.getChildren()) {
                        Driver driver = driverSnapshot.getValue(Driver.class);
                        driversList.add(driver);
                    }

                    final SpinnerAdapter dataAdapter = new SpinnerAdapter(getContext(),
                            R.layout.spinner_item, driversList);
                    driverSpinner.setAdapter(dataAdapter);

                    driverSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                               selDriverId = dataAdapter.getItem(position).getUid();

                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                            selDriverId = "";
                        }
                    });

                } else {

//                    driverSpinner("");
                    driversList.clear();
                    SpinnerAdapter dataAdapter = new SpinnerAdapter(getContext(),
                            R.layout.spinner_item, driversList);
                    driverSpinner.setAdapter(dataAdapter);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.i("data", "$$$$$$$$$$$$$$$$ CANCELLED $$$$$$$$$$$$$$$$$$");
                Log.i("data", databaseError.getMessage());
            }
        });

    }

    private void sendRequest(String date_selected, double mLatitude, double mLongitude,
                             String vehicleType, String selDriverId, String notes) {
        promptDialog.startLoading();
        String user_uid = firebaseAuth.getCurrentUser().getUid();

        DatabaseReference requestsRef = mDatabase.child("requests").push();
        String requestId = requestsRef.getKey();

        CustomerRequest customerRequest = new CustomerRequest(date_selected, requestId, user_uid,
                mLatitude, mLongitude, selDriverId, 0, 0, false, notes);

        requestsRef.setValue(customerRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {

                    promptDialog.stopLoading();
                    showAlert(getContext());


                } else {
                    promptDialog.stopLoading();
                    Snackbar.make(lyt_parent, getString(R.string.error_occurred), Snackbar.LENGTH_LONG).show();
                }
            }
        });

    }

    private void showAlert(final Context ctx) {
        AlertDialog dialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle(R.string.app_name);
        builder.setCancelable(false);
        builder.setMessage("Request sent successfully. View bookings to check status.");
        builder.setPositiveButton(ctx.getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent
                        = new Intent(getContext(),
                        HomeActivity.class);
                intent.putExtra("src", "home");
                intent.putExtra("navItem", 0);
                startActivity(intent);
                getActivity().finish();
            }
        });
        dialog = builder.create();
        dialog.getWindow().getAttributes().windowAnimations = R.style.AlertScale;
        dialog.show();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted.
//                getLastLocation();
                gpsTracker.getLocation();
            } else {
                // Permission denied.

                // Notify the user via a SnackBar that they have rejected a core permission for the
                // app, which makes the Activity useless. In a real app, core permissions would
                // typically be best requested during a welcome-screen flow.

                // Additionally, it is important to remember that a permission might have been
                // rejected without asking the user for permission (device policy or "Never ask
                // again" prompts). Therefore, a user interface affordance is typically implemented
                // when permissions are denied. Otherwise, your app could appear unresponsive to
                // touches or interactions which have required permissions.
//                showAlertDenied(getContext());
                gpsTracker.showSettingsAlert();
            }
        }
    }


}
