package com.example.sport_app_client.gameActivities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.sport_app_client.R;

public class FootballGameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_football_game);
        this.getSupportActionBar().hide();
    }
}