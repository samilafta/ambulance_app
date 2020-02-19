package com.project.ambulanceapp.customer;

import android.content.Intent;
import android.os.Bundle;

import android.os.Handler;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.project.ambulanceapp.ConnectionDetector;
import com.project.ambulanceapp.MainActivity;
import com.project.ambulanceapp.PromptDialog;
import com.project.ambulanceapp.R;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private PromptDialog promptDialog;
    private ConnectionDetector connectionDetector;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseAuth firebaseAuth;

    private static final String TAG_HOME = "home";
    private static final String TAG_BOOK = "book";
    private static final String TAG_BOOKINGS_LIST = "bookings_list";
    private static final String TAG_EMERGENCY = "emergency";
    private static final String TAG_CONTACTS = "contacts";
    public static String CURRENT_TAG = TAG_HOME;
    public static int navItemIndex = 0;
    private Handler mHandler;

    private String[] activityTitles;
    private boolean shouldLoadHomeFragOnBackPress = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initComponents(toolbar);

        if(getIntent() != null && getIntent().hasExtra("src") && getIntent().hasExtra("navItem")) {
            CURRENT_TAG = getIntent().getStringExtra("src");
            navItemIndex = getIntent().getIntExtra("navItem", 0);
        }

        if (savedInstanceState == null) {
            loadHomeFragment();
        }
    }

    private void initComponents(Toolbar toolbar) {

        mHandler = new Handler();
        promptDialog = new PromptDialog(this);
        connectionDetector = new ConnectionDetector(this);
        firebaseAuth = FirebaseAuth.getInstance();

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    Intent intent
                            = new Intent(HomeActivity.this,
                            MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }
        };

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        TextView full_name = headerView.findViewById(R.id.full_name);
        full_name.setText(firebaseAuth.getCurrentUser().getDisplayName());
        TextView email = headerView.findViewById(R.id.email);
        email.setText(firebaseAuth.getCurrentUser().getEmail());

        activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            return;
        }

        if (shouldLoadHomeFragOnBackPress) {
            // checking if user is on other navigation menu
            // rather than home
            if (navItemIndex != 0) {
                navItemIndex = 0;
                CURRENT_TAG = TAG_HOME;
                loadHomeFragment();
                return;
            }
        }

        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_logout) {

            if(connectionDetector.isNetworkAvailable()) {
                promptDialog.startLoading();
                try {
                    firebaseAuth.signOut();
                    promptDialog.deleteUserLoginDetails(getApplicationContext());
                    promptDialog.stopLoading();
                    Toast.makeText(this, "Customer Sign out!", Toast.LENGTH_SHORT).show();
                }catch (Exception e) {
                    promptDialog.stopLoading();
                }

            } else {
                promptDialog.showAlert(this, getString(R.string.no_internet));
            }

        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_HOME;
        } else if (id == R.id.nav_book) {
            navItemIndex = 1;
            CURRENT_TAG = TAG_BOOK;

        } else if (id == R.id.nav_view) {
            navItemIndex = 2;
            CURRENT_TAG = TAG_BOOKINGS_LIST;
        } else if (id == R.id.nav_emergency) {
            navItemIndex = 3;
            CURRENT_TAG = TAG_EMERGENCY;
        } else if (id == R.id.nav_emergency_contacts) {
            navItemIndex = 3;
            CURRENT_TAG = TAG_EMERGENCY;
    }
        else {
            navItemIndex = 0;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        loadHomeFragment();

        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    public void onStop(){
        super.onStop();
        if(authStateListener != null){
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }

    private Fragment getHomeFragment() {
        switch (navItemIndex) {
            case 0:
                return new CustomerHomeFragment();
            case 1:
                return new CustomerBookAmbFragment();
            case 2:
                return new CustomerBookingsFragment();
            case 3:
                return new CustomerEmergencyFragment();
            case 4:
                return new CustomerContactsFragment();
            default:
                return new CustomerHomeFragment();
        }
    }

    private void loadHomeFragment() {

        // set toolbar title
        setToolbarTitle();

        // if user select the current navigation menu again, don't do anything
        // just close the navigation drawer
        if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
//            drawer.closeDrawers();
            return;
        }

        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                // update the main content by replacing fragments
                Fragment fragment = getHomeFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.frameContainer, fragment, CURRENT_TAG);
                fragmentTransaction.commitAllowingStateLoss();
            }
        };

        // If mPendingRunnable is not null, then add to the message queue
        if (mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
        }

        //Closing drawer on item click
//        drawer.closeDrawers();

    }

    private void setToolbarTitle() {
        setTitle(activityTitles[navItemIndex]);
    }

}
