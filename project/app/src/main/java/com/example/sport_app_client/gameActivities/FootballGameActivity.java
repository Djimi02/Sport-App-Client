package com.example.sport_app_client.gameActivities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;

import com.example.sport_app_client.R;

public class FootballGameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blank_layout);
        this.getSupportActionBar().hide();

        // Get the data from the intent
        String fragmentToLoad = getIntent().getStringExtra("fragment");
        loadFragment(fragmentToLoad);
    }

    public void loadFragment(String fragmentToLoad) {
        Fragment fragment = null;

        // Determine which fragment to load based on the data
        switch (fragmentToLoad) {
            case "FOOTBALL":
                fragment = FBGameFragment.newInstance();
                break;
            case "-":
                break;
        }

        // Replace the container with the new fragment
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.groupFragmentContainer, fragment)
                    .commit();
        }
    }

}