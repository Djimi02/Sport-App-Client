package com.example.sport_app_client.groupActivities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sport_app_client.R;
import com.example.sport_app_client.adapter.GamesRVAdapter;
import com.example.sport_app_client.adapter.GroupSettingsMembersRVAdapter;
import com.example.sport_app_client.adapter.SelectMemberToJoinGroupRVAdapter;
import com.example.sport_app_client.adapter.football.FBMemberAllStatsViewRVAdapter;
import com.example.sport_app_client.adapter.football.FBGameStep3RVAdapter;
import com.example.sport_app_client.gameActivities.GameActivity;
import com.example.sport_app_client.helpers.ConfirmActionDialog;
import com.example.sport_app_client.helpers.GlobalMethods;
import com.example.sport_app_client.helpers.MyGlobals;
import com.example.sport_app_client.interfaces.GameClickListener;
import com.example.sport_app_client.interfaces.GameCreatedListener;
import com.example.sport_app_client.interfaces.GroupMemberDeletedListener;
import com.example.sport_app_client.interfaces.SelectMemberToJoinGroupListener;
import com.example.sport_app_client.model.User;
import com.example.sport_app_client.model.game.FootballGame;
import com.example.sport_app_client.model.game.Game;
import com.example.sport_app_client.model.member.FootballMember;
import com.example.sport_app_client.model.member.Member;
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

public class FBGroupFragment extends Fragment implements GameCreatedListener, GameClickListener, GroupMemberDeletedListener, SelectMemberToJoinGroupListener {
    private Activity activity;
    private View view;

    private boolean isJoining;

