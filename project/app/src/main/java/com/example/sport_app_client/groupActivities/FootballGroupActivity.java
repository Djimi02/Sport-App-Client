package com.example.sport_app_client.groupActivities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sport_app_client.R;
import com.example.sport_app_client.adapter.GamesRVAdapter;
import com.example.sport_app_client.adapter.GroupSettingsMembersRVAdapter;
import com.example.sport_app_client.adapter.football.FBMemberAllStatsViewRVAdapter;
import com.example.sport_app_client.adapter.football.FBMemberGameStatsViewRVAdapter;
import com.example.sport_app_client.gameActivities.FootballGameActivity;
import com.example.sport_app_client.helpers.ConfirmActionDialog;
import com.example.sport_app_client.helpers.KeyboardHidder;
import com.example.sport_app_client.helpers.MyGlobals;
import com.example.sport_app_client.interfaces.GameClickListener;
import com.example.sport_app_client.interfaces.GameCreatedListener;
import com.example.sport_app_client.interfaces.GroupMemberDeletedListener;
import com.example.sport_app_client.model.game.Game;
import com.example.sport_app_client.model.group.FootballGroup;
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
import retrofit2.Retrofit;

public class FootballGroupActivity extends AppCompatActivity implements GameCreatedListener, GameClickListener, GroupMemberDeletedListener {


    /* Main Activity Views */
    private TextView groupNameTV;
    private DrawerLayout drawerLayout;
    private Button addMemberBTN;
    private Button addGameBTN;
    private Button settingsBTN;
    private RecyclerView gamesRV;
    private RecyclerView membersRV;
    private ProgressBar mainProgressBar;

    /* Settings Views */
    private RecyclerView settingsMembersRV;
    private Button leaveGroupBTN;
    private Button deleteGroupBTN;
    private ProgressBar settingsProgressBar;

    /* Dialog */
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;

    /* Vars */
    private FootballGroup group;
    private Retrofit retrofit;
    private FbAPI groupAPI;

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
        this.mainProgressBar = findViewById(R.id.fbGroupProgressBar);
        Intent intent = getIntent();
        int result = intent.getIntExtra("new_group",-1);
        if (result == -1) {
            Toast.makeText(this, "Something went wrong with intent data", Toast.LENGTH_SHORT).show(); // delete later
            finish();
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
            finish();
            return;
        }

        // Show progress bar and disable UI interactions
        mainProgressBar.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        // Request group data
        groupAPI.getFootballGroup(groupID).enqueue(new Callback<FootballGroup>() {
            @Override
            public void onResponse(Call<FootballGroup> call, Response<FootballGroup> response) {
                if (response.code() == 200) { // OK
                    group = response.body();
                    initDataDependentViews(); // they depend on group info\
                    // Hide progress bar and allow UI interactions
                    mainProgressBar.setVisibility(View.GONE);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                } else {
                    Toast.makeText(FootballGroupActivity.this, MyGlobals.ERROR_MESSAGE_1, Toast.LENGTH_SHORT).show();
                    Toast.makeText(FootballGroupActivity.this, MyGlobals.ERROR_MESSAGE_2, Toast.LENGTH_LONG).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<FootballGroup> call, Throwable t) {
                Toast.makeText(FootballGroupActivity.this, MyGlobals.ERROR_MESSAGE_1, Toast.LENGTH_SHORT).show();
                Toast.makeText(FootballGroupActivity.this, MyGlobals.ERROR_MESSAGE_2, Toast.LENGTH_LONG).show();
                finish();
                System.out.println(t.toString());
            }
        });
    }

