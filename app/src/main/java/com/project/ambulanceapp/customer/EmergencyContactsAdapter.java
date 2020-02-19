package com.project.ambulanceapp.customer;

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

import java.util.List;

public class EmergencyContactsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Emergency> items;
    private OriginalViewHolder view;

    private Context ctx;
    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, Emergency obj, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public EmergencyContactsAdapter(Context context, List<Emergency> items) {
        this.items = items;
        this.ctx = context;
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        public TextView fullName, phoneNum;
        private MaterialButton callBtn, deleteBtn;
        public LinearLayout lyt_parent;

        public OriginalViewHolder(View v) {
            super(v);
            fullName = v.findViewById(R.id.full_name);
            phoneNum = v.findViewById(R.id.phone_number);
            callBtn = v.findViewById(R.id.call);
            deleteBtn = v.findViewById(R.id.delete);
            lyt_parent = v.findViewById(R.id.lyt_parent);
        }
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.emergency_contacts_list, parent, false);
        vh = new OriginalViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int position) {
        final Emergency s = items.get(position);
        final OriginalViewHolder view = (OriginalViewHolder) viewHolder;

        view.fullName.setText(s.getFname());
        view.phoneNum.setText(s.getPhone());

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
