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

import com.example.sport_app_client.R;
import com.example.sport_app_client.adapter.GamesRVAdapter;
import com.example.sport_app_client.adapter.GroupSettingsMembersRVAdapter;
import com.example.sport_app_client.adapter.SelectMemberToJoinGroupRVAdapter;
import com.example.sport_app_client.gameActivities.GameActivity;
import com.example.sport_app_client.helpers.MyGlobals;
import com.example.sport_app_client.interfaces.GameClickListener;
import com.example.sport_app_client.interfaces.GameCreatedListener;
import com.example.sport_app_client.interfaces.GroupMemberDeletedListener;
import com.example.sport_app_client.interfaces.SelectMemberToJoinGroupListener;
import com.example.sport_app_client.model.game.Game;
import com.example.sport_app_client.model.member.Member;

import java.util.ArrayList;
import java.util.List;

public abstract class GroupFragment extends Fragment implements GameCreatedListener, GameClickListener, GroupMemberDeletedListener, SelectMemberToJoinGroupListener {
    protected Activity activity;
    protected View view;

    protected boolean isJoining;

    public GroupFragment(boolean isJoining) {
        System.out.println("log from constructor");
        this.isJoining = isJoining;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        System.out.println("log from onCreate");

        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        System.out.println("log from onCreateView");
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
    protected TextView groupNameTV;
    protected DrawerLayout drawerLayout;
    protected Button addMemberBTN;
    protected Button addGameBTN;
    protected Button settingsBTN;
    protected RecyclerView gamesRV;
    protected RecyclerView membersRV;
    protected ProgressBar mainProgressBar;

    /* Join Group Dialog global views */
    protected ProgressBar joinGroupDialogPB;

    /* Settings Views */
    protected RecyclerView settingsMembersRV;
    protected Button leaveGroupBTN;
    protected Button deleteGroupBTN;
    protected ProgressBar settingsProgressBar;

    /* Dialog */
    protected AlertDialog.Builder dialogBuilder;
    protected AlertDialog dialog;

    /* Vars */
    protected List<Member<?>> membersWithoutUsers;

    /** ==================== START CODE INITIALIZATION ======================================= */

    private void initVars() {
        MyGlobals.gameCreatedListenerGroup = this;
        initSportDependentVars();
    }

    protected abstract void initSportDependentVars();

    protected void initViews() {
        initSportDependentViews();

        this.groupNameTV = view.findViewById(R.id.footballpageGroupNameTV);
        groupNameTV.setText(MyGlobals.group.getName().toString());

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

    protected abstract void initSportDependentViews();

    private void initRecyclers() {
        this.gamesRV = view.findViewById(R.id.footballpageGamesRV);
        sortGames();
        GamesRVAdapter gamesAdapter = new GamesRVAdapter(MyGlobals.group.getGames(), this);
        gamesRV.setAdapter(gamesAdapter);
        gamesRV.setLayoutManager(new LinearLayoutManager(activity));
    }

    private void initSettingsViews() {
        this.settingsProgressBar = view.findViewById(R.id.fbGroupSettingsProgressBar);

        this.leaveGroupBTN = view.findViewById(R.id.groupSettingsLeaveBTN);
        leaveGroupBTN.setOnClickListener(view -> onLeaveGroupBTNClick());

        this.deleteGroupBTN = view.findViewById(R.id.groupSettingsDeleteBTN);
        if (MyGlobals.associatedFBMember.getIsAdmin()) {
            deleteGroupBTN.setOnClickListener(view -> onDeleteGroupBTNClick());
        } else {
            deleteGroupBTN.setVisibility(View.GONE);
        }
    }

    /**
     * This method loads the needed data for the side drawer and opens it.
     */
    private void openSettings() {
        settingsMembersRV = view.findViewById(R.id.groupSettingsMembersRV);
        GroupSettingsMembersRVAdapter settingsMembersAdapter =
                new GroupSettingsMembersRVAdapter(MyGlobals.group.getMembers(),this, MyGlobals.associatedFBMember);
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

    protected abstract void onLeaveGroupBTNClick();

    protected abstract void onDeleteGroupBTNClick();

    protected abstract void onJoinAsNewMemberBTNClicked();

    protected abstract void onAddMemberBTNClick(View popupView, Button addBTN, EditText memberNameET);

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

        setUpSportSpecificGameDialog(popupView, game);

        // Show dialog
        dialogBuilder.setView(popupView);
        dialog = dialogBuilder.create();
    }

    protected abstract void setUpSportSpecificGameDialog(View popupView, Game<?,?> game);

    @Override
    public abstract void deleteMember(Member<?> member);

    /**
     * Method is called when joining from rv.
     */
    @Override
    public abstract void onMemberSelected(Member<?> member);

    /** ================= END LISTENER'S IMPLEMENTATION =================================== */

    /** ================= START HELPER FUNCTIONS =================================== */

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

    protected abstract void sortGames();

}