package com.example.sport_app_client.groupActivities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.sport_app_client.R;
import com.example.sport_app_client.helpers.LogOutHandler;
import com.example.sport_app_client.model.group.FootballGroup;
import com.example.sport_app_client.retrofit.RetrofitService;
import com.example.sport_app_client.retrofit.api.FootballGroupAPI;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class FootballGroupActivity extends AppCompatActivity {

    /* Views */

    /* Vars */
    private FootballGroup group;
    private Retrofit retrofit;
    private FootballGroupAPI groupAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_football_group);
        this.getSupportActionBar().hide();


        initVars();
        loadData();
        initViews();
    }

    private void loadData() {
        Intent intent = getIntent();
        int result = intent.getIntExtra("new_group",-1);
        if (result == -1) {
            Toast.makeText(this, "Something went wrong with intent data", Toast.LENGTH_SHORT).show(); // delete later
            LogOutHandler.logout(FootballGroupActivity.this, "Try again later!");
            return;
        } else if (result == 0) {
            requestGroupData(intent);
        } else if (result == 1) {
            requestGroupCreation(intent);
        }
    }

    private void requestGroupData(Intent intent) {
        Long groupID = intent.getLongExtra("group_id", -1);
        if (groupID == -1) { // Something went wrong with intent data
            Toast.makeText(this, "Something went wrong with intent data", Toast.LENGTH_SHORT).show(); // delete later
            LogOutHandler.logout(FootballGroupActivity.this, "Try again later!");
            return;
        }

        // Request group data
        groupAPI.getFootballGroup(groupID).enqueue(new Callback<FootballGroup>() {
            @Override
            public void onResponse(Call<FootballGroup> call, Response<FootballGroup> response) {
                if (response.code() == 200) { // OK
                    group = response.body();
                    Toast.makeText(FootballGroupActivity.this, group.getName(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<FootballGroup> call, Throwable t) {
                Toast.makeText(FootballGroupActivity.this, "group request failed", Toast.LENGTH_SHORT).show();
                LogOutHandler.logout(FootballGroupActivity.this, "Try again later!");
            }
        });
    }

    private void requestGroupCreation(Intent intent) {
        String groupName = intent.getStringExtra("group_name");
        Long userID = intent.getLongExtra("user_id",-1);
        if (groupName == null || userID == -1) {
            Toast.makeText(this, "Something went wrong with intent data", Toast.LENGTH_SHORT).show(); // delete later
            LogOutHandler.logout(FootballGroupActivity.this, "Try again later!");
            return;
        }
        groupAPI.createFootballGroup(groupName, userID).enqueue(new Callback<FootballGroup>() {
            @Override
            public void onResponse(Call<FootballGroup> call, Response<FootballGroup> response) {
                if (response.code() == 200) { // ok
                    group = response.body();
                    Toast.makeText(FootballGroupActivity.this, group.getName(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<FootballGroup> call, Throwable t) {
                Toast.makeText(FootballGroupActivity.this, "Something went wrong with intent data", Toast.LENGTH_SHORT).show();
                LogOutHandler.logout(FootballGroupActivity.this, "Try again later!");
            }
        });
    }

    private void initVars() {
        this.retrofit = new RetrofitService().getRetrofit();
        this.groupAPI = retrofit.create(FootballGroupAPI.class);
    }

    private void initViews() {

    }
}