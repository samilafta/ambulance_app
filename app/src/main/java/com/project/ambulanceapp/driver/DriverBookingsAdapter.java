package com.project.ambulanceapp.driver;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.ambulanceapp.PromptDialog;
import com.project.ambulanceapp.R;
import com.project.ambulanceapp.customer.CustomerRequest;

import java.util.List;

public class DriverBookingsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<CustomerRequest> items;
    private OriginalViewHolder view;

    private Context ctx;
    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, CustomerRequest obj, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public DriverBookingsAdapter(Context context, List<CustomerRequest> items) {
        this.items = items;
        this.ctx = context;
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        public TextView date, driverName, ambNum, ambType, destination;
        private MaterialButton pendingStatus, successStatus;
        public LinearLayout lyt_parent;

        public OriginalViewHolder(View v) {
            super(v);
            date = v.findViewById(R.id.date);
            driverName = v.findViewById(R.id.driverName);
            pendingStatus = v.findViewById(R.id.pendingStatus);
            successStatus = v.findViewById(R.id.successStatus);
            ambNum = v.findViewById(R.id.ambNum);
            ambType = v.findViewById(R.id.ambType);
            destination = v.findViewById(R.id.toDestination);
            lyt_parent = v.findViewById(R.id.lyt_parent);
        }
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.customer_bookings_list_item, parent, false);
        vh = new OriginalViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int position) {
        final CustomerRequest s = items.get(position);
        final OriginalViewHolder view = (OriginalViewHolder) viewHolder;

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users").child("customers").child(s.getUser_uid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()) {
                    String full_name = dataSnapshot.child("fname").getValue() + " " + dataSnapshot.child("lname").getValue();
                    view.driverName.setText(PromptDialog.toCamelCase(full_name));
//                    view.ambNum.setText(String.valueOf(dataSnapshot.child("vehicle_number").getValue()).toUpperCase());
//                    view.ambType.setText(String.valueOf(dataSnapshot.child("vehicle_type").getValue()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        view.date.setText(s.getRequest_date());
        String dest = "Destination: " + s.getTo_destination();
        view.destination.setText(dest);

        if(s.isRequest_status()) {
            view.successStatus.setVisibility(View.VISIBLE);
            view.pendingStatus.setVisibility(View.GONE);
        } else {
            view.pendingStatus.setVisibility(View.VISIBLE);
            view.successStatus.setVisibility(View.GONE);
        }

        view.lyt_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(v, items.get(position), position);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return items.size();
    }



}
