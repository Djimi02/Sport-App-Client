package com.example.sport_app_client;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sport_app_client.adapter.UserGroupsRVAdapter;
import com.example.sport_app_client.groupActivities.FootballGroupActivity;
import com.example.sport_app_client.interfaces.UserGroupClickListener;
import com.example.sport_app_client.model.Sports;
import com.example.sport_app_client.model.User;
import com.example.sport_app_client.model.member.FootballMember;
import com.example.sport_app_client.model.member.Member;
import com.example.sport_app_client.retrofit.MyAuthManager;
import com.example.sport_app_client.retrofit.RetrofitService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Retrofit;

public class HomepageActivity extends AppCompatActivity implements UserGroupClickListener {

    /* Views */
    private TextView totalGamesTV;
    private TextView totalGroupsTV;
    private TextView totalWinsTV;
    private RecyclerView userGroupsRV;
    private Button settingsBTN;

    /* Vars */
    private MyAuthManager authManager;
    private Retrofit retrofit;
    private int totalGames;
    private int totalGroups;
    private int totalWins;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        this.getSupportActionBar().hide();
        Toast.makeText(this, "On create!", Toast.LENGTH_SHORT).show();

        // Empty back button implementation
        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {

            }
        });

        initVars();
        initViews();
        computeGeneralStats();
    }

    private void initVars() {
        this.authManager = MyAuthManager.getInstance();
        this.retrofit = new RetrofitService().getRetrofit();

        this.totalGames = 0;
        this.totalGroups = 0;
        this.totalWins = 0;
    }

    private void initViews() {
        this.totalGroupsTV = findViewById(R.id.homepageTotalGroupsTV);
        this.totalWinsTV = findViewById(R.id.homepageTotalWinsTV);
        this.totalGamesTV = findViewById(R.id.homepageTotalGamesTV);

        this.userGroupsRV = findViewById(R.id.homepageGroupsRV);
        UserGroupsRVAdapter userGroupsRVAdapter = new UserGroupsRVAdapter(authManager.getUser().getMembers(), this);
        userGroupsRV.setAdapter(userGroupsRVAdapter);
        userGroupsRV.setLayoutManager(new LinearLayoutManager(this));

        this.settingsBTN = findViewById(R.id.homepageSettingsBTN);
        settingsBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });
    }

    private void computeGeneralStats() {
        if (authManager.getUser() == null) {
            return;
        }

        // Compute total groups
        this.totalGroups = authManager.getUser().getMembers().size();
        this.totalGroupsTV.setText("Total groups = " + totalGroups);

        // Compute total wins
        int wins = 0;
        int games = 0;
        for (Member member : authManager.getUser().getMembers()) {
            wins += member.getTotalWins();
            games += member.getTotalGames();
        }
        this.totalWins = wins;
        this.totalGames = games;
        this.totalWinsTV.setText("Total wins = " + totalWins);
        this.totalGamesTV.setText("Total games = " + totalGames);
    }

    @Override
    public void goToGroupActivity(Long groupID, Sports sport) {

        switch (sport) {
            case FOOTBALL:
                Intent intent = new Intent(HomepageActivity.this, FootballGroupActivity.class);
                intent.putExtra("group_id", groupID);
                startActivity(intent);
                break;
            default:
                break;
        }
    }
}