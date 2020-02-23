package com.project.ambulanceapp.customer;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
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

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class CustomerEmergencyFragment extends Fragment {

    private FloatingActionButton fab;
    private TextInputLayout nameEdt, phoneEdt;
    private ConnectionDetector connectionDetector;
    private PromptDialog promptDialog;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private Dialog dialog;
    private RecyclerView recyclerView;
    private TextView tvMessage;
    private List<Emergency> contactsList;
    private EmergencyContactsAdapter emergencyContactsAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview = inflater.inflate(R.layout.fragment_customer_emergency, container, false);

        initComponents(rootview);
        loadContacts();

        return rootview;
    }

    private void initComponents(View v) {
        connectionDetector = new ConnectionDetector(getContext());
        promptDialog = new PromptDialog(getActivity());
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        recyclerView = v.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new SpacingItemDecoration(1, 15, true));
        recyclerView.setHasFixedSize(true);
        tvMessage = v.findViewById(R.id.tvMessage);
        fab = v.findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                displayAddForm();

            }
        });

    }

    private void displayAddForm() {

        dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.add_emergency_contact);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.AlertScale;

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        nameEdt = dialog.findViewById(R.id.nameLyt);
        phoneEdt = dialog.findViewById(R.id.phoneLyt);

        MaterialButton closeBtn = dialog.findViewById(R.id.cancel);
        MaterialButton submitBtn = dialog.findViewById(R.id.add);

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = nameEdt.getEditText().getText().toString();
                String phone = phoneEdt.getEditText().getText().toString();

                if(validateForm(name, phone)) {

                    if(connectionDetector.isNetworkAvailable()) {

                        addContactRequest(name, phone);

                    } else {
                        Toast.makeText(getContext(), getString(R.string.no_internet), Toast.LENGTH_LONG).show();
                    }


                }


            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);

    }

    private boolean validateForm(String name, String phone) {

        nameEdt.setErrorEnabled(false);
        nameEdt.setError("");
        phoneEdt.setErrorEnabled(false);
        phoneEdt.setError("");

        if(name.isEmpty() && phone.isEmpty()) {
            nameEdt.setErrorEnabled(true);
            phoneEdt.setErrorEnabled(true);
            nameEdt.setError("Please enter full name!");
            phoneEdt.setError("Please enter phone number!");
            Toast.makeText(getContext(), "All fields are required!", Toast.LENGTH_LONG).show();
            return false;
        }

        if(name.isEmpty()) {
            nameEdt.setErrorEnabled(true);
            nameEdt.setError("Please enter full name!");
            return false;
        }

        if(phone.isEmpty()) {
            phoneEdt.setErrorEnabled(true);
            phoneEdt.setError("Please enter phone number!");
            return false;
        }

        if(phone.length() != 10) {
            phoneEdt.setErrorEnabled(true);
            phoneEdt.setError("Please enter valid phone number!");
            return false;
        }

        return true;
    }

    private void addContactRequest(String name, String pNumber) {

        promptDialog.startLoading();

        Emergency emergency = new Emergency(name, pNumber);
        String uid = firebaseAuth.getCurrentUser().getUid();

        DatabaseReference dbReference = FirebaseDatabase.getInstance().getReference();
        dbReference.child("users").child("customers").child(uid)
                .child("emergency").push().setValue(emergency).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {

                    promptDialog.stopLoading();
                    dialog.dismiss();
                    promptDialog.showAlert(getContext(), "Emergency Contact added!");
//                    loadContacts();

                } else {
                    promptDialog.stopLoading();
                    promptDialog.showToast(getContext(), getString(R.string.error_occurred));
                }
            }
        });

    }

    private void loadContacts() {

        promptDialog.startLoading();

        final String uid = firebaseAuth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("users").child("customers").child(uid).child("emergency").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.getValue() != null) {

                    contactsList = new ArrayList<>();

                    for (DataSnapshot requestSnapshot : dataSnapshot.getChildren()) {
                        Emergency emergency = requestSnapshot.getValue(Emergency.class);
                        contactsList.add(emergency);
                    }

                    emergencyContactsAdapter = new EmergencyContactsAdapter(getContext(), contactsList);
                    recyclerView.setAdapter(emergencyContactsAdapter);
                    promptDialog.stopLoading();

                    emergencyContactsAdapter.setOnItemClickListener(new EmergencyContactsAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, Emergency obj, int position) {
                            promptDialog.makeCall(getContext(), obj.getPhone());
                        }
                    });

                    emergencyContactsAdapter.setOnItemDeleteListener(new EmergencyContactsAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, Emergency obj, int position) {
                            promptDialog.startLoading();
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                            Query contactQuery = ref.child("users").child("customers").child(uid).child("emergency")
                                    .orderByChild("phone").equalTo(obj.getPhone());

                            contactQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot cSnapshot: dataSnapshot.getChildren()) {
                                        cSnapshot.getRef().removeValue();
                                        promptDialog.stopLoading();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Log.e(TAG, "onCancelled", databaseError.toException());
                                    promptDialog.stopLoading();
                                    promptDialog.showToast(getContext(), getString(R.string.error_occurred));
                                }
                            });
                        }
                    });

                }
                else {
                    promptDialog.stopLoading();
                    tvMessage.setText(getString(R.string.no_record));
                    tvMessage.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}
