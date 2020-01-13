package com.project.ambulanceapp.customer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.project.ambulanceapp.ConnectionDetector;
import com.project.ambulanceapp.PromptDialog;
import com.project.ambulanceapp.R;
import com.project.ambulanceapp.SpacingItemDecoration;
import com.project.ambulanceapp.driver.Driver;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class CustomerBookingsFragment extends Fragment {

    private RecyclerView recyclerView;
    private TextView tvMessage;
    private PromptDialog promptDialog;
    private ConnectionDetector connectionDetector;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private List<CustomerRequest> requestsList;
    private CustomerBookingsAdapter customerBookingsAdapter;

    public CustomerBookingsFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview = inflater.inflate(R.layout.fragment_customer_bookings, container, false);

        initComponents(rootview);
        displayData();

        return rootview;
    }

    private void initComponents(View v) {

        promptDialog = new PromptDialog(getActivity());
        connectionDetector = new ConnectionDetector(getContext());
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        recyclerView = v.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new SpacingItemDecoration(1, 15, true));
        recyclerView.setHasFixedSize(true);
        tvMessage = v.findViewById(R.id.tvMessage);

    }

    private void displayData() {
        if(connectionDetector.isNetworkAvailable()) {

            loadData();

        } else {
            tvMessage.setText(getString(R.string.no_internet));
            tvMessage.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
    }

    private void loadData() {

        promptDialog.startLoading();
        requestsList = new ArrayList<>();
        String user_uid = firebaseAuth.getCurrentUser().getUid();
        final DatabaseReference driversRef = databaseReference.child("requests");
        final Query data = driversRef.orderByChild("user_uid").equalTo(user_uid);

        data.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                promptDialog.stopLoading();

                if(dataSnapshot.getValue() != null) {

                    for (DataSnapshot requestSnapshot : dataSnapshot.getChildren()) {
                        CustomerRequest customerRequest = requestSnapshot.getValue(CustomerRequest.class);
                        requestsList.add(customerRequest);
                    }

                    customerBookingsAdapter = new CustomerBookingsAdapter(getContext(), requestsList);
                    recyclerView.setAdapter(customerBookingsAdapter);

                    customerBookingsAdapter.setOnItemClickListener(new CustomerBookingsAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, CustomerRequest obj, int position) {
                            Intent intent = new Intent(getContext(), BookingsDetailActivity.class);
                            intent.putExtra("request_uid", obj.getRequest_uid());
                            intent.putExtra("request_date", obj.getRequest_date());
                            startActivity(intent);
                        }
                    });

                }
                else {
                    tvMessage.setText(getString(R.string.no_record));
                    tvMessage.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Log.e("error", databaseError.getMessage());

                promptDialog.stopLoading();
                tvMessage.setText(getString(R.string.error_occurred));
                tvMessage.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);


            }
        });



    }

}
