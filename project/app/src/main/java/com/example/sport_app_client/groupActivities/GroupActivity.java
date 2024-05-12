package com.example.sport_app_client.groupActivities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.sport_app_client.R;
import com.example.sport_app_client.helpers.GlobalMethods;
import com.example.sport_app_client.helpers.GroupLoadConfig;
import com.example.sport_app_client.helpers.MyGlobals;
import com.example.sport_app_client.model.group.FootballGroup;
import com.example.sport_app_client.retrofit.RetrofitService;
import com.example.sport_app_client.retrofit.api.FbAPI;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class GroupActivity extends AppCompatActivity {

    /* Views */
    private ProgressBar progressBar;

    /* Vars */
    private Retrofit retrofit;
    private FbAPI fbGroupAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blank_layout);
        this.getSupportActionBar().hide();

        progressBar = findViewById(R.id.blankLayoutPG);

        retrofit = new RetrofitService().getRetrofit();

        // Get the data from the intent
        boolean isJoining = getIntent().getBooleanExtra("join", false);
        String sport = getIntent().getStringExtra("sport");
        Long groupID = getIntent().getLongExtra("groupID", -1);
        String uuid = getIntent().getStringExtra("UUID");

        if (sport == null) {
            Toast.makeText(this, "Try again later!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (uuid == null) {
            loadGroupDataByID(sport, groupID, isJoining);
        } else {

        }
    }

    public void loadFragment(String sport, boolean isJoining) {
        Fragment fragment = null;

        // Determine which fragment to load based on the data
        switch (sport) {
            case "FOOTBALL":
                fragment = FBGroupFragment.newInstance(isJoining);
                break;
            case "-":
                break;
        }

        // Replace the container with the new fragment
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, fragment)
                    .commit();
        }
    }

    public void loadGroupDataByID(String sport, Long groupID, boolean isJoining) {
        switch (sport) {
            case "FOOTBALL":
                this.fbGroupAPI = retrofit.create(FbAPI.class);
                loadFBGroupDataByID(sport, groupID, isJoining);
                break;
            case "-":
                break;
        }
    }

    private void loadFBGroupDataByID(String sport, Long groupID, boolean isJoining) {
        GlobalMethods.showPGAndBlockUI(progressBar, this);

        // Request group data
        fbGroupAPI.getFootballGroupByID(groupID).enqueue(new Callback<FootballGroup>() {
            @Override
            public void onResponse(Call<FootballGroup> call, Response<FootballGroup> response) {
                if (response.code() == 200) { // OK
                    GroupLoadConfig.configureGroupData(response.body());

                    loadFragment(sport, isJoining);
                } else {
                    Toast.makeText(GroupActivity.this, MyGlobals.ERROR_MESSAGE_1, Toast.LENGTH_SHORT).show();
                    Toast.makeText(GroupActivity.this, MyGlobals.ERROR_MESSAGE_2, Toast.LENGTH_LONG).show();
                    finish(); // Exit activity on failure
                }
                GlobalMethods.hidePGAndEnableUi(progressBar, GroupActivity.this);
            }

            @Override
            public void onFailure(Call<FootballGroup> call, Throwable t) {
                Toast.makeText(GroupActivity.this, MyGlobals.ERROR_MESSAGE_1, Toast.LENGTH_SHORT).show();
                Toast.makeText(GroupActivity.this, MyGlobals.ERROR_MESSAGE_2, Toast.LENGTH_LONG).show();
                finish(); // Exit activity on failure
                GlobalMethods.hidePGAndEnableUi(progressBar, GroupActivity.this);
                System.out.println(t.toString());
            }
        });
    }

}