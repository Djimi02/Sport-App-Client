package com.example.sport_app_client;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.sport_app_client.helpers.LogOutHandler;
import com.example.sport_app_client.retrofit.MyAuthManager;

public class SettingsActivity extends AppCompatActivity {

    /* Views */
    private Button logoutBTN;
    private Button backBTN;

    /* Vars */

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

    }

    private void initViews() {
        this.logoutBTN = findViewById(R.id.settingspageLogoutBTN);
        logoutBTN.setOnClickListener(view -> {
            LogOutHandler.logout(SettingsActivity.this, "Logged out Successfully!");
        });

        this.backBTN = findViewById(R.id.settingspageBackBTN);
        backBTN.setOnClickListener(view -> {
            Intent intent = new Intent(SettingsActivity.this, HomepageActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        });
    }


}