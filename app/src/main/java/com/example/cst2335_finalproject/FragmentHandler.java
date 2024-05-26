package com.example.cst2335_finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;

import java.util.Locale;
import java.util.Objects;


/* GENERAL EXPLANATION
*   The following code is the logic for getting the user's selected date, and populating the appropriate
*   textviews that give the required information. Between the two fragments is a progress bar lasting 1.5
*   seconds. The code also handles when the data excludes an HDURL (when it's a video) and makes the download
*   button appear or disappear depending on is the data is there.
*
 */

/*  REQUIRED ELEMENTS WITHIN THIS CODE
*       ProgressBar
*       DatePicker
*       Returning the date, url, and hdurl from the nasa API
*       Fragments
 */
public class FragmentHandler extends AppCompatActivity implements Date_Picker_Fragment.FetchImageClickListener {

    private ProgressBar progressBar;
    private Date_Picker_Fragment datePickerFragment;
    private ImageInfo imageInfoFragment;
    private DrawerLayout drawerLayout;
    private TextView activityNameNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_fragment_handler);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // The below code removes the title from the toolbar
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        progressBar = findViewById(R.id.progressBar);
        drawerLayout = findViewById(R.id.drawerLayoutFragment);
        NavigationView navigationView = findViewById(R.id.navView);
        activityNameNav = navigationView.getHeaderView(0).findViewById(R.id.navActivityName);

        activityNameNav.setText(R.string.fragmentHandlerActivity);

        ImageButton helpButton = findViewById(R.id.helpButton);
        helpButton.setOnClickListener(v -> showHelpDialog());


        // Initially load the Date_Picker_Fragment
        loadDatePickerFragment();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.navHome) {
                    Intent homeIntent = new Intent(FragmentHandler.this, MainActivity.class);
                    startActivity(homeIntent);
                } else if (id == R.id.navDatePicker) {
                    drawerLayout.closeDrawer(GravityCompat.END);
//                    Intent datePickerIntent = new Intent(FragmentHandler.this, FragmentHandler.class);
//                    startActivity(datePickerIntent);
                } else if (id == R.id.navList) {
                    Intent savedImagesIntent = new Intent(FragmentHandler.this, SavedImagesList.class);
                    startActivity(savedImagesIntent);
                } else if (id == R.id.navExit) {
                    finishAffinity();
                }
                drawerLayout.closeDrawer(GravityCompat.END);
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        MenuItem datePickerItem = menu.findItem(R.id.datePickMenu);
        MenuItem navBarItem = menu.findItem(R.id.navBarItem);

        datePickerItem.setVisible(true);
        navBarItem.setVisible(true);

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        if (item.getItemId() == R.id.navBarItem) {
            if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
                drawerLayout.closeDrawer(GravityCompat.END);
            } else {
                drawerLayout.openDrawer(GravityCompat.END);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Method to load the Date_Picker_Fragment
    private void loadDatePickerFragment() {
        datePickerFragment = new Date_Picker_Fragment();
        datePickerFragment.setFetchImageClickListener(this);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.datePickerContainer, datePickerFragment)
                .commit();
    }

    // Method to load the ImageInfo Fragment
    private void loadImageInfoFragment(int year, int month, int day) {
        imageInfoFragment = new ImageInfo();
        String selectedDate = formatDate(year, month, day);
        imageInfoFragment.setSelectedDate(selectedDate); // Set the selected date
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.imageInfoContainer, imageInfoFragment)
                .addToBackStack(null)
                .commit();

        showProgressBar();
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                hideProgressBar();
            }
        }, 500);
    }

    // These two are the methods to show or hide the progress bar
    private void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

    // This formats the date in the specific way the API calls for
    private String formatDate(int year, int month, int day){
        return String.format(Locale.CANADA,"%04d-%02d-%02d", year, month + 1, day);
    }


    @Override
    public void fetchImageClicked(int year, int month, int day) {
        datePickerFragment.hideDatePickerFragment();

        showProgressBar();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run(){
                loadImageInfoFragment(year,month,day);
                hideProgressBar();
            }
        },1500);
    }

    @Override
    public void onBackPressed() {
        if (imageInfoFragment != null && imageInfoFragment.isVisible()) {
            // If ImageInfo fragment is visible, remove it from the screen
            getSupportFragmentManager().beginTransaction().remove(imageInfoFragment).commit();
            imageInfoFragment = null;

            loadDatePickerFragment();
        } else {
            super.onBackPressed();
        }
    }

    private void showHelpDialog() {
        Fragment datePickFrag = getSupportFragmentManager().findFragmentById(R.id.datePickerContainer);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (datePickFrag != null && datePickFrag.isVisible()) {
           builder.setMessage(R.string.helpDatePick);
        } else if (imageInfoFragment != null && imageInfoFragment.isVisible()) {
            builder.setMessage(R.string.helpImageInfo);
        }
        builder.setPositiveButton(R.string.helpPositive, null);
        builder.show();
    }
}
