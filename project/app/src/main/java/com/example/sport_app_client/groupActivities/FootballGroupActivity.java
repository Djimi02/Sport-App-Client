package com.example.sport_app_client.groupActivities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sport_app_client.R;
import com.example.sport_app_client.adapter.GamesRVAdapter;
import com.example.sport_app_client.adapter.GroupSettingsMembersRVAdapter;
import com.example.sport_app_client.adapter.football.FBMemberAllStatsViewRVAdapter;
import com.example.sport_app_client.adapter.football.FBMemberGameStatsViewRVAdapter;
import com.example.sport_app_client.gameActivities.FootballGameActivity;
import com.example.sport_app_client.helpers.ConfirmActionDialog;
import com.example.sport_app_client.helpers.LogOutHandler;
import com.example.sport_app_client.helpers.MyGlobals;
import com.example.sport_app_client.interfaces.ActionDoer;
import com.example.sport_app_client.interfaces.GameClickListener;
import com.example.sport_app_client.interfaces.GameCreatedListener;
import com.example.sport_app_client.interfaces.GroupMemberDeletedListener;
import com.example.sport_app_client.model.game.Game;
import com.example.sport_app_client.model.group.FootballGroup;
import com.example.sport_app_client.model.member.FootballMember;
import com.example.sport_app_client.model.member.Member;
import com.example.sport_app_client.retrofit.RetrofitService;
import com.example.sport_app_client.retrofit.api.FBGroupAPI;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class FootballGroupActivity extends AppCompatActivity implements GameCreatedListener, GameClickListener, GroupMemberDeletedListener {

    /* Views */
    private DrawerLayout drawerLayout;
    private Button addMemberBTN;
    private Button addGameBTN;
    private Button settingsBTN;
    private RecyclerView gamesRV;
    private RecyclerView membersRV;
    private RecyclerView settingsMembersRV;

    /* Dialog */
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;

    /* Vars */
    private FootballGroup group;
    private Retrofit retrofit;
    private FBGroupAPI groupAPI;

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
        Intent intent = getIntent();
        int result = intent.getIntExtra("new_group",-1);
        if (result == -1) {
            Toast.makeText(this, "Something went wrong with intent data", Toast.LENGTH_SHORT).show(); // delete later
            LogOutHandler.logout(FootballGroupActivity.this, "Try again later!");
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
            LogOutHandler.logout(FootballGroupActivity.this, "Try again later!");
            return;
        }

        // Request group data
        groupAPI.getFootballGroup(groupID).enqueue(new Callback<FootballGroup>() {
            @Override
            public void onResponse(Call<FootballGroup> call, Response<FootballGroup> response) {
                if (response.code() == 200) { // OK
                    group = response.body();
                    initRecyclers(); // they depend on group info
                    Toast.makeText(FootballGroupActivity.this, group.getName(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(FootballGroupActivity.this, "This action cannot be done now!", Toast.LENGTH_SHORT).show();
                    Toast.makeText(FootballGroupActivity.this, "Try again later!", Toast.LENGTH_SHORT).show();
                    LogOutHandler.logout(FootballGroupActivity.this);
                }
            }

            @Override
            public void onFailure(Call<FootballGroup> call, Throwable t) {
                Toast.makeText(FootballGroupActivity.this, "group request failed", Toast.LENGTH_SHORT).show();
                LogOutHandler.logout(FootballGroupActivity.this, "Try again later!");
                System.out.println(t.toString());
                LogOutHandler.logout(FootballGroupActivity.this);
            }
        });
    }

    private void requestGroupCreation(Intent intent) {
        String groupName = intent.getStringExtra("group_name");
        Long userID = intent.getLongExtra("user_id",-1);
        if (groupName == null || userID == -1) {
            Toast.makeText(this, "Something went wrong with intent data", Toast.LENGTH_SHORT).show(); // delete later
            LogOutHandler.logout(FootballGroupActivity.this, "Try again later!");
            return;
        }
        groupAPI.createFootballGroup(groupName, userID).enqueue(new Callback<FootballGroup>() {
            @Override
            public void onResponse(Call<FootballGroup> call, Response<FootballGroup> response) {
                if (response.code() == 200) { // ok
                    group = response.body();
                    initRecyclers(); // they depend on group info
                    Toast.makeText(FootballGroupActivity.this, group.getName(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(FootballGroupActivity.this, "This action cannot be done now!", Toast.LENGTH_SHORT).show();
                    Toast.makeText(FootballGroupActivity.this, "Try again later!", Toast.LENGTH_SHORT).show();
                    LogOutHandler.logout(FootballGroupActivity.this);
                }
            }

            @Override
            public void onFailure(Call<FootballGroup> call, Throwable t) {
                Toast.makeText(FootballGroupActivity.this, "Something went wrong with intent data", Toast.LENGTH_SHORT).show();
                LogOutHandler.logout(FootballGroupActivity.this, "Try again later!");
            }
        });
    }

    private void initVars() {
        this.retrofit = new RetrofitService().getRetrofit();
        this.groupAPI = retrofit.create(FBGroupAPI.class);
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
        GroupSettingsMembersRVAdapter settingsMembersAdapter =
                new GroupSettingsMembersRVAdapter(group.getMembers(), getAssociatedMember().getIsAdmin(), this, getAssociatedMember());
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
            String memberName = memberNameET.getText().toString().trim();
            if (memberName.isEmpty()) {
                memberNameET.setError("Input member name!");
                return;
            } else if (memberName.length() > 10) {
                memberNameET.setError("Name can be 12 characters max!");
                return;
            }
            groupAPI.addFootballMember(group.getId(), memberName).enqueue(new Callback<FootballMember>() {
                @Override
                public void onResponse(Call<FootballMember> call, Response<FootballMember> response) {
                    if (response.code() == 200) { // OK
                        group.addMember(response.body());
                        Toast.makeText(FootballGroupActivity.this, "Member added successfully!", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    } else if (response.code() == 400) {
                        try {
                            Toast.makeText(FootballGroupActivity.this, response.errorBody().string(), Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            Toast.makeText(FootballGroupActivity.this, "Something went wrong!", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(FootballGroupActivity.this, "This action cannot be done now!", Toast.LENGTH_SHORT).show();
                        Toast.makeText(FootballGroupActivity.this, "Try again later!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<FootballMember> call, Throwable t) {
                    Toast.makeText(FootballGroupActivity.this, "group request failed", Toast.LENGTH_SHORT).show();
                    LogOutHandler.logout(FootballGroupActivity.this, "Try again later!");
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
            if (fbMember.getUser().getId() == MyGlobals.currentUser.getId()) {
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
        this.gamesRV.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void openGameDialog(Game game) {
        // Build dialog
        dialogBuilder = new AlertDialog.Builder(this);
        final View popupView = getLayoutInflater().inflate(R.layout.fb_game_stats_dialog, null);

        // Init vars
        List<FootballMember> team1 = new ArrayList<>();
        List<FootballMember> team2 = new ArrayList<>();

        // Init views
        TextView date = popupView.findViewById(R.id.fbGameDialogDate);
        date.setText(game.getDate().toString());
        TextView result = popupView.findViewById(R.id.fbGameDialogResult);
        result.setText(game.getResults().toString());

        // Init recyclers
        RecyclerView team1RV = popupView.findViewById(R.id.fbGameDialogTeam1RV);
        FBMemberGameStatsViewRVAdapter team1Adapter = new FBMemberGameStatsViewRVAdapter(team1);
        team1RV.setAdapter(team1Adapter);
        team1RV.setLayoutManager(new LinearLayoutManager(this));

        RecyclerView team2RV = popupView.findViewById(R.id.fbGameDialogTeam2RV);
        FBMemberGameStatsViewRVAdapter team2Adapter = new FBMemberGameStatsViewRVAdapter(team2);
        team2RV.setAdapter(team2Adapter);
        team2RV.setLayoutManager(new LinearLayoutManager(this));

        // Request game stats
        groupAPI.getGameStats(game.getId()).enqueue(new Callback<List<FootballMember>>() {
            @Override
            public void onResponse(Call<List<FootballMember>> call, Response<List<FootballMember>> response) {
                if (response.code() == 200) { // OK

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
                } else {
                    Toast.makeText(FootballGroupActivity.this, "Data fetching failed!", Toast.LENGTH_SHORT).show();
                    Toast.makeText(FootballGroupActivity.this, "Try again later!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<List<FootballMember>> call, Throwable t) {
                Toast.makeText(FootballGroupActivity.this, "Data fetching failed!", Toast.LENGTH_SHORT).show();
                Toast.makeText(FootballGroupActivity.this, "Try again later!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        // Show dialog
        dialogBuilder.setView(popupView);
        dialog = dialogBuilder.create();
        dialog.show();
    }

    @Override
    public void deleteMember(Member member) {
        ConfirmActionDialog.showDialog(this, "Are you sure you want to delete the member?", () -> {
            groupAPI.removeMemberFromGroup(group.getId(), member.getId()).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.code() == 200) { // OK
                        group.removeMember(member.getId());

                        // Update recyclers
                        membersRV.getAdapter().notifyDataSetChanged();
                        settingsMembersRV.getAdapter().notifyDataSetChanged();

                        Toast.makeText(FootballGroupActivity.this, "Member deleted successfully!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(FootballGroupActivity.this, "Unable execute this action now!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(FootballGroupActivity.this, "Unable execute this action now!", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}