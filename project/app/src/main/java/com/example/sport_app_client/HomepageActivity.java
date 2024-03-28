package com.example.sport_app_client;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sport_app_client.adapter.UserGroupsRVAdapter;
import com.example.sport_app_client.groupActivities.GroupActivity;
import com.example.sport_app_client.helpers.MyGlobals;
import com.example.sport_app_client.interfaces.CreateOrJoinOrLeaveGroupListener;
import com.example.sport_app_client.interfaces.GameCreatedListener;
import com.example.sport_app_client.interfaces.UserGroupClickListener;
import com.example.sport_app_client.model.Sports;
import com.example.sport_app_client.model.member.Member;
import com.example.sport_app_client.retrofit.MyAuthManager;
import com.example.sport_app_client.retrofit.RetrofitService;

import retrofit2.Retrofit;

public class HomepageActivity extends AppCompatActivity implements UserGroupClickListener, CreateOrJoinOrLeaveGroupListener, GameCreatedListener {

    /* Views */
    private TextView totalGroups;
    private TextView totalLosesTV;
    private TextView totalDrawsTV;
    private TextView totalWinsTV;
    private RecyclerView userGroupsRV;
    private Button settingsBTN;
    private Button createGroupBTN;

    /* Dialog */
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;

    /* Vars */
    private Retrofit retrofit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        this.getSupportActionBar().hide();
        MyGlobals.gameCreatedListenerHomepage = this;

        // Empty back button implementation
        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {

            }
        });

        MyGlobals.createOrJoinOrLeaveGroupListener = this;

        initVars();
        initViews();
        computeGeneralStats();
    }

    private void initVars() {
        this.retrofit = new RetrofitService().getRetrofit();
    }

    private void initViews() {
        this.totalGroups = findViewById(R.id.homepageTotalGroupsTV);
        this.totalWinsTV = findViewById(R.id.homepageTotalWinsTV);
        this.totalDrawsTV = findViewById(R.id.homepageTotalDrawsTV);
        this.totalLosesTV = findViewById(R.id.homepageTotalLosesTV);

        this.userGroupsRV = findViewById(R.id.homepageGroupsRV);
        UserGroupsRVAdapter userGroupsRVAdapter = new UserGroupsRVAdapter(MyAuthManager.user.getMembers(), this);
        userGroupsRV.setAdapter(userGroupsRVAdapter);
        userGroupsRV.setLayoutManager(new LinearLayoutManager(this));

        this.settingsBTN = findViewById(R.id.homepageSettingsBTN);
        settingsBTN.setOnClickListener((view -> {
            Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        }));

        this.createGroupBTN = findViewById(R.id.homepageCreateGroupBTN);
        createGroupBTN.setOnClickListener(view -> {
            openCreateGroupDialog();
        });
    }

    private void computeGeneralStats() {
        if (MyAuthManager.user == null) {
            return;
        }

        // Compute total groups
        this.totalGroups.setText("Total groups: " + MyAuthManager.user.getMembers().size());

        // Compute stats
        int wins = 0;
        int draws = 0;
        int loses = 0;
        for (Member<?> member : MyAuthManager.user.getMembers()) {
            wins += member.getWins();
            draws += member.getDraws();
            loses += member.getLoses();
        }
        this.totalWinsTV.setText("Total wins: " + wins);
        this.totalDrawsTV.setText("Total draws: " + draws);
        this.totalLosesTV.setText("Total loses: " + loses);
    }

    private void openCreateGroupDialog() {
        // Build dialog
        dialogBuilder = new AlertDialog.Builder(this);
        final View popupView = getLayoutInflater().inflate(R.layout.create_group_dialog, null);

        final EditText groupNameET = popupView.findViewById(R.id.createGroupDialogET);
        final Spinner spinner = popupView.findViewById(R.id.createGroupDialogSpinner);
        spinner.setAdapter(new ArrayAdapter<Sports>(this, android.R.layout.simple_spinner_item, Sports.values()));
        final Button createBTN = popupView.findViewById(R.id.createGroupDialogBTN);
        createBTN.setOnClickListener(view -> {
            String groupName = groupNameET.getText().toString().trim();
            if (groupName.isEmpty()) {
                groupNameET.setError("Add group name!");
                return;
            } else if (spinner.getSelectedItemPosition() == 0) {
                Toast.makeText(HomepageActivity.this, "Select a sport!", Toast.LENGTH_SHORT).show();
            }

            switch (spinner.getSelectedItem().toString()) {
                case "Football":
                    Intent intent = new Intent(HomepageActivity.this, GroupActivity.class);
                    intent.putExtra("new_group", 1); // 1 means true
                    intent.putExtra("group_name", groupName);
                    intent.putExtra("fragment", "FOOTBALL");
                    startActivity(intent);
                    dialog.dismiss();
                    return;
            }
        });

        // Show dialog
        dialogBuilder.setView(popupView);
        dialog = dialogBuilder.create();
        dialog.show();
    }

    @Override
    public void goToGroupActivity(Long groupID, Sports sport) {

        switch (sport) {
            case FOOTBALL:
                Intent intent = new Intent(HomepageActivity.this, GroupActivity.class);
                intent.putExtra("new_group", 0); // 0 means false
                intent.putExtra("group_id", groupID);
                intent.putExtra("fragment", "FOOTBALL");
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    @Override
    public void onGroupLeft(long memberID) {
        for (int i = 0; i < MyAuthManager.user.getMembers().size(); i++) {
            if (MyAuthManager.user.getMembers().get(i).getId() == memberID) {
                MyAuthManager.user.getMembers().remove(i);
                userGroupsRV.getAdapter().notifyItemRemoved(i); // update the rv
                return;
            }
        }
    }

    @Override
    public void onGroupCreated(Member<?> member) {
        MyAuthManager.user.getMembers().add(member);
        userGroupsRV.getAdapter().notifyItemInserted(MyAuthManager.user.getMembers().size()-1); // update the rv
    }

    @Override
    public void onGroupJoined(Member<?> member) {

    }

    @Override
    public void onGameCreatedOrDeletedHomepageIMPL(Member<?> member) {
        for (int i = 0; i < MyAuthManager.user.getMembers().size(); i++) {
            if (MyAuthManager.user.getMembers().get(i).getId() == member.getId()) {
                MyAuthManager.user.getMembers().get(i).setWins(member.getWins());
                MyAuthManager.user.getMembers().get(i).setDraws(member.getDraws());
                MyAuthManager.user.getMembers().get(i).setLoses(member.getLoses());
                userGroupsRV.getAdapter().notifyItemChanged(i);
            }
        }
    }
}