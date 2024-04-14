package com.example.sport_app_client.groupActivities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sport_app_client.R;
import com.example.sport_app_client.adapter.football.FBGameStep3RVAdapter;
import com.example.sport_app_client.adapter.football.FBMemberAllStatsViewRVAdapter;
import com.example.sport_app_client.helpers.ConfirmActionDialog;
import com.example.sport_app_client.helpers.GlobalMethods;
import com.example.sport_app_client.helpers.MyGlobals;
import com.example.sport_app_client.model.User;
import com.example.sport_app_client.model.game.FootballGame;
import com.example.sport_app_client.model.game.Game;
import com.example.sport_app_client.model.member.FootballMember;
import com.example.sport_app_client.model.member.Member;
import com.example.sport_app_client.model.stats.FBStats;
import com.example.sport_app_client.retrofit.MyAuthManager;
import com.example.sport_app_client.retrofit.RetrofitService;
import com.example.sport_app_client.retrofit.api.FbAPI;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FBGroupFragment extends GroupFragment {

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment FBGroupFragment.
     */
    public static FBGroupFragment newInstance(boolean isJoining) {
        FBGroupFragment fragment = new FBGroupFragment(isJoining);
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public FBGroupFragment(boolean isJoining) {
        super(isJoining);
    }

    /* Vars */
    private FbAPI groupAPI;

    // ==================== START CODE INITIALIZATION =======================================

    @Override
    protected void initSportDependentVars() {
        this.groupAPI = new RetrofitService().getRetrofit().create(FbAPI.class);
        if (!isJoining) { // get associated member if not new user
            MyGlobals.associatedFBMember = getAssociatedMember();
            MyGlobals.associatedMember = MyGlobals.associatedFBMember;
            if (MyGlobals.associatedFBMember == null) { // should not happen
                Toast.makeText(activity, MyGlobals.ERROR_MESSAGE_1, Toast.LENGTH_SHORT).show();
                Toast.makeText(activity, MyGlobals.ERROR_MESSAGE_2, Toast.LENGTH_LONG).show();
                activity.finish();
                return;
            }
        }
    }

    @Override
    protected void initSportDependentViews() {
        this.membersRV = view.findViewById(R.id.GroupFragmentMembersRV);
        FBMemberAllStatsViewRVAdapter membersAdapter = new FBMemberAllStatsViewRVAdapter(MyGlobals.footballGroup.getMembers());
        membersRV.setAdapter(membersAdapter);
        membersRV.setLayoutManager(new LinearLayoutManager(activity));
    }

    // ==================== END CODE INITIALIZATION =========================================

    // ==================== START BTN IMPLEMENTATION ==========================================

    @Override
    protected void onLeaveGroupBTNClick() {
        ConfirmActionDialog.showDialog(activity, "Are you sure you want to leave the group?", () -> {
            GlobalMethods.showPGAndBlockUI(settingsProgressBar, activity);

            groupAPI.removeMemberFromGroup(MyGlobals.associatedFBMember.getId()).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.code() == 200) { // OK
                        MyGlobals.createJoinLeaveGroupListenerHomepageActivity.onGroupRemoved(MyGlobals.associatedFBMember.getId()); // remove from homepage

                        activity.finish(); // Exit activity

                        Toast.makeText(activity, "Group left successfully!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(activity, MyGlobals.ERROR_MESSAGE_1, Toast.LENGTH_SHORT).show();
                        Toast.makeText(activity, MyGlobals.ERROR_MESSAGE_2, Toast.LENGTH_LONG).show();
                    }
                    GlobalMethods.hidePGAndEnableUi(settingsProgressBar, activity);
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    GlobalMethods.hidePGAndEnableUi(settingsProgressBar, activity);
                    Toast.makeText(activity, MyGlobals.ERROR_MESSAGE_1, Toast.LENGTH_SHORT).show();
                    Toast.makeText(activity, MyGlobals.ERROR_MESSAGE_2, Toast.LENGTH_LONG).show();
                }
            });
        });
    }

    @Override
    protected void onDeleteGroupBTNClick() {
        ConfirmActionDialog.showDialog(activity, "Are you sure you want to delete the group?", () -> {
            GlobalMethods.showPGAndBlockUI(settingsProgressBar, activity);

            // Send request
            groupAPI.deleteGroup(MyGlobals.footballGroup.getId()).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.code() == 200) { // OK
                        MyGlobals.createJoinLeaveGroupListenerHomepageActivity.onGroupRemoved(MyGlobals.associatedFBMember.getId());
                        Toast.makeText(activity, "Group deleted successfully!", Toast.LENGTH_SHORT).show();
                        activity.finish();
                    } else {
                        Toast.makeText(activity, MyGlobals.ERROR_MESSAGE_1, Toast.LENGTH_SHORT).show();
                        Toast.makeText(activity, MyGlobals.ERROR_MESSAGE_2, Toast.LENGTH_LONG).show();
                    }
                    GlobalMethods.hidePGAndEnableUi(settingsProgressBar, activity);
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    GlobalMethods.hidePGAndEnableUi(settingsProgressBar, activity);
                    Toast.makeText(activity, MyGlobals.ERROR_MESSAGE_1, Toast.LENGTH_SHORT).show();
                    Toast.makeText(activity, MyGlobals.ERROR_MESSAGE_2, Toast.LENGTH_LONG).show();
                }
            });
        });
    }

    @Override
    protected void onJoinAsNewMemberBTNClicked() {
        GlobalMethods.showPGAndBlockUI(joinGroupDialogPB, activity);

        groupAPI.joinGroupAsNewMember(MyAuthManager.user.getId(), MyGlobals.footballGroup.getId()).enqueue(new Callback<FootballMember>() {
            @Override
            public void onResponse(Call<FootballMember> call, Response<FootballMember> response) {
                if (response.code() == 200) { // OK
                    FootballMember newMember = response.body();
                    MyGlobals.footballGroup.addMember(newMember);
                    MyGlobals.associatedFBMember = newMember;
                    MyGlobals.associatedMember = MyGlobals.associatedFBMember;
                    MyGlobals.createJoinLeaveGroupListenerHomepageActivity.onGroupJoined(newMember, MyGlobals.footballGroup);
                    initViews();
                    dialog.dismiss();
                } else {
                    Toast.makeText(activity, MyGlobals.ERROR_MESSAGE_1, Toast.LENGTH_SHORT).show();
                    Toast.makeText(activity, MyGlobals.ERROR_MESSAGE_2, Toast.LENGTH_SHORT).show();
                }

                GlobalMethods.hidePGAndEnableUi(joinGroupDialogPB, activity);
            }

            @Override
            public void onFailure(Call<FootballMember> call, Throwable t) {
                Toast.makeText(activity, MyGlobals.ERROR_MESSAGE_1, Toast.LENGTH_SHORT).show();
                Toast.makeText(activity, MyGlobals.ERROR_MESSAGE_2, Toast.LENGTH_SHORT).show();

                GlobalMethods.hidePGAndEnableUi(joinGroupDialogPB, activity);
            }
        });
    }

    @Override
    protected void onAddMemberBTNClickSportSpecific(String memberName) {
        GlobalMethods.showPGAndBlockUI(mainProgressBar, activity);

        groupAPI.addFootballMember(MyGlobals.footballGroup.getId(), memberName).enqueue(new Callback<FootballMember>() {
            @Override
            public void onResponse(Call<FootballMember> call, Response<FootballMember> response) {
                if (response.code() == 200) { // OK
                    MyGlobals.footballGroup.addMember(response.body());
                    membersRV.getAdapter().notifyItemInserted(MyGlobals.footballGroup.getMembers().size());
                    Toast.makeText(activity, "Member added successfully!", Toast.LENGTH_SHORT).show();
                } else if (response.code() == 400) {
                    try {
                        Toast.makeText(activity, response.errorBody().string(), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(activity, "Something went wrong!", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(activity, MyGlobals.ERROR_MESSAGE_1, Toast.LENGTH_SHORT).show();
                    Toast.makeText(activity, MyGlobals.ERROR_MESSAGE_2, Toast.LENGTH_LONG).show();
                }
                dialog.dismiss();
                GlobalMethods.hidePGAndEnableUi(mainProgressBar, activity);
            }

            @Override
            public void onFailure(Call<FootballMember> call, Throwable t) {
                Toast.makeText(activity, MyGlobals.ERROR_MESSAGE_1, Toast.LENGTH_SHORT).show();
                Toast.makeText(activity, MyGlobals.ERROR_MESSAGE_2, Toast.LENGTH_LONG).show();

                GlobalMethods.hidePGAndEnableUi(mainProgressBar, activity);
                dialog.dismiss();
            }
        });
    }

    // ==================== END BTN IMPLEMENTATION ==========================================

    // ================= START LISTENER'S IMPLEMENTATION ===================================

    @Override
    protected void setUpSportSpecificGameDialog(View popupView, Button deleteBTN, Game<?> game) {
        // Init vars
        List<FBStats> allStats = new ArrayList<>();
        List<FBStats> team1Stats = new ArrayList<>();
        List<FBStats> team2Stats = new ArrayList<>();

        // enable btn if member is admin
        if (MyGlobals.associatedMember.getIsAdmin()) {
            deleteBTN.setVisibility(View.VISIBLE);
            deleteBTN.setOnClickListener(view -> {
                deleteBTN.setEnabled(false);
                removeGame(game, allStats);
            });
        }

        // Init recyclers
        RecyclerView team1RV = popupView.findViewById(R.id.GameDialogTeam1RV);
        FBGameStep3RVAdapter team1Adapter = new FBGameStep3RVAdapter(team1Stats);
        team1RV.setAdapter(team1Adapter);
        team1RV.setLayoutManager(new LinearLayoutManager(activity));

        RecyclerView team2RV = popupView.findViewById(R.id.GameDialogTeam2RV);
        FBGameStep3RVAdapter team2Adapter = new FBGameStep3RVAdapter(team2Stats);
        team2RV.setAdapter(team2Adapter);
        team2RV.setLayoutManager(new LinearLayoutManager(activity));

        GlobalMethods.showPGAndBlockUI(mainProgressBar, activity);

        // Request game stats
        groupAPI.getGameStats(game.getId()).enqueue(new Callback<List<FBStats>>() {
            @Override
            public void onResponse(Call<List<FBStats>> call, Response<List<FBStats>> response) {
                if (response.code() == 200) { // OK
                    allStats.addAll(response.body());
                    // Split game members into teams
                    for (FBStats stats : response.body()) {
                        if (stats.getIsPartOfTeam1()) {
                            team1Stats.add(stats);
                        } else {
                            team2Stats.add(stats);
                        }
                    }

                    // Update recyclers
                    team1Adapter.notifyDataSetChanged();
                    team2Adapter.notifyDataSetChanged();
                    dialog.show();
                } else {
                    Toast.makeText(activity, MyGlobals.ERROR_MESSAGE_1, Toast.LENGTH_SHORT).show();
                    Toast.makeText(activity, MyGlobals.ERROR_MESSAGE_2, Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                }
                GlobalMethods.hidePGAndEnableUi(mainProgressBar, activity);
            }

            @Override
            public void onFailure(Call<List<FBStats>> call, Throwable t) {
                Toast.makeText(activity, MyGlobals.ERROR_MESSAGE_1, Toast.LENGTH_SHORT).show();
                Toast.makeText(activity, MyGlobals.ERROR_MESSAGE_2, Toast.LENGTH_LONG).show();

                dialog.dismiss();
                GlobalMethods.hidePGAndEnableUi(mainProgressBar, activity);
            }
        });
    }

    @Override
    public void deleteMember(Member<?,?> member) {
        ConfirmActionDialog.showDialog(activity, "Are you sure you want to delete the member?", () -> {
            GlobalMethods.showPGAndBlockUI(settingsProgressBar, activity);
            groupAPI.removeMemberFromGroup(member.getId()).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.code() == 200) { // OK
                        MyGlobals.footballGroup.removeMember(member.getId());

                        // Update recyclers
                        membersRV.getAdapter().notifyDataSetChanged();
                        settingsMembersRV.getAdapter().notifyDataSetChanged();

                        Toast.makeText(activity, "Member deleted successfully!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(activity, MyGlobals.ERROR_MESSAGE_1, Toast.LENGTH_SHORT).show();
                        Toast.makeText(activity, MyGlobals.ERROR_MESSAGE_2, Toast.LENGTH_LONG).show();
                    }
                    GlobalMethods.hidePGAndEnableUi(settingsProgressBar, activity);
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    GlobalMethods.hidePGAndEnableUi(settingsProgressBar, activity);
                    Toast.makeText(activity, MyGlobals.ERROR_MESSAGE_1, Toast.LENGTH_SHORT).show();
                    Toast.makeText(activity, MyGlobals.ERROR_MESSAGE_2, Toast.LENGTH_LONG).show();
                }
            });
        });
    }

    @Override
    public void onMemberSelected(Member<?,?> member) {
        GlobalMethods.showPGAndBlockUI(joinGroupDialogPB, activity);

        groupAPI.joinGroupAsExistingMember(MyAuthManager.user.getId(), member.getId()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.code() == 200) { // OK
                    // A copy of the user is made so that there is no cyclic references
                    member.setUser(new User(MyAuthManager.user.getUserName(), MyAuthManager.user.getId()));
                    MyGlobals.createJoinLeaveGroupListenerHomepageActivity.onGroupJoined(member, MyGlobals.footballGroup);
                    initViews();
                    MyGlobals.associatedFBMember = (FootballMember) member;
                    MyGlobals.associatedMember = MyGlobals.associatedFBMember;
                    dialog.dismiss();
                } else {
                    Toast.makeText(activity, MyGlobals.ERROR_MESSAGE_1, Toast.LENGTH_SHORT).show();
                    Toast.makeText(activity, MyGlobals.ERROR_MESSAGE_2, Toast.LENGTH_SHORT).show();
                }

                GlobalMethods.hidePGAndEnableUi(joinGroupDialogPB, activity);
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(activity, MyGlobals.ERROR_MESSAGE_1, Toast.LENGTH_SHORT).show();
                Toast.makeText(activity, MyGlobals.ERROR_MESSAGE_2, Toast.LENGTH_SHORT).show();

                GlobalMethods.hidePGAndEnableUi(joinGroupDialogPB, activity);
            }
        });
    }

    // ================= END LISTENER'S IMPLEMENTATION ===================================

    // ================= START HELPER FUNCTIONS ===================================

    /**
     * This method removes the game and updates the group members stats accordingly.
     * @param game - game to be removed
     * @param gameStats - game stats
     */
    private void removeGame(Game<?> game, List<FBStats> gameStats) {
        ConfirmActionDialog.showDialog(activity, "Are you sure you want to delete this game!", () -> {
            GlobalMethods.showPGAndBlockUI(mainProgressBar, activity);

            groupAPI.deleteGame(game.getId()).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.code() == 200) { // OK
                        // Update current data
                        decreaseMemberStatsAfterGameDeleted(game, gameStats);
                        MyGlobals.footballGroup.getGames().remove(game);
                        MyGlobals.gameCreatedListenerHomepage.onGameCreatedOrDeletedHomepageIMPL(MyGlobals.associatedFBMember);

                        // Update recyclers
                        membersRV.getAdapter().notifyDataSetChanged();
                        gamesRV.getAdapter().notifyDataSetChanged();
                        Toast.makeText(activity, "Game deleted successfully!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(activity, MyGlobals.ERROR_MESSAGE_1, Toast.LENGTH_SHORT).show();
                        Toast.makeText(activity, MyGlobals.ERROR_MESSAGE_2, Toast.LENGTH_LONG).show();
                    }
                    GlobalMethods.hidePGAndEnableUi(mainProgressBar, activity);
                    dialog.dismiss();
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(activity, MyGlobals.ERROR_MESSAGE_1, Toast.LENGTH_SHORT).show();
                    Toast.makeText(activity, MyGlobals.ERROR_MESSAGE_2, Toast.LENGTH_LONG).show();

                    dialog.dismiss();
                    GlobalMethods.hidePGAndEnableUi(mainProgressBar, activity);
                }
            });

        });
    }

    /**
     * This method takes as input the game stats and find each associated member in
     * this.group.getMembers and decreases their stats;
     * @param gameStats - the list of game stats
     */
    private void decreaseMemberStatsAfterGameDeleted(Game<?> game, List<FBStats> gameStats) {
        for (FBStats stats : gameStats) {
            if (stats.getMember() == null) {
                continue;
            }
            FootballMember associatedGMember = getGroupMemberById(stats.getMember().getId());
            if (associatedGMember == null) {
                continue;
            }

            associatedGMember.getStats().setWins(associatedGMember.getStats().getWins() - stats.getWins());
            associatedGMember.getStats().setDraws(associatedGMember.getStats().getDraws() - stats.getDraws());
            associatedGMember.getStats().setLoses(associatedGMember.getStats().getLoses() - stats.getLoses());
            associatedGMember.getStats().setGoals(associatedGMember.getStats().getGoals() - stats.getGoals());
            associatedGMember.getStats().setAssists(associatedGMember.getStats().getAssists() - stats.getAssists());
            associatedGMember.getStats().setSaves(associatedGMember.getStats().getSaves() - stats.getSaves());
            associatedGMember.getStats().setFouls(associatedGMember.getStats().getFouls() - stats.getFouls());
        }
    }
    private FootballMember getGroupMemberById(long memberID) {
        for (FootballMember member : MyGlobals.footballGroup.getMembers()) {
            if (member.getId() == memberID) {
                return member;
            }
        }
        return null;
    }

    @Override
    protected void sortGames() {
        MyGlobals.footballGroup.setGames(
                MyGlobals.footballGroup.getGames().stream()
                        .sorted(Comparator.comparing(FootballGame::getDate).reversed()) // Sort by releaseDate in descending order
                        .collect(Collectors.toList())
        );
    }

    /**
     * This method returns the member that is associated with the currently
     * logged in user.
     */
    private FootballMember getAssociatedMember() {
        FootballMember output = null;
        for (FootballMember fbMember : MyGlobals.footballGroup.getMembers()) {
            if (fbMember.getUser() == null) { } // skip
            else if (fbMember.getUser().getId() == MyAuthManager.user.getId()) {
                output = fbMember;
                break;
            }
        }
        return output;
    }

    @Override
    protected void clearSportSpecificGroupData() {
        MyGlobals.footballGroup = null;
        MyGlobals.associatedFBMember = null;
    }
}