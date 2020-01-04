package com.project.ambulanceapp.customer;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.project.ambulanceapp.R;

public class CustomerBookAmbFragment extends Fragment {


    public CustomerBookAmbFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview = inflater.inflate(R.layout.fragment_customer_book_amb, container, false);

        return rootview;
    }

}
