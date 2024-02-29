package com.example.sport_app_client;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.sport_app_client.retrofit.MyAuthManager;

public class SettingsActivity extends AppCompatActivity {

    /* Views */
    private Button logoutBTN;
    private Button backBTN;

    /* Vars */
    private MyAuthManager authManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        this.getSupportActionBar().hide();
        Toast.makeText(this, "On create!", Toast.LENGTH_SHORT).show();

        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(SettingsActivity.this, HomepageActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });

        initVars();
        initViews();
    }

    private void initVars() {
        this.authManager = MyAuthManager.getInstance();
    }

    private void initViews() {
        this.logoutBTN = findViewById(R.id.settingspageLogoutBTN);
        logoutBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });

        this.backBTN = findViewById(R.id.settingspageBackBTN);
        backBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingsActivity.this, HomepageActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });
    }

    private void logout() {
        MyAuthManager.deleteInstance();
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        // Flags to clear the back stack of activities
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}