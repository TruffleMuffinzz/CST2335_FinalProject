package com.example.cst2335_finalproject;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


/* Required elements in this activity:
*       EditText
*       SharedPreferences
*       Button
*       Toast
*/


public class MainActivity extends AppCompatActivity {
    private EditText editTextName;
    private Button submitBtn;
    private TextView welcomeText;
    private SharedPreferences sharedPrefs;


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

        editTextName = findViewById(R.id.nameEditText);
        submitBtn = findViewById(R.id.submitButton);
        welcomeText = findViewById(R.id.welcomeMessage);

        sharedPrefs = getSharedPreferences("namePrefs", MODE_PRIVATE);
        String savedName = sharedPrefs.getString("name", "");

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

                }
            }
        });



    }
    private void saveName(String name) {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString("name", name);
        editor.apply();
    }
}