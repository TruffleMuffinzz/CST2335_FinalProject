package com.example.cst2335_finalproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Objects;


/* Required elements in this activity:
*       EditText
*       SharedPreferences
*       Button
*       Toast
*       AlertDialog
*/


public class MainActivity extends AppCompatActivity {
    private EditText editTextName;
    private Button submitBtn;
    private TextView welcomeText;
    private SharedPreferences sharedPrefs;
    private ImageButton helpBtn;
    private String savedName;
    private MenuItem dateItem;
    private MenuItem navItem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Toolbar tbar = findViewById(R.id.toolbar);
        setSupportActionBar(tbar);
        // The below code removes the title from the toolbar
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        editTextName = findViewById(R.id.nameEditText);
        submitBtn = findViewById(R.id.submitButton);
        welcomeText = findViewById(R.id.welcomeMessage);
        helpBtn = findViewById(R.id.helpButton);

        sharedPrefs = getSharedPreferences("namePrefs", MODE_PRIVATE);
        savedName = sharedPrefs.getString("name", "");

        // Check if the sharedPrefs name is empty, if it isn't set the visibilty of the editText
        // and button to be gone, and show welcome message.
        if(!TextUtils.isEmpty(savedName)){
            editTextName.setVisibility(View.GONE);
            submitBtn.setVisibility(View.GONE);
            welcomeText.setText(getString(R.string.welcomeMessage, savedName));
        }


        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editTextName.getText().toString().trim();
                if (TextUtils.isEmpty(name)){
                    Toast.makeText(MainActivity.this, "Please enter your name", Toast.LENGTH_SHORT).show();
                } else {
                    saveName(name);
                    editTextName.setVisibility(View.GONE);
                    submitBtn.setVisibility(View.GONE);
                    welcomeText.setText(getString(R.string.welcomeMessage, name));

                    updateMenuItemsVisibility(true);

                    // The below code makes sure that the keyboard is hidden once a name is successfully submitted
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }

                }
            }
        });

        helpBtn.setOnClickListener(new View.OnClickListener() {
           public void onClick(View v) {
               showAlertDialog();
           }
        });



    }
    private void saveName(String name) {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString("name", name);
        editor.apply();
    }
// Shows an AlertDialog depending on if the user has entered their name or not.
    private void showAlertDialog() {
        String savedName = sharedPrefs.getString("name", "");
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        if (TextUtils.isEmpty(savedName)) {
            builder.setMessage(R.string.helpNoName);
        } else {
            builder.setMessage(R.string.helpName);
        }
        builder.setPositiveButton(R.string.helpPositive, null);
        builder.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        dateItem = menu.findItem(R.id.datePickMenu);
        navItem = menu.findItem(R.id.navBarItem);

        updateMenuItemsVisibility(!TextUtils.isEmpty(savedName));

        return true;
    }

    private void updateMenuItemsVisibility(boolean isVisible){
        if (dateItem != null && navItem != null) {
            dateItem.setVisible(isVisible);
            navItem.setVisible(isVisible);
        }
    }

    protected void onResume(){
        super.onResume();
        savedName = sharedPrefs.getString("name","");
        invalidateOptionsMenu();
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
}