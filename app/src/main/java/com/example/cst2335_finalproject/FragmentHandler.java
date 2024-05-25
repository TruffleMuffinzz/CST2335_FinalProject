package com.example.cst2335_finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import java.util.Locale;
import java.util.Objects;

public class FragmentHandler extends AppCompatActivity implements Date_Picker_Fragment.FetchImageClickListener {

    private Date_Picker_Fragment datePickerFragment;
    private ImageInfo imageInfoFragment;

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

        // Initially load the Date_Picker_Fragment
        loadDatePickerFragment();
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
        if (item.getItemId() == R.id.datePickMenu) {
            Intent intent = new Intent(this, FragmentHandler.class);
            startActivity(intent);
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
    }

    private String formatDate(int year, int month, int day){
        return String.format(Locale.CANADA,"%04d-%02d-%02d", year, month + 1, day);
    }

    @Override
    public void fetchImageClicked(int year, int month, int day) {
        // Handle fetch image button click and load ImageInfo fragment
        loadImageInfoFragment(year, month, day);

        getSupportFragmentManager().beginTransaction()
                .remove(datePickerFragment)
                .commit();
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
}