    public FBGroupFragment(boolean isJoining) {
        this.isJoining = isJoining;
    }

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fb_group_fragment_layout, container, false);

        this.mainProgressBar = view.findViewById(R.id.fbGroupProgressBar);

        initVars();
        if (isJoining) {
            openJoinGroupDialog();
        } else {
            initViews(); // init will be called again if join group is successful
        }

        return view;
    }

    /* Main Activity Views */
    private TextView groupNameTV;
    private DrawerLayout drawerLayout;
    private Button addMemberBTN;
    private Button addGameBTN;
    private Button settingsBTN;
    private RecyclerView gamesRV;
    private RecyclerView membersRV;
    private ProgressBar mainProgressBar;

    /* Join Group Dialog global views */
    private ProgressBar joinGroupDialogPB;

    /* Settings Views */
    private RecyclerView settingsMembersRV;
    private Button leaveGroupBTN;
    private Button deleteGroupBTN;
    private ProgressBar settingsProgressBar;

    /* Dialog */
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;

    /* Vars */
    private FbAPI groupAPI;
    private List<Member<?>> membersWithoutUsers;

    /** ==================== START CODE INITIALIZATION ======================================= */

    private void initVars() {
        this.groupAPI = new RetrofitService().getRetrofit().create(FbAPI.class);
        if (!isJoining) { // get associated member if not new user
            MyGlobals.associatedFBMember = getAssociatedMember();
            if (MyGlobals.associatedFBMember == null) { // should not happen
                Toast.makeText(activity, MyGlobals.ERROR_MESSAGE_1, Toast.LENGTH_SHORT).show();
                Toast.makeText(activity, MyGlobals.ERROR_MESSAGE_2, Toast.LENGTH_LONG).show();
                activity.finish();
                return;
            }
        }
        MyGlobals.gameCreatedListenerGroup = this;
    }

    private void initViews() {
        this.groupNameTV = view.findViewById(R.id.footballpageGroupNameTV);
        groupNameTV.setText(MyGlobals.footballGroup.getName().toString());

        this.addMemberBTN = view.findViewById(R.id.footballpageAddMemberBTN);
        addMemberBTN.setOnClickListener((view -> {
            openAddMemberDialog();
        }));

        this.addGameBTN = view.findViewById(R.id.footballpageAddGameBTN);
        addGameBTN.setOnClickListener((view) -> {
            onAddGameBTNClick();
        });

        this.drawerLayout = view.findViewById(R.id.fb_drawer_layout);

        this.settingsBTN = view.findViewById(R.id.footballpageSettingsBTN);
        settingsBTN.setOnClickListener(view -> {
            openSettings();
        });

        initRecyclers();

        initSettingsViews();
    }

    private void initRecyclers() {
        this.gamesRV = view.findViewById(R.id.footballpageGamesRV);
        // Sorting the array
        MyGlobals.footballGroup.setGames(
                MyGlobals.footballGroup.getGames().stream()
                        .sorted(Comparator.comparing(FootballGame::getDate).reversed()) // Sort by releaseDate in descending order
                        .collect(Collectors.toList())
        );
        GamesRVAdapter gamesAdapter = new GamesRVAdapter(MyGlobals.footballGroup.getGames(), this);
        gamesRV.setAdapter(gamesAdapter);
        gamesRV.setLayoutManager(new LinearLayoutManager(activity));

        this.membersRV = view.findViewById(R.id.footballpageMembersRV);
        FBMemberAllStatsViewRVAdapter membersAdapter = new FBMemberAllStatsViewRVAdapter(MyGlobals.footballGroup.getMembers());
        membersRV.setAdapter(membersAdapter);
        membersRV.setLayoutManager(new LinearLayoutManager(activity));
    }

    private void initSettingsViews() {
        this.settingsProgressBar = view.findViewById(R.id.fbGroupSettingsProgressBar);

        this.leaveGroupBTN = view.findViewById(R.id.groupSettingsLeaveBTN);
        leaveGroupBTN.setOnClickListener(view -> onLeaveGroupBTNClick());

        this.deleteGroupBTN = view.findViewById(R.id.groupSettingsDeleteBTN);
        deleteGroupBTN.setOnClickListener(view -> onDeleteGroupBTNClick());
    }

    /**
     * This method loads the needed data for the side drawer and opens it.
     */
    private void openSettings() {
        settingsMembersRV = view.findViewById(R.id.groupSettingsMembersRV);
        GroupSettingsMembersRVAdapter settingsMembersAdapter =
                new GroupSettingsMembersRVAdapter(MyGlobals.footballGroup.getMembers(),this, MyGlobals.associatedFBMember);
        settingsMembersRV.setAdapter(settingsMembersAdapter);
        settingsMembersRV.setLayoutManager(new LinearLayoutManager(activity));

        drawerLayout.open();
    }

    private void openAddMemberDialog() {
        // Build dialog
        dialogBuilder = new AlertDialog.Builder(activity);
        final View popupView = getLayoutInflater().inflate(R.layout.add_member_dialog, null);

        // Init views
        final EditText memberNameET = popupView.findViewById(R.id.addFootballMemberDialogET);
        final Button addBTN = popupView.findViewById(R.id.addFootballMemberDialogBTN);
        addBTN.setOnClickListener(view -> onAddMemberBTNClick(popupView, addBTN, memberNameET));

        // Show dialog
        dialogBuilder.setView(popupView);
        dialog = dialogBuilder.create();
        dialog.show();
    }

    private void openJoinGroupDialog() {
        // Build dialog
        dialogBuilder = new AlertDialog.Builder(activity);
        final View popupView = getLayoutInflater().inflate(R.layout.select_member_dialog, null);

        // Init dialog views
        this.joinGroupDialogPB = popupView.findViewById(R.id.selectMemberDialogPG);
        TextView groupName = popupView.findViewById(R.id.selectMemberDialogGroupNameTV);
        groupName.setText(MyGlobals.footballGroup.getName().toString());
        findMembersWithoutUsers();
        RecyclerView rv = popupView.findViewById(R.id.selectMemberDialogRV);
        if (membersWithoutUsers.size() == 0) { // Hide if no members to be displayed
            rv.setVisibility(View.GONE);
            TextView tv = popupView.findViewById(R.id.selectMemberDialogTV1);
            tv.setText("No available members!".toString());
        } else {
            SelectMemberToJoinGroupRVAdapter adapter = new SelectMemberToJoinGroupRVAdapter(membersWithoutUsers, this);
            rv.setAdapter(adapter);
            rv.setLayoutManager(new LinearLayoutManager(activity));
        }
        Button backBTN = popupView.findViewById(R.id.selectMemberDialogBackBTN);
        backBTN.setOnClickListener(v -> activity.finish());
        Button newMemberBTN = popupView.findViewById(R.id.selectMemberNewMemberBTN);
        newMemberBTN.setOnClickListener(v -> onJoinAsNewMemberBTNClicked());

        // Show dialog
        dialogBuilder.setView(popupView);
        dialogBuilder.setCancelable(false);
        dialog = dialogBuilder.create();
        dialog.show();
    }

    /** ==================== END CODE INITIALIZATION ========================================= */

    /** ==================== START BTN IMPLEMENTATION ========================================== */

    private void onAddGameBTNClick() {
        Intent intent = new Intent(activity, GameActivity.class);
        intent.putExtra("fragment", "FOOTBALL");
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }

    private void onLeaveGroupBTNClick() {
        ConfirmActionDialog.showDialog(activity, "Are you sure you want to leave the group?", () -> {
            GlobalMethods.showPGAndBlockUI(settingsProgressBar, activity);

            groupAPI.removeMemberFromGroup(MyGlobals.associatedFBMember.getId()).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.code() == 200) { // OK
                        MyGlobals.createOrJoinOrLeaveGroupListener.onGroupRemoved(MyGlobals.associatedFBMember.getId()); // remove from homepage

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

    private void onDeleteGroupBTNClick() {
        ConfirmActionDialog.showDialog(activity, "Are you sure you want to delete the group?", () -> {
            GlobalMethods.showPGAndBlockUI(settingsProgressBar, activity);

            // Send request
            groupAPI.deleteGroup(MyGlobals.footballGroup.getId()).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.code() == 200) { // OK
                        MyGlobals.createOrJoinOrLeaveGroupListener.onGroupRemoved(MyGlobals.associatedFBMember.getId());
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

    private void onJoinAsNewMemberBTNClicked() {
        GlobalMethods.showPGAndBlockUI(joinGroupDialogPB, activity);

        groupAPI.joinGroupAsNewMember(MyAuthManager.user.getId(), MyGlobals.footballGroup.getId()).enqueue(new Callback<FootballMember>() {
            @Override
            public void onResponse(Call<FootballMember> call, Response<FootballMember> response) {
                if (response.code() == 200) { // OK
                    FootballMember newMember = response.body();
                    MyGlobals.footballGroup.addMember(newMember);
                    MyGlobals.associatedFBMember = (FootballMember) newMember;
                    MyGlobals.createOrJoinOrLeaveGroupListener.onGroupJoined(newMember, MyGlobals.footballGroup);
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

    private void onAddMemberBTNClick(View popupView, Button addBTN, EditText memberNameET) {
        GlobalMethods.hideSoftKeyboard(popupView, activity);
        addBTN.setEnabled(false);

        String memberName = memberNameET.getText().toString().trim();
        if (memberName.isEmpty()) {
            memberNameET.setError("Input member name!");
            return;
        } else if (memberName.length() > 10) {
            memberNameET.setError("Name can be 12 characters max!");
            return;
        }

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

    /** ==================== END BTN IMPLEMENTATION ========================================== */

    /** ================= START LISTENER'S IMPLEMENTATION =================================== */

    @Override
    public void onGameCreatedGroupIMPL() {
        // Update members rv with new stats after game
        this.membersRV.getAdapter().notifyDataSetChanged();

        // Update games rv with new game
        this.gamesRV.getAdapter().notifyItemInserted(0);
    }

    @Override
    public void openGameDialog(Game<?,?> game) {
        // Build dialog
        dialogBuilder = new AlertDialog.Builder(activity);
        final View popupView = getLayoutInflater().inflate(R.layout.fb_game_stats_dialog, null);

        // Init vars
        List<FootballMember> allMembers = new ArrayList<>();
        List<FootballMember> team1 = new ArrayList<>();
        List<FootballMember> team2 = new ArrayList<>();

        // Init views
        TextView date = popupView.findViewById(R.id.fbGameDialogDate);
        date.setText(game.getDate().toString());
        TextView result = popupView.findViewById(R.id.fbGameDialogResult);
        result.setText(game.getResults().toString());
        Button deleteBTN = popupView.findViewById(R.id.fbGameDialogDeleteBTN);
        FootballMember associatedMember = getAssociatedMember();
        if (associatedMember != null) {
            if (associatedMember.getIsAdmin()) {
                deleteBTN.setVisibility(View.VISIBLE);
                deleteBTN.setOnClickListener(view -> {
                    deleteBTN.setEnabled(false);
                    removeGame(game, allMembers);
                });
            }
        }

        // Init recyclers
        RecyclerView team1RV = popupView.findViewById(R.id.fbGameDialogTeam1RV);
        FBGameStep3RVAdapter team1Adapter = new FBGameStep3RVAdapter(team1);
        team1RV.setAdapter(team1Adapter);
        team1RV.setLayoutManager(new LinearLayoutManager(activity));

        RecyclerView team2RV = popupView.findViewById(R.id.fbGameDialogTeam2RV);
        FBGameStep3RVAdapter team2Adapter = new FBGameStep3RVAdapter(team2);
        team2RV.setAdapter(team2Adapter);
        team2RV.setLayoutManager(new LinearLayoutManager(activity));

        GlobalMethods.showPGAndBlockUI(mainProgressBar, activity);

        // Request game stats
        groupAPI.getGameStats(game.getId()).enqueue(new Callback<List<FootballMember>>() {
            @Override
            public void onResponse(Call<List<FootballMember>> call, Response<List<FootballMember>> response) {
                if (response.code() == 200) { // OK
                    allMembers.addAll(response.body());
                    // Split game members into teams
                    for ( FootballMember member : response.body()) {
                        if (member.getIsPartOfTeam1()) {
                            team1.add(member);
                        } else {
                            team2.add(member);
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
            public void onFailure(Call<List<FootballMember>> call, Throwable t) {
                Toast.makeText(activity, MyGlobals.ERROR_MESSAGE_1, Toast.LENGTH_SHORT).show();
                Toast.makeText(activity, MyGlobals.ERROR_MESSAGE_2, Toast.LENGTH_LONG).show();

                dialog.dismiss();
                GlobalMethods.hidePGAndEnableUi(mainProgressBar, activity);
            }
        });

        // Show dialog
        dialogBuilder.setView(popupView);
        dialog = dialogBuilder.create();
    }

    @Override
    public void deleteMember(Member<?> member) {
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

    /**
     * Method is called when joining from rv.
     */
    @Override
    public void onMemberSelected(Member<?> member) {
        GlobalMethods.showPGAndBlockUI(joinGroupDialogPB, activity);

        groupAPI.joinGroupAsExistingMember(MyAuthManager.user.getId(), member.getId()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.code() == 200) { // OK
                    // A copy of the user is made so that there is no cyclic references
                    member.setUser(new User(MyAuthManager.user.getUserName(), MyAuthManager.user.getId()));
                    MyGlobals.createOrJoinOrLeaveGroupListener.onGroupJoined(member, MyGlobals.footballGroup);
                    initViews();
                    MyGlobals.associatedFBMember = (FootballMember) member;
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

    /** ================= END LISTENER'S IMPLEMENTATION =================================== */

    /** ================= START HELPER FUNCTIONS =================================== */

    /**
     * This method removes the game and updates the group members stats accordingly.
     * @param game - game to be removed
     * @param members - game stats
     */
    private void removeGame(Game<?,?> game, List<FootballMember> members) {
        ConfirmActionDialog.showDialog(activity, "Are you sure you want to delete this game!", () -> {
            GlobalMethods.showPGAndBlockUI(mainProgressBar, activity);

            groupAPI.deleteGame(game.getId()).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.code() == 200) { // OK
                        // Update current data
                        decreaseMemberStatsAfterGameDeleted(game, members);
                        MyGlobals.footballGroup.getGames().remove(game);
                        MyGlobals.gameCreatedListenerHomepage.onGameCreatedOrDeletedHomepageIMPL(MyGlobals.associatedFBMember);

                        // Update recyclers
                        membersRV.getAdapter().notifyDataSetChanged();
                        gamesRV.getAdapter().notifyDataSetChanged();
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
     * @param members - the list of game stats
     */
    private void decreaseMemberStatsAfterGameDeleted(Game<?,?> game, List<FootballMember> members) {
        for (int i = 0; i < members.size(); i++) {
            FootballMember associatedGMember = getGroupMemberByNickname(members.get(i).getNickname());
            if (associatedGMember == null) {
                return;
            }
            associatedGMember.setGoals(associatedGMember.getGoals() - members.get(i).getGoals());
            associatedGMember.setAssists(associatedGMember.getAssists() - members.get(i).getAssists());
            associatedGMember.setSaves(associatedGMember.getSaves() - members.get(i).getSaves());
            associatedGMember.setFouls(associatedGMember.getFouls() - members.get(i).getFouls());
            if (game.getVictory() == 0) { // draw
                associatedGMember.setDraws(associatedGMember.getDraws()-1);
            } else if ((game.getVictory() == -1 && members.get(i).getIsPartOfTeam1() ||
                    (game.getVictory() == 1 && !members.get(i).getIsPartOfTeam1()))) { // player had won
                associatedGMember.setWins(associatedGMember.getWins()-1);
            } else if ((game.getVictory() == 1 && members.get(i).getIsPartOfTeam1() ||
                    (game.getVictory() == -1 && !members.get(i).getIsPartOfTeam1()))) { // player had lost
                associatedGMember.setLoses(associatedGMember.getLoses()-1);
            }
        }
    }

    private FootballMember getGroupMemberByNickname(String nickname) {
        for (FootballMember member : MyGlobals.footballGroup.getMembers()) {
            if (member.getNickname().equals(nickname)) {
                return member;
            }
        }
        return null;
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

    /**
     * The method fills in this.membersWithoutUsers with members from
     * MyGlobals.footballGroup who do not have associated user.
     */
    private void findMembersWithoutUsers() {
        if (membersWithoutUsers == null) {
            membersWithoutUsers = new ArrayList<>();
        } else {
            membersWithoutUsers.clear();
        }
        for (Member<?> member : MyGlobals.footballGroup.getMembers()) {
            if (member.getUser() == null) {
                membersWithoutUsers.add(member);
            }
        }
    }

}