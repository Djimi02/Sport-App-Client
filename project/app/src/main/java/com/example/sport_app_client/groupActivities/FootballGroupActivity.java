package com.example.sport_app_client.groupActivities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sport_app_client.R;
import com.example.sport_app_client.adapter.football.FBMemberAllStatsViewRVAdapter;
import com.example.sport_app_client.gameActivities.FootballGameActivity;
import com.example.sport_app_client.helpers.LogOutHandler;
import com.example.sport_app_client.helpers.MyGlobals;
import com.example.sport_app_client.model.group.FootballGroup;
import com.example.sport_app_client.model.member.FootballMember;
import com.example.sport_app_client.retrofit.RetrofitService;
import com.example.sport_app_client.retrofit.api.FBGroupAPI;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class FootballGroupActivity extends AppCompatActivity {

    /* Views */
    private Button addMemberBTN;
    private Button addGameBTN;
    private RecyclerView gamesRV;
    private RecyclerView membersRV;

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
                }
            }

            @Override
            public void onFailure(Call<FootballGroup> call, Throwable t) {
                Toast.makeText(FootballGroupActivity.this, "group request failed", Toast.LENGTH_SHORT).show();
                LogOutHandler.logout(FootballGroupActivity.this, "Try again later!");
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
        addMemberBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAddMemberDialog();
            }
        });

        this.addGameBTN = findViewById(R.id.footballpageAddGameBTN);
        addGameBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyGlobals.footballMembers = group.getMembers();
                MyGlobals.footballGroup = group;
                Intent intent = new Intent(FootballGroupActivity.this, FootballGameActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });
    }

    private void initRecyclers() {
        this.gamesRV = findViewById(R.id.footballpageGamesRV);


        this.membersRV = findViewById(R.id.footballpageMembersRV);
        FBMemberAllStatsViewRVAdapter membersAdapter = new FBMemberAllStatsViewRVAdapter(this.group.getMembers());
        membersRV.setAdapter(membersAdapter);
        membersRV.setLayoutManager(new LinearLayoutManager(this));
    }

    private void openAddMemberDialog() {
        // Build dialog
        dialogBuilder = new AlertDialog.Builder(this);
        final View popupView = getLayoutInflater().inflate(R.layout.add_member_dialog, null);

        final EditText memberNameET = popupView.findViewById(R.id.addFootballMemberDialogET);
        final Button addBTN = popupView.findViewById(R.id.addFootballMemberDialogBTN);
        addBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String memberName = memberNameET.getText().toString().trim();
                if (memberName.isEmpty()) {
                    memberNameET.setError("Input member name!");
                    return;
                } else if (memberName.length() > 12) {
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
            }
        });

        // Show dialog
        dialogBuilder.setView(popupView);
        dialog = dialogBuilder.create();
        dialog.show();
    }
}