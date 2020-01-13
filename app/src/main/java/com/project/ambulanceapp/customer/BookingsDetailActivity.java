package com.project.ambulanceapp.customer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.project.ambulanceapp.R;

public class BookingsDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookings_detail);

        if(getIntent() != null && getIntent().hasExtra("request_uid") && getIntent().hasExtra("request_date")) {

            String request_uid = getIntent().getStringExtra("request_uid");
            String request_date = getIntent().getStringExtra("request_date");

            initToolbar(request_date);
            initComponents();

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

    private void initComponents() {




    }

}
