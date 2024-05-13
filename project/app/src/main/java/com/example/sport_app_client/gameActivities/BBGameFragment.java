package com.example.sport_app_client.gameActivities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.sport_app_client.R;
import com.example.sport_app_client.adapter.basketball.BBGameStep2RVAdapter;
import com.example.sport_app_client.adapter.basketball.BBGameStep3RVAdapter;
import com.example.sport_app_client.helpers.GlobalMethods;
import com.example.sport_app_client.helpers.MyGlobals;
import com.example.sport_app_client.model.game.BasketballGame;
import com.example.sport_app_client.model.member.BasketballMember;
import com.example.sport_app_client.model.stats.BBStats;
import com.example.sport_app_client.model.stats.Stats;
import com.example.sport_app_client.retrofit.RetrofitService;
import com.example.sport_app_client.retrofit.api.BbApi;
import com.example.sport_app_client.retrofit.request.AddNewBBGameRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BBGameFragment extends GameFragment {

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment BBGroupFragment.
     */
    public static GameFragment newInstance() {
        BBGameFragment fragment = new BBGameFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    protected final int MAX_TEAM_SIZE = 5;

    /** Vars */
    private List<BasketballMember> bbTeam1;
    private List<BasketballMember> bbTeam2;
    private List<BBStats> step3Team1Stats;
    private List<BBStats> step3Team2Stats;
    private Map<Long, BBStats> gameStats; // key is member id and value is member stats
    private HashMap<BasketballMember, BBStats> currentGameStatsTeam1;
    private HashMap<BasketballMember, BBStats> currentGameStatsTeam2;

    /** Retrofit */
    private BbApi groupAPI;

    /* ==================== START CODE INITIALIZATION ======================================= */

    @Override
    protected void initSportDependentVars() {
        this.bbTeam1 = new ArrayList<>();
        this.bbTeam2 = new ArrayList<>();
        this.step3Team1Stats = new ArrayList<>();
        this.step3Team2Stats = new ArrayList<>();
        this.gameStats = new HashMap<>();

        this.groupAPI = new RetrofitService().getRetrofit().create(BbApi.class);
    }

    @Override
    protected void initSportDependentRecyclerViews() {
        // Step 2
        this.step2Team1RV = view.findViewById(R.id.gameFragmentStep2Team1RV);
        BBGameStep2RVAdapter step2Team1Adapter = new BBGameStep2RVAdapter(bbTeam1, this);
        step2Team1RV.setAdapter(step2Team1Adapter);
        step2Team1RV.setLayoutManager(new LinearLayoutManager(activity));

        this.step2Team2RV = view.findViewById(R.id.gameFragmentStep2Team2RV);
        BBGameStep2RVAdapter step2Team2Adapter = new BBGameStep2RVAdapter(bbTeam2, this);
        step2Team2RV.setAdapter(step2Team2Adapter);
        step2Team2RV.setLayoutManager(new LinearLayoutManager(activity));

        // Step 3
        this.step3Team1RV = view.findViewById(R.id.gameFragmentStep3Team1RV);
        BBGameStep3RVAdapter step3Team1Adapter = new BBGameStep3RVAdapter(step3Team1Stats);
        step3Team1RV.setAdapter(step3Team1Adapter);
        step3Team1RV.setLayoutManager(new LinearLayoutManager(activity));

        this.step3Team2RV = view.findViewById(R.id.gameFragmentStep3Team2RV);
        BBGameStep3RVAdapter step3Team2Adapter = new BBGameStep3RVAdapter(step3Team2Stats);
        step3Team2RV.setAdapter(step3Team2Adapter);
        step3Team2RV.setLayoutManager(new LinearLayoutManager(activity));
    }

    @Override
    protected View initSportDependentDialog(Stats stats) {
        final View popupView = getLayoutInflater().inflate(R.layout.bb_member_game_stats_dialog, null);

        TextView name = popupView.findViewById(R.id.bbMemberGameStatsNameTV);
        name.setText(stats.getMemberName());

        Button memberPointsBTN1 = popupView.findViewById(R.id.bbMemberGameStatsPointsBTN1);
        EditText memberPointsET = popupView.findViewById(R.id.bbMemberGameStatsPointsET);
        memberPointsET.setText(Integer.toString(((BBStats)stats).getPoints()));
        Button memberPointsBTN2 = popupView.findViewById(R.id.bbMemberGameStatsPointsBTN2);
        memberPointsBTN1.setOnClickListener(v -> {
            int value = Integer.parseInt(memberPointsET.getText().toString());
            if (value > 0) {
                value--;
                memberPointsET.setText(Integer.toString(value));
            }
        });
        memberPointsBTN2.setOnClickListener(v -> {
            int value = Integer.parseInt(memberPointsET.getText().toString());
            if (value < 99) {
                value++;
                memberPointsET.setText(Integer.toString(value));
            }
        });

        Button memberNumOfThreePointsBTN1 = popupView.findViewById(R.id.bbMemberGameStatsNumOfThreePointsBTN1);
        EditText memberNumOfThreePointsET = popupView.findViewById(R.id.bbMemberGameStatsNumOfThreePointsET);
        memberNumOfThreePointsET.setText(Integer.toString(((BBStats)stats).getNumberOfThreePoints()));
        Button memberNumOfThreePointsBTN2 = popupView.findViewById(R.id.bbMemberGameStatsNumOfThreePointsBTN2);
        memberNumOfThreePointsBTN1.setOnClickListener(v -> {
            int value = Integer.parseInt(memberNumOfThreePointsET.getText().toString());
            if (value > 0) {
                value--;
                memberNumOfThreePointsET.setText(Integer.toString(value));
            }
        });
        memberNumOfThreePointsBTN2.setOnClickListener(v -> {
            int value = Integer.parseInt(memberNumOfThreePointsET.getText().toString());
            if (value < 99) {
                value++;
                memberNumOfThreePointsET.setText(Integer.toString(value));
            }
        });

        Button memberDunksBTN1 = popupView.findViewById(R.id.bbMemberGameStatsDunksBTN1);
        EditText memberDunksET = popupView.findViewById(R.id.bbMemberGameStatsDunksET);
        memberDunksET.setText(Integer.toString(((BBStats)stats).getNumOfDunks()));
        Button memberDunksBTN2 = popupView.findViewById(R.id.bbMemberGameStatsDunksBTN2);
        memberDunksBTN1.setOnClickListener(v -> {
            int value = Integer.parseInt(memberDunksET.getText().toString());
            if (value > 0) {
                value--;
                memberDunksET.setText(Integer.toString(value));
            }
        });
        memberDunksBTN2.setOnClickListener(v -> {
            int value = Integer.parseInt(memberDunksET.getText().toString());
            if (value < 99) {
                value++;
                memberDunksET.setText(Integer.toString(value));
            }
        });

        Button memberBlocksBTN1 = popupView.findViewById(R.id.bbMemberGameStatsBlocksBTN1);
        EditText memberBlocksET = popupView.findViewById(R.id.bbMemberGameStatsBlocksET);
        memberBlocksET.setText(Integer.toString(((BBStats)stats).getBlocks()));
        Button memberBlocksBTN2 = popupView.findViewById(R.id.bbMemberGameStatsBlocksBTN2);
        memberBlocksBTN1.setOnClickListener(v -> {
            int value = Integer.parseInt(memberBlocksET.getText().toString());
            if (value > 0) {
                value--;
                memberBlocksET.setText(Integer.toString(value));
            }
        });
        memberBlocksBTN2.setOnClickListener(v -> {
            int value = Integer.parseInt(memberBlocksET.getText().toString());
            if (value < 99) {
                value++;
                memberBlocksET.setText(Integer.toString(value));
            }
        });

        Button memberFoulsBTN1 = popupView.findViewById(R.id.bbMemberGameStatsFoulsBTN1);
        EditText memberFoulsET = popupView.findViewById(R.id.bbMemberGameStatsFoulsET);
        memberFoulsET.setText(Integer.toString(((BBStats)stats).getFouls()));
        Button memberFoulsBTN2 = popupView.findViewById(R.id.bbMemberGameStatsFoulsBTN2);
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

        Button saveStatsBTN = popupView.findViewById(R.id.bbMemberGameStatsSaveStatsBTN);
        saveStatsBTN.setOnClickListener(v -> {
            ((BBStats)stats).setPoints(Integer.parseInt(memberPointsET.getText().toString()));
            ((BBStats)stats).setNumberOfThreePoints(Integer.parseInt(memberNumOfThreePointsET.getText().toString()));
            ((BBStats)stats).setNumOfDunks(Integer.parseInt(memberDunksET.getText().toString()));
            ((BBStats)stats).setBlocks(Integer.parseInt(memberBlocksET.getText().toString()));
            ((BBStats)stats).setFouls(Integer.parseInt(memberFoulsET.getText().toString()));
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
        ((BBGameStep2RVAdapter) step2Team1RV.getAdapter())
                .setMembers(team1.stream().map(member -> (BasketballMember)member).collect(Collectors.toList()));
        ((BBGameStep2RVAdapter) step2Team2RV.getAdapter())
                .setMembers(team2.stream().map(member -> (BasketballMember)member).collect(Collectors.toList()));

        step2Team1RV.getAdapter().notifyDataSetChanged();
        step2Team2RV.getAdapter().notifyDataSetChanged();

        return true;
    }

    @Override
    protected void goFromStep2ToStep3() {
        // update step 3 based on step 2
        step3Team1Stats.clear();
        step3Team1Stats.addAll(((BBGameStep2RVAdapter)step2Team1RV.getAdapter()).getCurrentGameStats().values());
        currentGameStatsTeam1 =
                ((BBGameStep2RVAdapter) step2Team1RV.getAdapter()).getCurrentGameStats();
        step3Team1RV.getAdapter().notifyDataSetChanged();

        step3Team2Stats.clear();
        step3Team2Stats.addAll(((BBGameStep2RVAdapter)step2Team2RV.getAdapter()).getCurrentGameStats().values());
        currentGameStatsTeam2 =
                ((BBGameStep2RVAdapter) step2Team2RV.getAdapter()).getCurrentGameStats();
        step3Team2RV.getAdapter().notifyDataSetChanged();
    }

    @Override
    protected void finalizeStep3() {
        GlobalMethods.showPGAndBlockUI(progressBar, activity);

        collectAndUpdateGameStats(); // Collect game stats

        // Create request
        AddNewBBGameRequest request = new AddNewBBGameRequest();
        request.setGroupID(MyGlobals.getBasketballGroup().getId());
        request.setGameStats(gameStats);

        // Send request
        groupAPI.addNewGame(request).enqueue(new Callback<BasketballGame>() {
            @Override
            public void onResponse(Call<BasketballGame> call, Response<BasketballGame> response) {
                if (response.code() == 200) {
                    Toast.makeText(activity, "Game created successfully!", Toast.LENGTH_SHORT).show();
                    MyGlobals.getBasketballGroup().addGame(response.body());
                    // Update group and homepage
                    MyGlobals.gameCreatedListenerGroup.onGameCreatedGroupIMPL();
                    MyGlobals.gameCreatedListenerHomepage.onGameCreatedOrDeletedHomepageIMPL(MyGlobals.getAssociatedBBMember());

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
            public void onFailure(Call<BasketballGame> call, Throwable t) {
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
        int team1TotalPoints = 0;
        for (BasketballMember member : currentGameStatsTeam1.keySet()) {
            BBStats tempStats = currentGameStatsTeam1.get(member);
            tempStats.setIsPartOfTeam1(true);

            member.getStats().setPoints(member.getStats().getPoints() + tempStats.getPoints());
            member.getStats().setNumberOfThreePoints(member.getStats().getNumberOfThreePoints() + tempStats.getNumberOfThreePoints());
            member.getStats().setNumOfDunks(member.getStats().getNumOfDunks() + tempStats.getNumOfDunks());
            member.getStats().setBlocks(member.getStats().getBlocks() + tempStats.getBlocks());
            member.getStats().setFouls(member.getStats().getFouls() + tempStats.getFouls());

            gameStats.put(member.getId(), tempStats);

            team1TotalPoints += tempStats.getPoints();
        }

        // Collect and update team2 members with stats from the game
        int team2TotalPoints = 0;
        for (BasketballMember member : currentGameStatsTeam2.keySet()) {
            BBStats tempStats = currentGameStatsTeam2.get(member);
            tempStats.setIsPartOfTeam1(false);

            member.getStats().setPoints(member.getStats().getPoints() + tempStats.getPoints());
            member.getStats().setNumberOfThreePoints(member.getStats().getNumberOfThreePoints() + tempStats.getNumberOfThreePoints());
            member.getStats().setNumOfDunks(member.getStats().getNumOfDunks() + tempStats.getNumOfDunks());
            member.getStats().setBlocks(member.getStats().getBlocks() + tempStats.getBlocks());
            member.getStats().setFouls(member.getStats().getFouls() + tempStats.getFouls());

            gameStats.put(member.getId(), tempStats);

            team2TotalPoints += tempStats.getPoints();
        }

        // Increment wins/draws/loses of members and temp stats
        if (team1TotalPoints > team2TotalPoints) {
            new ArrayList<>(currentGameStatsTeam1.keySet()).forEach((member) -> {
                member.getStats().setWins(member.getStats().getWins() + 1);
                currentGameStatsTeam1.get(member).setWins(1);
            });
            new ArrayList<>(currentGameStatsTeam2.keySet()).forEach((member) -> {
                member.getStats().setLoses(member.getStats().getLoses()+1);
                currentGameStatsTeam2.get(member).setLoses(1);
            });
        } else if (team2TotalPoints > team1TotalPoints) {
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
        for (BasketballMember member : currentGameStatsTeam1.keySet()) {
            BBStats tempStats = currentGameStatsTeam1.get(member);

            member.getStats().setWins(member.getStats().getWins() - tempStats.getWins());
            member.getStats().setDraws(member.getStats().getDraws() - tempStats.getDraws());
            member.getStats().setLoses(member.getStats().getLoses() - tempStats.getLoses());
            member.getStats().setPoints(member.getStats().getPoints() - tempStats.getPoints());
            member.getStats().setNumberOfThreePoints(member.getStats().getNumberOfThreePoints() - tempStats.getNumberOfThreePoints());
            member.getStats().setNumOfDunks(member.getStats().getNumOfDunks() - tempStats.getNumOfDunks());
            member.getStats().setBlocks(member.getStats().getBlocks() - tempStats.getBlocks());
            member.getStats().setFouls(member.getStats().getFouls() - tempStats.getFouls());
        }

        for (BasketballMember member : currentGameStatsTeam2.keySet()) {
            BBStats tempStats = currentGameStatsTeam2.get(member);

            member.getStats().setWins(member.getStats().getWins() - tempStats.getWins());
            member.getStats().setDraws(member.getStats().getDraws() - tempStats.getDraws());
            member.getStats().setLoses(member.getStats().getLoses() - tempStats.getLoses());
            member.getStats().setPoints(member.getStats().getPoints() - tempStats.getPoints());
            member.getStats().setNumberOfThreePoints(member.getStats().getNumberOfThreePoints() - tempStats.getNumberOfThreePoints());
            member.getStats().setNumOfDunks(member.getStats().getNumOfDunks() - tempStats.getNumOfDunks());
            member.getStats().setBlocks(member.getStats().getBlocks() - tempStats.getBlocks());
            member.getStats().setFouls(member.getStats().getFouls() - tempStats.getFouls());
        }

        this.gameStats.clear();
    }

}