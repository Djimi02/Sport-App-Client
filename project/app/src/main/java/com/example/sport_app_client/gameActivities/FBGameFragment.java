package com.example.sport_app_client.gameActivities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.sport_app_client.R;
import com.example.sport_app_client.adapter.football.FBGameStep2RVAdapter;
import com.example.sport_app_client.adapter.football.FBGameStep3RVAdapter;
import com.example.sport_app_client.helpers.GlobalMethods;
import com.example.sport_app_client.helpers.MyGlobals;
import com.example.sport_app_client.model.game.FootballGame;
import com.example.sport_app_client.model.member.FootballMember;
import com.example.sport_app_client.model.stats.FBStats;
import com.example.sport_app_client.model.stats.Stats;
import com.example.sport_app_client.retrofit.RetrofitService;
import com.example.sport_app_client.retrofit.api.FbAPI;
import com.example.sport_app_client.retrofit.request.AddNewFBGameRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FBGameFragment extends GameFragment {

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment FBGroupFragment.
     */
    public static GameFragment newInstance() {
        FBGameFragment fragment = new FBGameFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    /** Vars */
    private List<FootballMember> fbTeam1;
    private List<FootballMember> fbTeam2;
    private List<FBStats> step3Team1Stats;
    private List<FBStats> step3Team2Stats;
    private Map<Long, FBStats> gameStats; // key is member id and value is member stats
    private HashMap<FootballMember, FBStats> currentGameStatsTeam1;
    private HashMap<FootballMember, FBStats> currentGameStatsTeam2;

    /** Retrofit */
    private FbAPI footballGroupAPI;

    /* ==================== START CODE INITIALIZATION ======================================= */

    @Override
    protected void initSportDependentVars() {
        this.fbTeam1 = new ArrayList<>();
        this.fbTeam2 = new ArrayList<>();
        this.step3Team1Stats = new ArrayList<>();
        this.step3Team2Stats = new ArrayList<>();
        this.gameStats = new HashMap<>();

        this.footballGroupAPI = new RetrofitService().getRetrofit().create(FbAPI.class);
    }

    @Override
    protected void initSportDependentRecyclerViews() {
        // Step 2
        this.step2Team1RV = view.findViewById(R.id.gameFragmentStep2Team1RV);
        FBGameStep2RVAdapter step2Team1Adapter = new FBGameStep2RVAdapter(fbTeam1, this);
        step2Team1RV.setAdapter(step2Team1Adapter);
        step2Team1RV.setLayoutManager(new LinearLayoutManager(activity));

        this.step2Team2RV = view.findViewById(R.id.gameFragmentStep2Team2RV);
        FBGameStep2RVAdapter step2Team2Adapter = new FBGameStep2RVAdapter(fbTeam2, this);
        step2Team2RV.setAdapter(step2Team2Adapter);
        step2Team2RV.setLayoutManager(new LinearLayoutManager(activity));

        // Step 3
        this.step3Team1RV = view.findViewById(R.id.gameFragmentStep3Team1RV);
        FBGameStep3RVAdapter step3Team1Adapter = new FBGameStep3RVAdapter(step3Team1Stats);
        step3Team1RV.setAdapter(step3Team1Adapter);
        step3Team1RV.setLayoutManager(new LinearLayoutManager(activity));

        this.step3Team2RV = view.findViewById(R.id.gameFragmentStep3Team2RV);
        FBGameStep3RVAdapter step3Team2Adapter = new FBGameStep3RVAdapter(step3Team2Stats);
        step3Team2RV.setAdapter(step3Team2Adapter);
        step3Team2RV.setLayoutManager(new LinearLayoutManager(activity));
    }

    @Override
    protected View initSportDependentDialog(Stats stats) {
        final View popupView = getLayoutInflater().inflate(R.layout.fb_member_game_stats_dialog, null);

        TextView name = popupView.findViewById(R.id.fbMemberGameStatsNameTV);
        name.setText(stats.getMemberName());

        Button memberGoalsBTN1 = popupView.findViewById(R.id.fbMemberGameStatsGoalsBTN1);
        EditText memberGoalsET = popupView.findViewById(R.id.fbMemberGameStatsGoalsET);
        memberGoalsET.setText(Integer.toString(((FBStats)stats).getGoals()));
        Button memberGoalsBTN2 = popupView.findViewById(R.id.fbMemberGameStatsGoalsBTN2);
        memberGoalsBTN1.setOnClickListener(v -> {
            int value = Integer.parseInt(memberGoalsET.getText().toString());
            if (value > 0) {
                value--;
                memberGoalsET.setText(Integer.toString(value));
            }
        });
        memberGoalsBTN2.setOnClickListener(v -> {
            int value = Integer.parseInt(memberGoalsET.getText().toString());
            if (value < 99) {
                value++;
                memberGoalsET.setText(Integer.toString(value));
            }
        });

        Button memberAssistsBTN1 = popupView.findViewById(R.id.fbMemberGameStatsAssistsBTN1);
        EditText memberAssistsET = popupView.findViewById(R.id.fbMemberGameStatsAssistsET);
        memberAssistsET.setText(Integer.toString(((FBStats)stats).getAssists()));
        Button memberAssistsBTN2 = popupView.findViewById(R.id.fbMemberGameStatsAssistsBTN2);
        memberAssistsBTN1.setOnClickListener(v -> {
            int value = Integer.parseInt(memberAssistsET.getText().toString());
            if (value > 0) {
                value--;
                memberAssistsET.setText(Integer.toString(value));
            }
        });
        memberAssistsBTN2.setOnClickListener(v -> {
            int value = Integer.parseInt(memberAssistsET.getText().toString());
            if (value < 99) {
                value++;
                memberAssistsET.setText(Integer.toString(value));
            }
        });

        Button memberSavesBTN1 = popupView.findViewById(R.id.fbMemberGameStatsSavesBTN1);
        EditText memberSavesET = popupView.findViewById(R.id.fbMemberGameStatsSavesET);
        memberSavesET.setText(Integer.toString(((FBStats)stats).getSaves()));
        Button memberSavesBTN2 = popupView.findViewById(R.id.fbMemberGameStatsSavesBTN2);
        memberSavesBTN1.setOnClickListener(v -> {
            int value = Integer.parseInt(memberSavesET.getText().toString());
            if (value > 0) {
                value--;
                memberSavesET.setText(Integer.toString(value));
            }
        });
        memberSavesBTN2.setOnClickListener(v -> {
            int value = Integer.parseInt(memberSavesET.getText().toString());
            if (value < 99) {
                value++;
                memberSavesET.setText(Integer.toString(value));
            }
        });

        Button memberFoulsBTN1 = popupView.findViewById(R.id.fbMemberGameStatsFoulsBTN1);
        EditText memberFoulsET = popupView.findViewById(R.id.fbMemberGameStatsFoulsET);
        memberFoulsET.setText(Integer.toString(((FBStats)stats).getFouls()));
        Button memberFoulsBTN2 = popupView.findViewById(R.id.fbMemberGameStatsFoulsBTN2);
        memberFoulsBTN1.setOnClickListener(v -> {
            int value = Integer.parseInt(memberFoulsET.getText().toString());
            if (value > 0) {
                value--;
                memberFoulsET.setText(Integer.toString(value));
            }
        });
        memberFoulsBTN2.setOnClickListener(v -> {
            int value = Integer.parseInt(memberFoulsET.getText().toString());
            if (value < 99) {
                value++;
                memberFoulsET.setText(Integer.toString(value));
            }
        });

        Button saveStatsBTN = popupView.findViewById(R.id.fbMemberGameStatsSaveStatsBTN);
        saveStatsBTN.setOnClickListener(v -> {
            ((FBStats)stats).setGoals(Integer.parseInt(memberGoalsET.getText().toString()));
            ((FBStats)stats).setAssists(Integer.parseInt(memberAssistsET.getText().toString()));
            ((FBStats)stats).setSaves(Integer.parseInt(memberSavesET.getText().toString()));
            ((FBStats)stats).setFouls(Integer.parseInt(memberFoulsET.getText().toString()));
            dialog.dismiss();
        });

        return popupView;
    }

    @Override
    protected void setSportSpecificDesign() {
        // TODO: implement
    }

    /* ==================== END CODE INITIALIZATION ========================================= */

    /* ==================== START BTN IMPLEMENTATION ========================================== */

    @Override
    protected boolean goFromStep1ToStep2() {
        if (Math.abs(team1.size() - team2.size()) > 1) {
            Toast.makeText(activity, "Team size difference of more than 1 not allowed!", Toast.LENGTH_LONG).show();
            return false;
        } else if (team1.size() == 0 || team2.size() == 0) {
            Toast.makeText(activity, "Empty teams are not allowed!", Toast.LENGTH_SHORT).show();
            return false;
        } else if (team1.size() > MAX_TEAM_SIZE || team2.size() > MAX_TEAM_SIZE) {
            Toast.makeText(activity, "Teams bigger than " + MAX_TEAM_SIZE + "are not allowed!", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Get and parse members from step1 RVs
        ((FBGameStep2RVAdapter) step2Team1RV.getAdapter())
                .setMembers(team1.stream().map(member -> (FootballMember)member).collect(Collectors.toList()));
        ((FBGameStep2RVAdapter) step2Team2RV.getAdapter())
                .setMembers(team2.stream().map(member -> (FootballMember)member).collect(Collectors.toList()));

        step2Team1RV.getAdapter().notifyDataSetChanged();
        step2Team2RV.getAdapter().notifyDataSetChanged();

        return true;
    }

    @Override
    protected void goFromStep2ToStep3() {
        // update step 3 based on step 2
        step3Team1Stats.clear();
        step3Team1Stats.addAll(((FBGameStep2RVAdapter)step2Team1RV.getAdapter()).getCurrentGameStats().values());
        currentGameStatsTeam1 =
                ((FBGameStep2RVAdapter) step2Team1RV.getAdapter()).getCurrentGameStats();
        step3Team1RV.getAdapter().notifyDataSetChanged();

        step3Team2Stats.clear();
        step3Team2Stats.addAll(((FBGameStep2RVAdapter)step2Team2RV.getAdapter()).getCurrentGameStats().values());
        currentGameStatsTeam2 =
                ((FBGameStep2RVAdapter) step2Team2RV.getAdapter()).getCurrentGameStats();
        step3Team2RV.getAdapter().notifyDataSetChanged();
    }

    @Override
    protected void finalizeStep3() {
        GlobalMethods.showPGAndBlockUI(progressBar, activity);

        collectAndUpdateGameStats(); // Collect game stats

        // Create request
        AddNewFBGameRequest request = new AddNewFBGameRequest();
        request.setGroupID(MyGlobals.footballGroup.getId());
        request.setGameStats(gameStats);

        // Send request
        footballGroupAPI.addNewFootballGame(request).enqueue(new Callback<FootballGame>() {
            @Override
            public void onResponse(Call<FootballGame> call, Response<FootballGame> response) {
                if (response.code() == 200) {
                    Toast.makeText(activity, "Game created successfully!", Toast.LENGTH_SHORT).show();
                    MyGlobals.footballGroup.addGame(response.body());
                    // Update group and homepage
                    MyGlobals.gameCreatedListenerGroup.onGameCreatedGroupIMPL();
                    MyGlobals.gameCreatedListenerHomepage.onGameCreatedOrDeletedHomepageIMPL(MyGlobals.associatedFBMember);

                    activity.finish();
                } else {
                    Toast.makeText(activity, "Game creation failed!", Toast.LENGTH_SHORT).show();
                    Toast.makeText(activity, MyGlobals.ERROR_MESSAGE_2, Toast.LENGTH_SHORT).show();
                    undoCollectGameStats();
                    System.out.println("CODE = " + response.code());
                }
                GlobalMethods.hidePGAndEnableUi(progressBar, activity);
            }

            @Override
            public void onFailure(Call<FootballGame> call, Throwable t) {
                Toast.makeText(activity, "Game creation failed!", Toast.LENGTH_SHORT).show();
                Toast.makeText(activity, MyGlobals.ERROR_MESSAGE_2, Toast.LENGTH_SHORT).show();
                undoCollectGameStats();
                GlobalMethods.hidePGAndEnableUi(progressBar, activity);

                System.out.println(t.toString());
            }
        });
    }

    /* ==================== END BTN IMPLEMENTATION ========================================== */

    /* ===================== START HELPER METHODS ============================================*/


    /**
     * Updates all selected members with their new stat selected in step 2
     * and adds them in this.allMembersWithUpdatedStats and adds all game stats
     * in this.allTemporaryMembers.
     */
    private void collectAndUpdateGameStats() {
        // Collect and update team1 members with stats from the game
        int team1TotalGoals = 0;
        for (FootballMember member : currentGameStatsTeam1.keySet()) {
            FBStats tempStats = currentGameStatsTeam1.get(member);
            tempStats.setIsPartOfTeam1(true);

            member.getStats().setGoals(member.getStats().getGoals() + tempStats.getGoals());
            member.getStats().setAssists(member.getStats().getAssists() + tempStats.getAssists());
            member.getStats().setSaves(member.getStats().getSaves() + tempStats.getSaves());
            member.getStats().setFouls(member.getStats().getFouls() + tempStats.getFouls());

            gameStats.put(member.getId(), tempStats);

            team1TotalGoals += tempStats.getGoals();
        }

        // Collect and update team2 members with stats from the game
        int team2TotalGoals = 0;
        for (FootballMember member : currentGameStatsTeam2.keySet()) {
            FBStats tempStats = currentGameStatsTeam2.get(member);
            tempStats.setIsPartOfTeam1(false);

            member.getStats().setGoals(member.getStats().getGoals() + tempStats.getGoals());
            member.getStats().setAssists(member.getStats().getAssists() + tempStats.getAssists());
            member.getStats().setSaves(member.getStats().getSaves() + tempStats.getSaves());
            member.getStats().setFouls(member.getStats().getFouls() + tempStats.getFouls());

            gameStats.put(member.getId(), tempStats);

            team2TotalGoals += tempStats.getGoals();
        }

        // Increment wins/draws/loses of members and temp stats
        if (team1TotalGoals > team2TotalGoals) {
            new ArrayList<>(currentGameStatsTeam1.keySet()).forEach((member) -> {
                member.getStats().setWins(member.getStats().getWins() + 1);
                currentGameStatsTeam1.get(member).setWins(1);
            });
            new ArrayList<>(currentGameStatsTeam2.keySet()).forEach((member) -> {
                member.getStats().setLoses(member.getStats().getLoses()+1);
                currentGameStatsTeam2.get(member).setLoses(1);
            });
        } else if (team2TotalGoals > team1TotalGoals) {
            new ArrayList<>(currentGameStatsTeam2.keySet()).forEach((member) -> {
                member.getStats().setWins(member.getStats().getWins() + 1);
                currentGameStatsTeam2.get(member).setWins(1);
            });
            new ArrayList<>(currentGameStatsTeam1.keySet()).forEach((member) -> {
                member.getStats().setLoses(member.getStats().getLoses()+1);
                currentGameStatsTeam1.get(member).setLoses(1);
            });
        } else {
            new ArrayList<>(currentGameStatsTeam1.keySet()).forEach((member) -> {
                member.getStats().setDraws(member.getStats().getDraws()+1);
                currentGameStatsTeam1.get(member).setDraws(1);
            });
            new ArrayList<>(currentGameStatsTeam2.keySet()).forEach((member) -> {
                member.getStats().setDraws(member.getStats().getDraws()+1);
                currentGameStatsTeam2.get(member).setDraws(1);
            });
        }

    }

    /**
     * Undoes the updates over the selected members that were done by updateMembersWithNewStats()
     * and clears this.allMembersWithUpdatedStats and this.allTemporaryMembers.
     */
    private void undoCollectGameStats() {
//        currentGameStatsTeam1 =
//                ((FBGameStep2RVAdapter) step2Team1RV.getAdapter()).getCurrentGameStats();
        for (FootballMember member : currentGameStatsTeam1.keySet()) {
            FBStats tempStats = currentGameStatsTeam1.get(member);

            member.getStats().setWins(member.getStats().getWins() - tempStats.getWins());
            member.getStats().setDraws(member.getStats().getDraws() - tempStats.getDraws());
            member.getStats().setLoses(member.getStats().getLoses() - tempStats.getLoses());
            member.getStats().setGoals(member.getStats().getGoals() - tempStats.getGoals());
            member.getStats().setAssists(member.getStats().getAssists() - tempStats.getAssists());
            member.getStats().setSaves(member.getStats().getSaves() - tempStats.getSaves());
            member.getStats().setFouls(member.getStats().getFouls() - tempStats.getFouls());
        }

//        currentGameStatsTeam2 =
//                ((FBGameStep2RVAdapter) step2Team2RV.getAdapter()).getCurrentGameStats();
        for (FootballMember member : currentGameStatsTeam2.keySet()) {
            FBStats tempStats = currentGameStatsTeam2.get(member);

            member.getStats().setWins(member.getStats().getWins() - tempStats.getWins());
            member.getStats().setDraws(member.getStats().getDraws() - tempStats.getDraws());
            member.getStats().setLoses(member.getStats().getLoses() - tempStats.getLoses());
            member.getStats().setGoals(member.getStats().getGoals() - tempStats.getGoals());
            member.getStats().setAssists(member.getStats().getAssists() - tempStats.getAssists());
            member.getStats().setSaves(member.getStats().getSaves() - tempStats.getSaves());
            member.getStats().setFouls(member.getStats().getFouls() - tempStats.getFouls());
        }

        this.gameStats.clear();
    }
}