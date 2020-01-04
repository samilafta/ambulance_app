package com.project.ambulanceapp.customer;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.project.ambulanceapp.PromptDialog;
import com.project.ambulanceapp.R;

public class CustomerHomeFragment extends Fragment {

    private PromptDialog promptDialog;
    private static final String TAG_HOME = "home";
    private static final String TAG_BOOK = "book";
    private static final String TAG_BOOKINGS_LIST = "bookings_list";
    private static final String TAG_EMERGENCY = "emergency";

    public CustomerHomeFragment() {
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
        View rootview = inflater.inflate(R.layout.fragment_customer_home, container, false);

        initComponents(rootview);

        return rootview;
    }

    private void initComponents(View v) {

        promptDialog = new PromptDialog(getActivity());
        LinearLayout book_appointment = (LinearLayout) v.findViewById(R.id.book_appointment);
        LinearLayout view_bookings = (LinearLayout) v.findViewById(R.id.view_appointment);
        LinearLayout emergency = (LinearLayout) v.findViewById(R.id.emergency);

        book_appointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent
                        = new Intent(getContext(),
                        HomeActivity.class);
                intent.putExtra("src", TAG_BOOK);
                intent.putExtra("navItem", 1);
                startActivity(intent);
            }
        });

        view_bookings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent
                        = new Intent(getContext(),
                        HomeActivity.class);
                intent.putExtra("src", TAG_BOOKINGS_LIST);
                intent.putExtra("navItem", 2);
                startActivity(intent);
            }
        });

        emergency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent
                        = new Intent(getContext(),
                        HomeActivity.class);
                intent.putExtra("src", TAG_EMERGENCY);
                intent.putExtra("navItem", 3);
                startActivity(intent);
            }
        });


    }

}
