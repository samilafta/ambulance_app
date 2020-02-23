package com.project.ambulanceapp.driver;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;

import com.project.ambulanceapp.R;
import com.project.ambulanceapp.customer.HomeActivity;

public class DriverHomeFragment extends Fragment {

    private static final String TAG_HOME = "home";
    private static final String TAG_BOOK = "book";
    private static final String TAG_BOOKINGS_LIST = "bookings_list";
    private static final String TAG_EMERGENCY = "emergency";

    public DriverHomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview = inflater.inflate(R.layout.fragment_driver_home, container, false);

        initComponents(rootview);

        return rootview;
    }

    private void initComponents(View v) {

        LinearLayout view_bookings = (LinearLayout) v.findViewById(R.id.view_appointment);
        LinearLayout emergency = (LinearLayout) v.findViewById(R.id.emergency);


        view_bookings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent
                        = new Intent(getContext(),
                        DriverHomeActivity.class);
                intent.putExtra("src", TAG_BOOKINGS_LIST);
                intent.putExtra("navItem", 1);
                startActivity(intent);
            }
        });

        emergency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent
                        = new Intent(getContext(),
                        DriverHomeActivity.class);
                intent.putExtra("src", TAG_EMERGENCY);
                intent.putExtra("navItem", 2);
                startActivity(intent);
            }
        });


    }

}
