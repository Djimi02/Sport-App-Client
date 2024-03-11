package com.example.sport_app_client.gameActivities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.DragEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.example.sport_app_client.R;
import com.example.sport_app_client.adapter.football.FootballGameMembersRVAdapter;
import com.example.sport_app_client.adapter.GameMembersRVAdapter;
import com.example.sport_app_client.adapter.GameTeamsRVAdapter;
import com.example.sport_app_client.adapter.football.FootballMemberStatsRVAdapter;
import com.example.sport_app_client.helpers.MyGlobals;
import com.example.sport_app_client.interfaces.OnGameMemberDragListener;
import com.example.sport_app_client.model.member.FootballMember;
import com.example.sport_app_client.model.member.Member;
import com.example.sport_app_client.retrofit.RetrofitService;
import com.example.sport_app_client.retrofit.api.FootballGroupAPI;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class FootballGameActivity extends AppCompatActivity implements OnGameMemberDragListener {

    /* Views */
    private ViewFlipper viewFlipper;
    private Button backBTN;
    private Button nextBTN;
    private RecyclerView membersRV;
    private RecyclerView step1Team1RV;
    private RecyclerView step1Team2RV;
    private RecyclerView step2Team1RV;
    private RecyclerView step2Team2RV;
    private RecyclerView step3Team1RV;
    private RecyclerView step3Team2RV;


    /* Vars */
    private List<FootballMember> members; // links to the real members so modification is propagated
    private List<FootballMember> team1;
    private List<FootballMember> team2;
    private FootballMember draggedMember;
    private List<FootballMember> step3Team1;
    private List<FootballMember> step3Team2;

    /* Retrofit */
    private Retrofit retrofit;
    private FootballGroupAPI footballGroupAPI;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_football_game);
        this.getSupportActionBar().hide();

        initVars();
        initViews();
    }

    private void initVars() {
        this.members = MyGlobals.footballMembers;
        this.team1 = new ArrayList<>();
        this.team2 = new ArrayList<>();
        this.step3Team1 = new ArrayList<>();
        this.step3Team2 = new ArrayList<>();

        this.retrofit = new RetrofitService().getRetrofit();
        this.footballGroupAPI = retrofit.create(FootballGroupAPI.class);
    }

    private void initViews() {
        this.viewFlipper = findViewById(R.id.footballgameactivityVF);

        this.backBTN = findViewById(R.id.footballgameactivityBackBTN);
        backBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewFlipper.showPrevious();
                nextBTN.setEnabled(true);

                if (viewFlipper.getDisplayedChild() == 0) {
                    backBTN.setEnabled(false);
                    nextBTN.setText("Confirm Teams");
                }

                if (viewFlipper.getDisplayedChild() == 1) {
                    nextBTN.setText("Confirm Stats");
                }
            }
        });
        backBTN.setEnabled(false);

        this.nextBTN = findViewById(R.id.footballgameactivityNextBTN);
        this.nextBTN.setText("Confirm Teams");
        nextBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (viewFlipper.getDisplayedChild() == 2) {
                    Toast.makeText(FootballGameActivity.this, "save game", Toast.LENGTH_SHORT).show();

                    // This will update the member objects in this.team1 and this.team2
                    ((FootballGameMembersRVAdapter)step2Team1RV.getAdapter()).updateMembersWithNewStats();
                    ((FootballGameMembersRVAdapter)step2Team2RV.getAdapter()).updateMembersWithNewStats();

                    // For each member send request to the server to save it
                    for (int i = 0; i < team1.size(); i++) {
                        footballGroupAPI.updateFootballMember(team1.get(i)).enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                if (response.code() == 200) {

                                } else {
                                    // TODO: HANDLE NOT SAVED MEMBER
                                    Toast.makeText(FootballGameActivity.this, "member saving failed", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                Toast.makeText(FootballGameActivity.this, "member saving failed", Toast.LENGTH_SHORT).show();
                            }
                        });
                        System.out.println(team1.get(i).getNickname() + " = " + team1.get(i).getGoals());
                    }
                    for (int i = 0; i < team2.size(); i++) {
                        footballGroupAPI.updateFootballMember(team2.get(i)).enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                if (response.code() == 200) {

                                } else {
                                    // TODO: HANDLE
                                    Toast.makeText(FootballGameActivity.this, "member saving failed", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                Toast.makeText(FootballGameActivity.this, "member saving failed", Toast.LENGTH_SHORT).show();
                            }
                        });
                        System.out.println(team2.get(i).getNickname() + " = " + team2.get(i).getGoals());
                    }

                    // TODO: SAVE GAME

                    finish();
                    return;
                }
                
                viewFlipper.showNext();
                backBTN.setEnabled(true);

                if (viewFlipper.getDisplayedChild() == 1) {
                    nextBTN.setText("Confirm Stats");

                    // Update step 2 based on step 1
                    step2Team1RV.getAdapter().notifyDataSetChanged();
                    step2Team2RV.getAdapter().notifyDataSetChanged();
                }

                if (viewFlipper.getDisplayedChild() == 2) {
                    nextBTN.setText("Confirm Game");

                    // update step 3 based on step 2
                    step3Team1.clear();
                    step3Team1.addAll(((FootballGameMembersRVAdapter)step2Team1RV.getAdapter()).getCurrentGameStats());
                    step3Team1RV.getAdapter().notifyDataSetChanged();
                    step3Team2.clear();
                    step3Team2.addAll(((FootballGameMembersRVAdapter)step2Team2RV.getAdapter()).getCurrentGameStats());
                    step3Team2RV.getAdapter().notifyDataSetChanged();
                }
            }
        });

        initRecyclerViews();
    }

    private void initRecyclerViews() {

        // Step 1
        this.membersRV = findViewById(R.id.footballgameStep1MembersRV);
        GameMembersRVAdapter membersAdapter = new GameMembersRVAdapter(members, this);
        membersRV.setAdapter(membersAdapter);
        membersRV.setLayoutManager(new LinearLayoutManager(this));

        this.step1Team1RV = findViewById(R.id.footballgameStep1Team1RV);
        GameTeamsRVAdapter step1Team1Adapter = new GameTeamsRVAdapter(team1);
        step1Team1RV.setAdapter(step1Team1Adapter);
        step1Team1RV.setLayoutManager(new LinearLayoutManager(this));
        step1Team1RV.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View view, DragEvent dragEvent) {
                switch (dragEvent.getAction()) {
                    case DragEvent.ACTION_DROP:
                        if (!(team2.contains(draggedMember) || team1.contains(draggedMember))) {
                            team1.add(draggedMember);
                        }
                        step1Team1Adapter.notifyDataSetChanged();
                        break;
                }
                return true;
            }
        });

        this.step1Team2RV = findViewById(R.id.footballgameStep1Team2RV);
        GameTeamsRVAdapter step1Team2Adapter = new GameTeamsRVAdapter(team2);
        step1Team2RV.setAdapter(step1Team2Adapter);
        step1Team2RV.setLayoutManager(new LinearLayoutManager(this));
        step1Team2RV.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View view, DragEvent dragEvent) {
                switch (dragEvent.getAction()) {
                    case DragEvent.ACTION_DROP:
                        if (!(team2.contains(draggedMember) || team1.contains(draggedMember))) {
                            team2.add(draggedMember);
                        }
                        step1Team2Adapter.notifyDataSetChanged();
                        break;
                }
                return true;
            }
        });

        // Step 2
        this.step2Team1RV = findViewById(R.id.footballgameStep2Team1RV);
        FootballGameMembersRVAdapter step2Team1Adapter = new FootballGameMembersRVAdapter(team1);
        step2Team1RV.setAdapter(step2Team1Adapter);
        step2Team1RV.setLayoutManager(new LinearLayoutManager(this));

        this.step2Team2RV = findViewById(R.id.footballgameStep2Team2RV);
        FootballGameMembersRVAdapter step2Team2Adapter = new FootballGameMembersRVAdapter(team2);
        step2Team2RV.setAdapter(step2Team2Adapter);
        step2Team2RV.setLayoutManager(new LinearLayoutManager(this));

        // Step 3
        this.step3Team1RV = findViewById(R.id.footballgameStep3Team1RV);
        FootballMemberStatsRVAdapter step3Team1Adapter = new FootballMemberStatsRVAdapter(step3Team1);
        step3Team1RV.setAdapter(step3Team1Adapter);
        step3Team1RV.setLayoutManager(new LinearLayoutManager(this));

        this.step3Team2RV = findViewById(R.id.footballgameStep3Team2RV);
        FootballMemberStatsRVAdapter step3Team2Adapter = new FootballMemberStatsRVAdapter(step3Team2);
        step3Team2RV.setAdapter(step3Team2Adapter);
        step3Team2RV.setLayoutManager(new LinearLayoutManager(this));
    }


    @Override
    public void draggedMember(Member member) {
        this.draggedMember = (FootballMember) member;
    }
}