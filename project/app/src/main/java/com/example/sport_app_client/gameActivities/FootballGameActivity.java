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
import com.example.sport_app_client.adapter.football.FBMembersGameStatsSelectorRVAdapter;
import com.example.sport_app_client.adapter.GameMembersRVAdapter;
import com.example.sport_app_client.adapter.GameTeamsRVAdapter;
import com.example.sport_app_client.adapter.football.FBMemberGameStatsViewRVAdapter;
import com.example.sport_app_client.helpers.MyGlobals;
import com.example.sport_app_client.interfaces.OnGameMemberDragListener;
import com.example.sport_app_client.model.member.FootballMember;
import com.example.sport_app_client.model.member.Member;
import com.example.sport_app_client.retrofit.RetrofitService;
import com.example.sport_app_client.retrofit.api.FBGroupAPI;
import com.example.sport_app_client.retrofit.request.AddNewFBGameRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class FootballGameActivity extends AppCompatActivity implements OnGameMemberDragListener {

    /** Views */
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


    /** Vars */
    private List<FootballMember> members; // links to the real members so modification is propagated
    private List<FootballMember> team1;
    private List<FootballMember> team2;
    private FootballMember draggedMember;
    private List<FootballMember> step3Team1;
    private List<FootballMember> step3Team2;
    private List<FootballMember> allMembersWithUpdatedStats;
    private List<FootballMember> allTemporaryMembers; // represent game stats
    private HashMap<FootballMember, FootballMember> currentGameStatsTeam1;
    private HashMap<FootballMember, FootballMember> currentGameStatsTeam2;
    private Integer victory;

    /** Retrofit */
    private Retrofit retrofit;
    private FBGroupAPI footballGroupAPI;


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
        this.allMembersWithUpdatedStats = new ArrayList<>();
        this.allTemporaryMembers = new ArrayList<>();

        this.retrofit = new RetrofitService().getRetrofit();
        this.footballGroupAPI = retrofit.create(FBGroupAPI.class);
    }

    private void initViews() {
        this.viewFlipper = findViewById(R.id.footballgameactivityVF);

        this.backBTN = findViewById(R.id.footballgameactivityBackBTN);
        backBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backBtnPressed();
            }
        });
        backBTN.setEnabled(false);

        this.nextBTN = findViewById(R.id.footballgameactivityNextBTN);
        this.nextBTN.setText("Confirm Teams");
        nextBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextBtnPressed();
            }
        });

        initRecyclerViews();
    }

    /**
     * This method implements the functionality of "NEXT" button.
     */
    private void nextBtnPressed() {
        if (viewFlipper.getDisplayedChild() == 0) { // Confirm teams pressed
            if (Math.abs(team1.size() - team2.size()) > 1) {
                Toast.makeText(FootballGameActivity.this, "Team size difference of not allowed!", Toast.LENGTH_SHORT).show();
                return;
            } else if (team1.size() == 0 || team2.size() == 0) {
                Toast.makeText(FootballGameActivity.this, "Empty teams are not allowed!", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        else if (viewFlipper.getDisplayedChild() == 2) { // Confirm game pressed
            collectGameStats(); // Collect game stats

            // Create request
            AddNewFBGameRequest request = new AddNewFBGameRequest();
            request.setUpdatedMembers(allMembersWithUpdatedStats);
            request.setGroupID(MyGlobals.footballGroup.getId());
            request.setVictory(victory);
            request.setMembersGameStats(allTemporaryMembers);

            // Send request
            footballGroupAPI.addNewFootballGame(request).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.code() == 200) {
                        Toast.makeText(FootballGameActivity.this, "Game created successfully!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(FootballGameActivity.this, "Game creation failed!", Toast.LENGTH_SHORT).show();
                        Toast.makeText(FootballGameActivity.this, "Try again later!", Toast.LENGTH_SHORT).show();
                        undoCollectGameStats();
                        System.out.println("CODE = " + response.code());
                        finish();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(FootballGameActivity.this, "Game creation failed!", Toast.LENGTH_SHORT).show();
                    Toast.makeText(FootballGameActivity.this, "Try again later!", Toast.LENGTH_SHORT).show();
                    undoCollectGameStats();
                    System.out.println(t.toString());
                    finish();
                }
            });

            return;
        }

        viewFlipper.showNext();
        backBTN.setEnabled(true);

        if (viewFlipper.getDisplayedChild() == 1) { // After confirm teams pressed
            nextBTN.setText("Confirm Stats");

            // Update step 2 based on step 1
            step2Team1RV.getAdapter().notifyDataSetChanged();
            step2Team2RV.getAdapter().notifyDataSetChanged();
        }
        else if (viewFlipper.getDisplayedChild() == 2) { // After confirm stats pressed
            nextBTN.setText("Confirm Game");

            // update step 3 based on step 2
            step3Team1.clear();
            step3Team1.addAll(((FBMembersGameStatsSelectorRVAdapter)step2Team1RV.getAdapter()).getCurrentGameStats().values());
            step3Team1RV.getAdapter().notifyDataSetChanged();
            step3Team2.clear();
            step3Team2.addAll(((FBMembersGameStatsSelectorRVAdapter)step2Team2RV.getAdapter()).getCurrentGameStats().values());
            step3Team2RV.getAdapter().notifyDataSetChanged();
        }
    }

    /**
     * Updates all selected members with their new stat selected in step 2
     * and adds them in this.allMembersWithUpdatedStats and adds all game stats
     * in this.allTemporaryMembers.
     */
    private void collectGameStats() {

        // Collect and update team1 members with stats from the game
        int team1TotalGoals = 0;
        currentGameStatsTeam1 =
                ((FBMembersGameStatsSelectorRVAdapter) step2Team1RV.getAdapter()).getCurrentGameStats();
        for (FootballMember member : currentGameStatsTeam1.keySet()) {
            FootballMember tempMember = currentGameStatsTeam1.get(member);
            tempMember.setPartOfTeam1(true);

            member.setGoals(member.getGoals() + tempMember.getGoals());
            member.setAssists(member.getAssists() + tempMember.getAssists());
            member.setSaves(member.getSaves() + tempMember.getSaves());
            member.setFouls(member.getFouls() + tempMember.getFouls());

            allMembersWithUpdatedStats.add(member);
            allTemporaryMembers.add(tempMember);

            team1TotalGoals =+ tempMember.getGoals();
        }

        // Collect and update team2 members with stats from the game
        int team2TotalGoals = 0;
        currentGameStatsTeam2 =
                ((FBMembersGameStatsSelectorRVAdapter) step2Team2RV.getAdapter()).getCurrentGameStats();
        for (FootballMember member : currentGameStatsTeam2.keySet()) {
            FootballMember tempMember = currentGameStatsTeam2.get(member);
            tempMember.setPartOfTeam1(false);

            member.setGoals(member.getGoals() + tempMember.getGoals());
            member.setAssists(member.getAssists() + tempMember.getAssists());
            member.setSaves(member.getSaves() + tempMember.getSaves());
            member.setFouls(member.getFouls() + tempMember.getFouls());

            allMembersWithUpdatedStats.add(member);
            allTemporaryMembers.add(tempMember);

            team2TotalGoals =+ tempMember.getGoals();
        }

        // Increment wins/draws/loses
        if (team1TotalGoals > team2TotalGoals) {
            this.victory = -1;
            new ArrayList<>(currentGameStatsTeam1.keySet()).forEach((member) -> {member.setWins(member.getWins()+1);});
            new ArrayList<>(currentGameStatsTeam2.keySet()).forEach((member) -> {member.setLoses(member.getLoses()+1);});
        } else if (team2TotalGoals > team1TotalGoals) {
            this.victory = 1;
            new ArrayList<>(currentGameStatsTeam2.keySet()).forEach((member) -> {member.setWins(member.getWins()+1);});
            new ArrayList<>(currentGameStatsTeam1.keySet()).forEach((member) -> {member.setLoses(member.getLoses()+1);});
        } else {
            this.victory = 0;
            new ArrayList<>(currentGameStatsTeam1.keySet()).forEach((member) -> {member.setDraws(member.getDraws()+1);});
            new ArrayList<>(currentGameStatsTeam2.keySet()).forEach((member) -> {member.setDraws(member.getDraws()+1);});
        }

    }

    /**
     * Undoes the updates over the selected members that were done by updateMembersWithNewStats()
     * and clears this.allMembersWithUpdatedStats and this.allTemporaryMembers.
     */
    private void undoCollectGameStats() {
        currentGameStatsTeam1 =
                ((FBMembersGameStatsSelectorRVAdapter) step2Team1RV.getAdapter()).getCurrentGameStats();
        for (FootballMember member : currentGameStatsTeam1.keySet()) {
            FootballMember tempMember = currentGameStatsTeam1.get(member);

            member.setGoals(member.getGoals() - tempMember.getGoals());
            member.setAssists(member.getAssists() - tempMember.getAssists());
            member.setSaves(member.getSaves() - tempMember.getSaves());
            member.setFouls(member.getFouls() - tempMember.getFouls());
        }

        currentGameStatsTeam2 =
                ((FBMembersGameStatsSelectorRVAdapter) step2Team2RV.getAdapter()).getCurrentGameStats();
        for (FootballMember member : currentGameStatsTeam2.keySet()) {
            FootballMember tempMember = currentGameStatsTeam2.get(member);

            member.setGoals(member.getGoals() - tempMember.getGoals());
            member.setAssists(member.getAssists() - tempMember.getAssists());
            member.setSaves(member.getSaves() - tempMember.getSaves());
            member.setFouls(member.getFouls() - tempMember.getFouls());
        }

        this.allMembersWithUpdatedStats.clear();
        this.allTemporaryMembers.clear();
    }

    /**
     * This method implements the functionality of "BACK" button.
     */
    private void backBtnPressed() {
        viewFlipper.showPrevious();
        if (!nextBTN.isEnabled()) {
            nextBTN.setEnabled(true);
        }

        if (viewFlipper.getDisplayedChild() == 0) { // Back to step 1
            backBTN.setEnabled(false);
            nextBTN.setText("Confirm Teams");
        }

        if (viewFlipper.getDisplayedChild() == 1) { // Back to step 2
            nextBTN.setText("Confirm Stats");
        }
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
        FBMembersGameStatsSelectorRVAdapter step2Team1Adapter = new FBMembersGameStatsSelectorRVAdapter(team1);
        step2Team1RV.setAdapter(step2Team1Adapter);
        step2Team1RV.setLayoutManager(new LinearLayoutManager(this));

        this.step2Team2RV = findViewById(R.id.footballgameStep2Team2RV);
        FBMembersGameStatsSelectorRVAdapter step2Team2Adapter = new FBMembersGameStatsSelectorRVAdapter(team2);
        step2Team2RV.setAdapter(step2Team2Adapter);
        step2Team2RV.setLayoutManager(new LinearLayoutManager(this));

        // Step 3
        this.step3Team1RV = findViewById(R.id.footballgameStep3Team1RV);
        FBMemberGameStatsViewRVAdapter step3Team1Adapter = new FBMemberGameStatsViewRVAdapter(step3Team1);
        step3Team1RV.setAdapter(step3Team1Adapter);
        step3Team1RV.setLayoutManager(new LinearLayoutManager(this));

        this.step3Team2RV = findViewById(R.id.footballgameStep3Team2RV);
        FBMemberGameStatsViewRVAdapter step3Team2Adapter = new FBMemberGameStatsViewRVAdapter(step3Team2);
        step3Team2RV.setAdapter(step3Team2Adapter);
        step3Team2RV.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void draggedMember(Member member) {
        this.draggedMember = (FootballMember) member;
    }
}