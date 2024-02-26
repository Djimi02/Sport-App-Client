package com.example.sport_app_client;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.example.sport_app_client.model.User;
import com.example.sport_app_client.model.member.FootballMember;
import com.example.sport_app_client.model.member.Member;
import com.example.sport_app_client.retrofit.MyAuthManager;
import com.example.sport_app_client.retrofit.RetrofitService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Retrofit;

public class HomepageActivity extends AppCompatActivity {

    /* Views */


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

        initVars();
        initViews();
        System.out.println("totalGames= " + totalGames);
        System.out.println("totalGroups= " + totalGroups);
        System.out.println("totalWins= " + totalWins);
    }

    private void initVars() {
        this.authManager = MyAuthManager.getInstance();
        this.retrofit = new RetrofitService().getRetrofit();

        this.totalGames = 0;
        this.totalGroups = 0;
        this.totalWins = 0;
    }

    private void initViews() {


        computeGeneralStats();
    }

    private void computeGeneralStats() {
        if (authManager.getUser() == null) {
            return;
        }

        // Compute total groups
        this.totalGroups = authManager.getUser().getMembers().size();
        // TODO: update the view that holds the stat

        // Compute total wins
        int wins = 0;
        int games = 0;
        for (Member member : authManager.getUser().getMembers()) {
            wins += member.getTotalWins();
            games += member.getTotalGames();
        }
        this.totalWins = wins;
        this.totalGames = games;
        // TODO: update the view that holds the stat
    }

}