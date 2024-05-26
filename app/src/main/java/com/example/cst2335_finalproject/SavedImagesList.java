package com.example.cst2335_finalproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;
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

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class SavedImagesList extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ImageButton helpBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_saved_images_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // The below code removes the title from the toolbar
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);


        ListView imageList = findViewById(R.id.imageList);
        List<SavedImageData> imageDataList = new ArrayList<>();
        drawerLayout = findViewById(R.id.drawerLayout);
        NavigationView navigationView = findViewById(R.id.navView);
        TextView activityNameNav = navigationView.getHeaderView(0).findViewById(R.id.navActivityName);
        helpBtn = findViewById(R.id.helpButton);

        activityNameNav.setText(R.string.listActivity);

        SharedPreferences sharedPrefs = getSharedPreferences("ImageData", MODE_PRIVATE);
        String savedData = sharedPrefs.getString("imageList", "");

        if (!TextUtils.isEmpty(savedData)) {
            String[] dataArray = savedData.split(";");
            for (String data : dataArray) {
                String[] parts = data.split(",");
                if (parts.length == 2) {
                    String imagePath = parts[0];
                    String date = parts[1];
                    imageDataList.add(new SavedImageData(imagePath, date));
                }
            }
        }

        helpBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showAlertDialog();
            }
        });

        CustomArrayAdapter adapter = new CustomArrayAdapter(this, R.layout.list_items_layout, imageDataList);
        imageList.setAdapter(adapter);

        // Set long click listener for each item in the list
        imageList.setOnItemLongClickListener((parent, view, position, id) -> {
            SavedImageData imageData = (SavedImageData) parent.getItemAtPosition(position);
            String imagePath = imageData.getImagePath();

            Log.d("longClick", "Item was long clicked at position: " + position);
            // Show a Snackbar for confirmation
            Snackbar snackbar = Snackbar.make(view, "Delete this image?", Snackbar.LENGTH_LONG)
                    .setAction("Delete", v -> {
                        // Delete image from storage
                        deleteImage(imagePath);

                        // Remove item from list
                        adapter.remove(imageData);

                        // Update SharedPreferences
                        updateSharedPreferences(adapter.getData());

                        // Notify adapter of data change
                        adapter.notifyDataSetChanged();
                    });
            // Set custom style for the Snackbar
            View snackbarView = snackbar.getView();
            snackbarView.setBackgroundColor(getResources().getColor(R.color.darkFG));
            TextView textView = snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
            textView.setTextColor(getResources().getColor(R.color.darkWhite));
            snackbar.setActionTextColor(getResources().getColor(R.color.darkTeal));


            // Show the Snackbar with the anchor view
            snackbar.show();

            Snackbar.SnackbarLayout.LayoutParams params = (Snackbar.SnackbarLayout.LayoutParams) snackbarView.getLayoutParams();
            params.bottomMargin = 200; // Adjust margin as needed
            snackbarView.setLayoutParams(params);

            return true;
        });

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.navHome) {
                Intent homeIntent = new Intent(SavedImagesList.this, MainActivity.class);
                startActivity(homeIntent);
            } else if (id == R.id.navDatePicker) {
                Intent datePickerIntent = new Intent(SavedImagesList.this, FragmentHandler.class);
                startActivity(datePickerIntent);
            } else if (id == R.id.navList) {
                drawerLayout.closeDrawer(GravityCompat.END);
//                    Intent savedImagesIntent = new Intent(SavedImagesList.this, SavedImagesList.class);
//                    startActivity(savedImagesIntent);
            } else if (id == R.id.navExit) {
                finishAffinity();
            }
            drawerLayout.closeDrawer(GravityCompat.END);
            return true;
        });
    }
    private void deleteImage(String imagePath) {
        File file = new File(imagePath);
        if (file.exists()) {
            if (file.delete()) {
                Log.d("DeleteImage", "Image deleted successfully");
            } else {
                Log.e("DeleteImage", "Failed to delete image");
            }
        } else {
            Log.e("DeleteImage", "Image file does not exist");
        }
    }
    private void updateSharedPreferences(List<SavedImageData> dataList) {
        StringBuilder newData = new StringBuilder();
        for (SavedImageData data : dataList) {
            newData.append(data.getImagePath()).append(",").append(data.getDate()).append(";");
        }
        // Update SharedPreferences with the new data
        SharedPreferences.Editor editor = getSharedPreferences("ImageData", MODE_PRIVATE).edit();
        editor.putString("imageList", newData.toString());
        editor.apply();
    }

    // Shows an AlertDialog depending on if the user has entered their name or not.
    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.listItemHelp);
        builder.setPositiveButton(R.string.helpPositive, null);
        builder.show();
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
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.datePickMenu) {
            Intent intent = new Intent(this, FragmentHandler.class);
            startActivity(intent);
            return true;
        } else if (item.getItemId() == R.id.navBarItem) {
            if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
                drawerLayout.closeDrawer(GravityCompat.END);
            } else {
                drawerLayout.openDrawer(GravityCompat.END);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}