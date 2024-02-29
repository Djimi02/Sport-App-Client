package com.example.sport_app_client.groupActivities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.sport_app_client.R;
import com.example.sport_app_client.model.group.FootballGroup;
import com.example.sport_app_client.retrofit.RetrofitService;
import com.example.sport_app_client.retrofit.api.GroupAPI;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class FootballGroupActivity extends AppCompatActivity {

    /* Views */

    /* Vars */
    private FootballGroup group;
    private Retrofit retrofit;
    private GroupAPI groupAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_football_group);
        this.getSupportActionBar().hide();


        initVars();
        requestGroupData();
        initViews();
    }

    private void requestGroupData() {
        Intent intent = getIntent();
        Long groupID = intent.getLongExtra("group_id", -1);
        if (groupID == -1) { // Something went wrong with intent data
            Toast.makeText(this, "Something went wrong with intent data", Toast.LENGTH_SHORT).show();
            // TODO: maybe return back to homepage
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
            }
        });
    }

    private void initVars() {
        this.retrofit = new RetrofitService().getRetrofit();
        this.groupAPI = retrofit.create(GroupAPI.class);
    }

    private void initViews() {

    }
}