    private void requestGroupCreation(Intent intent) {
        String groupName = intent.getStringExtra("group_name");
        if (groupName == null) {
            Toast.makeText(this, "Something went wrong with intent data", Toast.LENGTH_SHORT).show(); // delete later
            finish();
            return;
        }

        // Show progress bar and disable UI interactions
        mainProgressBar.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        // Send request
        groupAPI.createFootballGroup(groupName, MyAuthManager.user.getId()).enqueue(new Callback<FootballGroup>() {
            @Override
            public void onResponse(Call<FootballGroup> call, Response<FootballGroup> response) {
                if (response.code() == 200) { // ok
                    group = response.body();
                    initDataDependentViews(); // they depend on group info
                    group.getMembers().get(0).setGroup(group);
                    MyGlobals.createOrJoinOrLeaveGroupListener.onGroupCreated(group.getMembers().get(0));
                    Toast.makeText(FootballGroupActivity.this, group.getName(), Toast.LENGTH_SHORT).show();
                    // Hide progress bar and allow UI interactions
                    mainProgressBar.setVisibility(View.GONE);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                } else {
                    Toast.makeText(FootballGroupActivity.this, MyGlobals.ERROR_MESSAGE_1, Toast.LENGTH_SHORT).show();
                    Toast.makeText(FootballGroupActivity.this, MyGlobals.ERROR_MESSAGE_2, Toast.LENGTH_LONG).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<FootballGroup> call, Throwable t) {
                Toast.makeText(FootballGroupActivity.this, MyGlobals.ERROR_MESSAGE_2, Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    private void initVars() {
        this.retrofit = new RetrofitService().getRetrofit();
        this.groupAPI = retrofit.create(FbAPI.class);
    }

    private void initViews() {
        this.addMemberBTN = findViewById(R.id.footballpageAddMemberBTN);
        addMemberBTN.setOnClickListener((view -> {
            openAddMemberDialog();
        }));

        this.addGameBTN = findViewById(R.id.footballpageAddGameBTN);
        addGameBTN.setOnClickListener((view) -> {
            MyGlobals.footballGroup = group;
            MyGlobals.gameCreatedListener = this;
            Intent intent = new Intent(FootballGroupActivity.this, FootballGameActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        });

        this.drawerLayout = findViewById(R.id.fb_drawer_layout);

        this.settingsBTN = findViewById(R.id.footballpageSettingsBTN);
        settingsBTN.setOnClickListener(view -> {
            openSettings();
        });

        initSettingsViews();
    }

    private void initSettingsViews() {
        this.settingsProgressBar = findViewById(R.id.fbGroupSettingsProgressBar);

        this.leaveGroupBTN = findViewById(R.id.groupSettingsLeaveBTN);
        leaveGroupBTN.setOnClickListener(view -> {
            ConfirmActionDialog.showDialog(this, "Are you sure you want to leave the group?", () -> {
                Member associatedMember = getAssociatedMember();
                if (associatedMember == null) {
                    Toast.makeText(FootballGroupActivity.this, MyGlobals.ERROR_MESSAGE_1, Toast.LENGTH_SHORT).show();
                    Toast.makeText(FootballGroupActivity.this, MyGlobals.ERROR_MESSAGE_2, Toast.LENGTH_LONG).show();
                    return;
                }

                // Show progress bar and disable UI interactions
                settingsProgressBar.setVisibility(View.VISIBLE);
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                groupAPI.removeMemberFromGroup(associatedMember.getId()).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.code() == 200) { // OK
                            MyGlobals.createOrJoinOrLeaveGroupListener.onGroupLeft(associatedMember.getId()); // remove from homepage

                            // Exit activity
                            finish();

                            Toast.makeText(FootballGroupActivity.this, "Group left successfully!", Toast.LENGTH_SHORT).show();
                        } else {
                            // Hide progress bar and allow UI interactions
                            settingsProgressBar.setVisibility(View.GONE);
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            Toast.makeText(FootballGroupActivity.this, MyGlobals.ERROR_MESSAGE_1, Toast.LENGTH_SHORT).show();
                            Toast.makeText(FootballGroupActivity.this, MyGlobals.ERROR_MESSAGE_2, Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        // Hide progress bar and allow UI interactions
                        settingsProgressBar.setVisibility(View.GONE);
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        Toast.makeText(FootballGroupActivity.this, MyGlobals.ERROR_MESSAGE_1, Toast.LENGTH_SHORT).show();
                        Toast.makeText(FootballGroupActivity.this, MyGlobals.ERROR_MESSAGE_2, Toast.LENGTH_LONG).show();
                    }
                });
            });
        });
        
        this.deleteGroupBTN = findViewById(R.id.groupSettingsDeleteBTN);
        deleteGroupBTN.setOnClickListener(view -> {
            ConfirmActionDialog.showDialog(this, "Are you sure you want to delete the group?", () -> {
                if (group == null) {
                    Toast.makeText(this, MyGlobals.ERROR_MESSAGE_1, Toast.LENGTH_SHORT).show();
                    Toast.makeText(this, MyGlobals.ERROR_MESSAGE_2, Toast.LENGTH_LONG).show();
                    return;
                }

                // Show progress bar and disable UI interactions
                settingsProgressBar.setVisibility(View.VISIBLE);
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                // Send request
                groupAPI.deleteGroup(group.getId()).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.code() == 200) { // OK
                            FootballMember associatedMember = getAssociatedMember();
                            if (associatedMember != null) {
                                MyGlobals.createOrJoinOrLeaveGroupListener.onGroupLeft(associatedMember.getId());
                            }
                            Toast.makeText(FootballGroupActivity.this, "Group deleted successfully!", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            // Hide progress bar and allow UI interactions
                            settingsProgressBar.setVisibility(View.GONE);
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            Toast.makeText(FootballGroupActivity.this, MyGlobals.ERROR_MESSAGE_1, Toast.LENGTH_SHORT).show();
                            Toast.makeText(FootballGroupActivity.this, MyGlobals.ERROR_MESSAGE_2, Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        // Hide progress bar and allow UI interactions
                        settingsProgressBar.setVisibility(View.GONE);
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        Toast.makeText(FootballGroupActivity.this, MyGlobals.ERROR_MESSAGE_1, Toast.LENGTH_SHORT).show();
                        Toast.makeText(FootballGroupActivity.this, MyGlobals.ERROR_MESSAGE_2, Toast.LENGTH_LONG).show();
                    }
                });
            });
        });
    }

    private void initDataDependentViews() {
        this.groupNameTV = findViewById(R.id.footballpageGroupNameTV);
        groupNameTV.setText(group.getName().toString());

        initRecyclers();
    }

    private void initRecyclers() {
        this.gamesRV = findViewById(R.id.footballpageGamesRV);
        // Sorting the array
        group.setGames(
                group.getGames().stream()
                        .sorted(Comparator.comparing(Game::getDate).reversed()) // Sort by releaseDate in descending order
                        .collect(Collectors.toList())
        );
        GamesRVAdapter gamesAdapter = new GamesRVAdapter(this.group.getGames(), this);
        gamesRV.setAdapter(gamesAdapter);
        gamesRV.setLayoutManager(new LinearLayoutManager(this));

        this.membersRV = findViewById(R.id.footballpageMembersRV);
        FBMemberAllStatsViewRVAdapter membersAdapter = new FBMemberAllStatsViewRVAdapter(this.group.getMembers());
        membersRV.setAdapter(membersAdapter);
        membersRV.setLayoutManager(new LinearLayoutManager(this));
    }

    /**
     * This method loads the needed data for the side drawer and opens it.
     */
    private void openSettings() {
        settingsMembersRV = findViewById(R.id.groupSettingsMembersRV);
        FootballMember associatedMember = getAssociatedMember();
        if (associatedMember == null) {
            Toast.makeText(this, MyGlobals.ERROR_MESSAGE_1, Toast.LENGTH_SHORT).show();
            Toast.makeText(FootballGroupActivity.this, MyGlobals.ERROR_MESSAGE_2, Toast.LENGTH_LONG).show();
            return;
        }
        GroupSettingsMembersRVAdapter settingsMembersAdapter =
                new GroupSettingsMembersRVAdapter(group.getMembers(), associatedMember.getIsAdmin(), this, getAssociatedMember());
        settingsMembersRV.setAdapter(settingsMembersAdapter);
        settingsMembersRV.setLayoutManager(new LinearLayoutManager(this));

        drawerLayout.open();
    }

    private void openAddMemberDialog() {
        // Build dialog
        dialogBuilder = new AlertDialog.Builder(this);
        final View popupView = getLayoutInflater().inflate(R.layout.add_member_dialog, null);

        final EditText memberNameET = popupView.findViewById(R.id.addFootballMemberDialogET);
        final Button addBTN = popupView.findViewById(R.id.addFootballMemberDialogBTN);
        addBTN.setOnClickListener((view -> {
            KeyboardHidder.hideSoftKeyboard(popupView, this);
            addBTN.setEnabled(false);

            String memberName = memberNameET.getText().toString().trim();
            if (memberName.isEmpty()) {
                memberNameET.setError("Input member name!");
                return;
            } else if (memberName.length() > 10) {
                memberNameET.setError("Name can be 12 characters max!");
                return;
            }

            // Show progress bar and disable UI interactions
            mainProgressBar.setVisibility(View.VISIBLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            groupAPI.addFootballMember(group.getId(), memberName).enqueue(new Callback<FootballMember>() {
                @Override
                public void onResponse(Call<FootballMember> call, Response<FootballMember> response) {
                    if (response.code() == 200) { // OK
                        group.addMember(response.body());
                        membersRV.getAdapter().notifyItemInserted(group.getMembers().size());
                        Toast.makeText(FootballGroupActivity.this, "Member added successfully!", Toast.LENGTH_SHORT).show();
                    } else if (response.code() == 400) {
                        try {
                            Toast.makeText(FootballGroupActivity.this, response.errorBody().string(), Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            Toast.makeText(FootballGroupActivity.this, "Something went wrong!", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(FootballGroupActivity.this, MyGlobals.ERROR_MESSAGE_1, Toast.LENGTH_SHORT).show();
                        Toast.makeText(FootballGroupActivity.this, MyGlobals.ERROR_MESSAGE_2, Toast.LENGTH_LONG).show();
                    }
                    dialog.dismiss();
                    // Hide progress bar and allow UI interactions
                    mainProgressBar.setVisibility(View.GONE);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                }

                @Override
                public void onFailure(Call<FootballMember> call, Throwable t) {
                    Toast.makeText(FootballGroupActivity.this, MyGlobals.ERROR_MESSAGE_1, Toast.LENGTH_SHORT).show();
                    Toast.makeText(FootballGroupActivity.this, MyGlobals.ERROR_MESSAGE_2, Toast.LENGTH_LONG).show();
                    // Hide progress bar and allow UI interactions
                    mainProgressBar.setVisibility(View.GONE);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    dialog.dismiss();
                }
            });
        }));

        // Show dialog
        dialogBuilder.setView(popupView);
        dialog = dialogBuilder.create();
        dialog.show();
    }

    /**
     * This method returns the member that is associated with the currently
     * logged in user.
     */
    private FootballMember getAssociatedMember() {
        FootballMember output = null;
        for (FootballMember fbMember : group.getMembers()) {
            if (fbMember.getUser() == null) { } // skip
            else if (fbMember.getUser().getId() == MyAuthManager.user.getId()) {
                output = fbMember;
                break;
            }
        }
        return output;
    }

    @Override
    public void onGameCreated() {
        // Update members rv with new stats after game
        this.membersRV.getAdapter().notifyDataSetChanged();

        // Update games rv with new game
        this.gamesRV.getAdapter().notifyItemInserted(0);
    }

    @Override
    public void openGameDialog(Game game) {
        // Build dialog
        dialogBuilder = new AlertDialog.Builder(this);
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
                deleteBTN.setOnClickListener(view -> removeGame(game, allMembers));
            }
        }

        // Init recyclers
        RecyclerView team1RV = popupView.findViewById(R.id.fbGameDialogTeam1RV);
        FBMemberGameStatsViewRVAdapter team1Adapter = new FBMemberGameStatsViewRVAdapter(team1);
        team1RV.setAdapter(team1Adapter);
        team1RV.setLayoutManager(new LinearLayoutManager(this));

        RecyclerView team2RV = popupView.findViewById(R.id.fbGameDialogTeam2RV);
        FBMemberGameStatsViewRVAdapter team2Adapter = new FBMemberGameStatsViewRVAdapter(team2);
        team2RV.setAdapter(team2Adapter);
        team2RV.setLayoutManager(new LinearLayoutManager(this));

        // Show progress bar and disable UI interactions
        mainProgressBar.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        // Request game stats
        groupAPI.getGameStats(game.getId()).enqueue(new Callback<List<FootballMember>>() {
            @Override
            public void onResponse(Call<List<FootballMember>> call, Response<List<FootballMember>> response) {
                if (response.code() == 200) { // OK
                    allMembers.addAll(response.body());
                    // Split game members into teams
                    for ( FootballMember member : response.body()) {
                        if (member.getPartOfTeam1()) {
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
                    Toast.makeText(FootballGroupActivity.this, MyGlobals.ERROR_MESSAGE_1, Toast.LENGTH_SHORT).show();
                    Toast.makeText(FootballGroupActivity.this, MyGlobals.ERROR_MESSAGE_2, Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                }
                // Hide progress bar and allow UI interactions
                mainProgressBar.setVisibility(View.GONE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }

            @Override
            public void onFailure(Call<List<FootballMember>> call, Throwable t) {
                Toast.makeText(FootballGroupActivity.this, MyGlobals.ERROR_MESSAGE_1, Toast.LENGTH_SHORT).show();
                Toast.makeText(FootballGroupActivity.this, MyGlobals.ERROR_MESSAGE_2, Toast.LENGTH_LONG).show();
                dialog.dismiss();
                // Hide progress bar and allow UI interactions
                mainProgressBar.setVisibility(View.GONE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        });

        // Show dialog
        dialogBuilder.setView(popupView);
        dialog = dialogBuilder.create();
    }

    /**
     * This method removes the game and updates the group members stats accordingly.
     * @param game - game to be removed
     * @param members - game stats
     */
    private void removeGame(Game game, List<FootballMember> members) {
        // Show progress bar and disable UI interactions
        mainProgressBar.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        groupAPI.deleteGame(game.getId()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.code() == 200) { // OK
                    // Update current data
                    decreaseMemberStatsAfterGameDeleted(game, members);
                    group.getGames().remove(game);

                    // Update recyclers
                    membersRV.getAdapter().notifyDataSetChanged();
                    gamesRV.getAdapter().notifyDataSetChanged();
                } else {
                    Toast.makeText(FootballGroupActivity.this, MyGlobals.ERROR_MESSAGE_1, Toast.LENGTH_SHORT).show();
                    Toast.makeText(FootballGroupActivity.this, MyGlobals.ERROR_MESSAGE_2, Toast.LENGTH_LONG).show();
                }
                // Hide progress bar and allow UI interactions
                mainProgressBar.setVisibility(View.GONE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                dialog.dismiss();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(FootballGroupActivity.this, MyGlobals.ERROR_MESSAGE_1, Toast.LENGTH_SHORT).show();
                Toast.makeText(FootballGroupActivity.this, MyGlobals.ERROR_MESSAGE_2, Toast.LENGTH_LONG).show();
                dialog.dismiss();
                // Hide progress bar and allow UI interactions
                mainProgressBar.setVisibility(View.GONE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        });
    }

    /**
     * This method takes as input the game stats and find each associated member in
     * this.group.getMembers and decreases their stats;
     * @param members - the list of game stats
     */
    private void decreaseMemberStatsAfterGameDeleted(Game game, List<FootballMember> members) {
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
            } else if ((game.getVictory() == -1 && members.get(i).getPartOfTeam1() ||
                    (game.getVictory() == 1 && !members.get(i).getPartOfTeam1()))) { // player had won
                associatedGMember.setWins(associatedGMember.getWins()-1);
            } else if ((game.getVictory() == 1 && members.get(i).getPartOfTeam1() ||
                    (game.getVictory() == -1 && !members.get(i).getPartOfTeam1()))) { // player had lost
                associatedGMember.setLoses(associatedGMember.getLoses()-1);
            }
        }
    }

    private FootballMember getGroupMemberByNickname(String nickname) {
        for (FootballMember member : group.getMembers()) {
            if (member.getNickname().equals(nickname)) {
                return member;
            }
        }
        return null;
    }

    @Override
    public void deleteMember(Member member) {
        ConfirmActionDialog.showDialog(this, "Are you sure you want to delete the member?", () -> {
            // Show progress bar and disable UI interactions
            settingsProgressBar.setVisibility(View.VISIBLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            groupAPI.removeMemberFromGroup(member.getId()).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.code() == 200) { // OK
                        group.removeMember(member.getId());

                        // Update recyclers
                        membersRV.getAdapter().notifyDataSetChanged();
                        settingsMembersRV.getAdapter().notifyDataSetChanged();

                        Toast.makeText(FootballGroupActivity.this, "Member deleted successfully!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(FootballGroupActivity.this, MyGlobals.ERROR_MESSAGE_1, Toast.LENGTH_SHORT).show();
                        Toast.makeText(FootballGroupActivity.this, MyGlobals.ERROR_MESSAGE_2, Toast.LENGTH_LONG).show();
                    }
                    // Hide progress bar and allow UI interactions
                    settingsProgressBar.setVisibility(View.GONE);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    // Hide progress bar and allow UI interactions
                    settingsProgressBar.setVisibility(View.GONE);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    Toast.makeText(FootballGroupActivity.this, MyGlobals.ERROR_MESSAGE_1, Toast.LENGTH_SHORT).show();
                    Toast.makeText(FootballGroupActivity.this, MyGlobals.ERROR_MESSAGE_2, Toast.LENGTH_LONG).show();
                }
            });
        });
    }

}