package com.example.sport_app_client.groupActivities;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.ComponentActivity;
import androidx.activity.OnBackPressedCallback;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sport_app_client.R;
import com.example.sport_app_client.adapter.GamesRVAdapter;
import com.example.sport_app_client.adapter.GroupSettingsMembersRVAdapter;
import com.example.sport_app_client.adapter.SelectMemberToJoinGroupRVAdapter;
import com.example.sport_app_client.gameActivities.GameActivity;
import com.example.sport_app_client.helpers.GlobalMethods;
import com.example.sport_app_client.helpers.MyGlobals;
import com.example.sport_app_client.interfaces.GameClickListener;
import com.example.sport_app_client.interfaces.GameCreatedListener;
import com.example.sport_app_client.interfaces.GroupMemberDeletedListener;
import com.example.sport_app_client.interfaces.SelectMemberToJoinGroupListener;
import com.example.sport_app_client.interfaces.ViewAllStatsListener;
import com.example.sport_app_client.model.MemberRole;
import com.example.sport_app_client.model.Sports;
import com.example.sport_app_client.model.game.Game;
import com.example.sport_app_client.model.member.Member;

import java.util.ArrayList;
import java.util.List;

public abstract class GroupFragment extends Fragment implements
        GameCreatedListener,
        GameClickListener,
        GroupMemberDeletedListener,
        SelectMemberToJoinGroupListener,
        ViewAllStatsListener
{
    protected Activity activity;
    protected View view;
    protected LayoutInflater inflater;

    protected boolean isJoining;

    public GroupFragment(boolean isJoining) {
        this.isJoining = isJoining;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = getActivity();
        ((ComponentActivity)activity).getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (dialog == null) {}
                else if (dialog.isShowing()) {
                    dialog.dismiss();
                    return;
                }

                backToHomepage();
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.inflater = inflater;
        view = inflater.inflate(R.layout.group_fragment_layout, container, false);

        this.mainProgressBar = view.findViewById(R.id.GroupProgressBar);

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
    protected TextView memberNameTV;
    protected DrawerLayout drawerLayout;
    protected Button addMemberBTN;
    protected Button addGameBTN;
    protected Button settingsBTN;
    protected RecyclerView gamesRV;
    protected RecyclerView membersRV;
    protected ProgressBar mainProgressBar;
    protected Button homepageBTN;
    protected Spinner filterMembersSpinner;

    /* Join Group Dialog global views */
    protected ProgressBar joinGroupDialogPB;

    /* Settings Views */
    protected RecyclerView settingsMembersRV;
    protected Button leaveGroupBTN;
    protected Button deleteGroupBTN;
    protected Button copyGroupCodeBTN;
    protected ProgressBar settingsProgressBar;
    protected ProgressBar memberSettingsDialogPB;


    /* Dialog */
    protected AlertDialog.Builder dialogBuilder;
    protected AlertDialog dialog;

    /* Vars */
    protected List<Member> membersWithoutUsers;

    // ==================== START CODE INITIALIZATION =======================================

    /** This method initializes all the variables required for this fragment. */
    private void initVars() {
        MyGlobals.gameCreatedListenerGroup = this;
        initSportDependentVars();
    }

    /** This method should initialize the variables that are dependent on sport specific data. */
    protected abstract void initSportDependentVars();

    /** This method initializes all the views included in this fragment. */
    protected void initViews() {
        initSportDependentViews();

        this.groupNameTV = view.findViewById(R.id.GroupFragmentNameTV);
        groupNameTV.setText(MyGlobals.getGroup().getName().toString());

        this.memberNameTV = view.findViewById(R.id.GroupFragmentMemberNameTV);
        memberNameTV.setText(MyGlobals.getAssociatedMember().getNickname().toString());

        this.addMemberBTN = view.findViewById(R.id.GroupFragmentAddMemberBTN);
        this.addGameBTN = view.findViewById(R.id.GroupFragmentAddGameBTN);

        if (MyGlobals.getAssociatedMember().getRole() == MemberRole.GROUP_ADMIN ||
                MyGlobals.getAssociatedMember().getRole() == MemberRole.GAME_MAKER) {
            addMemberBTN.setOnClickListener((view -> openAddMemberDialog()));
            addGameBTN.setOnClickListener((view) -> onAddGameBTNClick(MyGlobals.getGroup().getSport()));
        } else {
            addMemberBTN.setVisibility(View.GONE);
            addGameBTN.setVisibility(View.GONE);
        }

        this.drawerLayout = view.findViewById(R.id.group_drawer_layout);
        // disabling sliding gestures
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                // disabling sliding gestures
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            }
        });

        this.settingsBTN = view.findViewById(R.id.GroupFragmentSettingsBTN);
        settingsBTN.setOnClickListener(view -> openSettings());

        this.homepageBTN = view.findViewById(R.id.GroupFragmentHomepageBTN);
        homepageBTN.setOnClickListener(v -> backToHomepage());

        initRecyclers();

        initSettingsViews();

        setSportSpecificDesign();
    }

    /** This method should initialize the views that are dependent on sport specific data. */
    protected abstract void initSportDependentViews();

    /** This method initializes all the recycler views included in this fragment. */
    private void initRecyclers() {
        this.gamesRV = view.findViewById(R.id.GroupFragmentGamesRV);
        sortGames();
        GamesRVAdapter gamesAdapter = new GamesRVAdapter(MyGlobals.getGroup().getGamesAbs(), this);
        gamesRV.setAdapter(gamesAdapter);
        gamesRV.setLayoutManager(new LinearLayoutManager(activity));
    }

    /** This method initializes all the views included in the side drawer. */
    private void initSettingsViews() {
        this.settingsProgressBar = view.findViewById(R.id.GroupSettingsProgressBar);

        this.leaveGroupBTN = view.findViewById(R.id.groupSettingsLeaveBTN);
        leaveGroupBTN.setOnClickListener(view -> onLeaveGroupBTNClick());

        this.deleteGroupBTN = view.findViewById(R.id.groupSettingsDeleteBTN);
        if (MyGlobals.getAssociatedMember().getRole() == MemberRole.GROUP_ADMIN) {
            deleteGroupBTN.setOnClickListener(view -> onDeleteGroupBTNClick());
        } else {
            deleteGroupBTN.setVisibility(View.GONE);
        }

        this.copyGroupCodeBTN = view.findViewById(R.id.groupSettingsCopyBTN);
        copyGroupCodeBTN.setOnClickListener(v -> copyGroupCodeToClipBoard());
    }

    /**
     * This method loads the needed data for the side drawer and opens it.
     */
    private void openSettings() {
        settingsMembersRV = view.findViewById(R.id.groupSettingsMembersRV);
        GroupSettingsMembersRVAdapter settingsMembersAdapter =
                new GroupSettingsMembersRVAdapter(MyGlobals.getGroup().getMembersAbs(),this, MyGlobals.getAssociatedMember());
        settingsMembersRV.setAdapter(settingsMembersAdapter);
        settingsMembersRV.setLayoutManager(new LinearLayoutManager(activity));

        // Unlock drawer sliding gestures
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        drawerLayout.open();
    }

    /**
     * This method initializes all the views in the layout R.layout.add_member_dialog
     * and opens it as dialog window.
     */
    private void openAddMemberDialog() {
        // Build dialog
        dialogBuilder = new AlertDialog.Builder(activity);
        final View popupView = getLayoutInflater().inflate(R.layout.add_member_dialog, null);

        // Init views
        final EditText memberNameET = popupView.findViewById(R.id.addMemberDialogET);
        final Button addBTN = popupView.findViewById(R.id.addMemberDialogBTN);
        addBTN.setOnClickListener(view -> onAddMemberBTNClick(popupView, addBTN, memberNameET));

        // Show dialog
        dialogBuilder.setView(popupView);
        dialog = dialogBuilder.create();
        dialog.show();
    }

    /**
     * This method initializes all the views in the layout R.layout.select_member_dialog
     * and opens it as dialog window.
     */
    private void openJoinGroupDialog() {
        // Build dialog
        dialogBuilder = new AlertDialog.Builder(activity);
        final View popupView = getLayoutInflater().inflate(R.layout.select_member_dialog, null);

        // Init dialog views
        this.joinGroupDialogPB = popupView.findViewById(R.id.selectMemberDialogPG);
        TextView groupName = popupView.findViewById(R.id.selectMemberDialogGroupNameTV);
        groupName.setText(MyGlobals.getGroup().getName().toString());
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

    /**
     * This method should set the sport specific design of the views in the fragment
     * such as background color. The position of the views are fixed.
     */
    protected abstract void setSportSpecificDesign();

    // ==================== END CODE INITIALIZATION =========================================

    // ==================== START BTN IMPLEMENTATION ==========================================

    /** This method starts GameActivity and passes the correct sport as intent string extra. */
    private void onAddGameBTNClick(Sports sport) {
        Intent intent = new Intent(activity, GameActivity.class);

        switch (sport){
            case FOOTBALL: intent.putExtra("sport", "FOOTBALL"); break;
            case BASKETBALL: intent.putExtra("sport", "BASKETBALL"); break;
            case TENNIS: break;
            case TABLE_TENNIS: break;
            default: return;
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }

    /** This method starts GameActivity and passes the correct sport as intent string extra. */
    protected abstract void onLeaveGroupBTNClick();

    /**
     * This method should call ConfirmActionDialog.showDialog() and pass as arguments the
     * currently active activity, the text of the dialog and the required action. The required
     * action is API.deleteGroup();. On successful request, a call to
     * MyGlobals.createOrJoinOrLeaveGroupListener.onGroupRemoved(); should be made.
     * Also GlobalMethods.showPGAndBlockUI(); and GlobalMethods.hidePGAndEnableUi(); should be
     * called before the request and on response/fail respectively.
     */
    protected abstract void onDeleteGroupBTNClick();

    /**
     * This method should call ConfirmActionDialog.showDialog() and pass as arguments the
     * currently active activity, the text of the dialog and the required action. The required
     * action is API.joinGroupAsNewMember();. On successful request, the method should:
     * 1. Update global group and member variables; 2. call initViews()
     * 3. call MyGlobals.createOrJoinOrLeaveGroupListener.onGroupJoined();
     * Also GlobalMethods.showPGAndBlockUI(); and GlobalMethods.hidePGAndEnableUi(); should be
     * called before the request and on response/fail respectively.
     */
    protected abstract void onJoinAsNewMemberBTNClicked();

    /**
     * This method checks if the name adheres to the requirements and if it does
     * it sends request to the server to save and return a member with the specified name.
     * @param popupView - view of the dialog
     * @param addBTN - the btn in the dialog
     * @param memberNameET - the edit text in the dialog
     */
    private void onAddMemberBTNClick(View popupView, Button addBTN, EditText memberNameET) {
        GlobalMethods.hideSoftKeyboard(popupView, activity);

        String memberName = memberNameET.getText().toString().trim();
        if (memberName.isEmpty()) {
            memberNameET.setError("Input member name!");
            return;
        } else if (memberName.length() > 10) {
            memberNameET.setError("Name can be 12 characters max!");
            return;
        }

        onAddMemberBTNClickSportSpecific(memberName);
    }

    /**
     * This method should send request via API.addFootballMember();. On successful response
     * the method should update the global group variable and update the this.membersRV adapter.
     * Also GlobalMethods.showPGAndBlockUI(); and GlobalMethods.hidePGAndEnableUi(); should be
     * called before the request and on response/fail respectively.
     * @param memberName - the name of the member to be created,saved and returned
     */
    protected abstract void onAddMemberBTNClickSportSpecific(String memberName);

    /**
     * This method should call ConfirmActionDialog.showDialog() and pass as arguments the
     * currently active activity, the text of the dialog and the required action. The required
     * action is API.removeMemberFromGroup();. On successful request, the member should be
     * removed from the global group variable and the recyclers should be updated;
     * Also GlobalMethods.showPGAndBlockUI(); and GlobalMethods.hidePGAndEnableUi(); should be
     * called before the request and on response/fail respectively.
     * @param member - member to be deleted
     */
    protected abstract void deleteMember(Member member);

    /**
     * This method should make the role of the specified member to admin by calling
     * API.setRoleToAdmin(). Also GlobalMethods.showPGAndBlockUI(); and
     * GlobalMethods.hidePGAndEnableUi(); should be called before the request
     * and on response/fail respectively.
     * @param member - member to be promoted to admin
     */
    protected abstract void setRoleToAdmin(Member member);

    /**
     * This method should make the role of the specified member to game maker by calling
     * API.setRoleToGameMaker(). Also GlobalMethods.showPGAndBlockUI(); and
     * GlobalMethods.hidePGAndEnableUi(); should be called before the request
     * and on response/fail respectively.
     * @param member - member whose role to be set to game maker
     */
    protected abstract void setRoleToGameMaker(Member member);

    /**
     * This method should make the role of the specified member to admin by calling
     * API.demoteMember(). Also GlobalMethods.showPGAndBlockUI(); and
     * GlobalMethods.hidePGAndEnableUi(); should be called before the request
     * and on response/fail respectively.
     * @param member - member to be demoted
     */
    protected abstract void setRoleToMember(Member member);

    private void copyGroupCodeToClipBoard() {
        ClipboardManager clipboard = (ClipboardManager)
                activity.getSystemService(Context.CLIPBOARD_SERVICE);

        // Creates a new text clip to put on the clipboard.
        ClipData clip = ClipData.newPlainText("Group code", MyGlobals.getGroup().getUuid().toString());

        // Set the clipboard's primary clip.
        clipboard.setPrimaryClip(clip);

        Toast.makeText(activity, "Group code copied!", Toast.LENGTH_SHORT).show();
    }

    // ==================== END BTN IMPLEMENTATION ==========================================

    // ================= START LISTENER'S IMPLEMENTATION ===================================

    @Override
    public void onGameCreatedGroupIMPL() {
        // Update games rv with new game
        this.gamesRV.getAdapter().notifyItemInserted(0);

        onGameCreatedSportSpecific();
    }

    public abstract void onGameCreatedSportSpecific();

    @Override
    public void openGameDialog(Game game) {
        // Build dialog
        dialogBuilder = new AlertDialog.Builder(activity);
        final View popupView = getLayoutInflater().inflate(R.layout.game_dialog, null);

        // Init general views views
        TextView date = popupView.findViewById(R.id.GameDialogDate);
        date.setText(game.getDate().toString());
        TextView result = popupView.findViewById(R.id.GameDialogResult);
        result.setText(game.getResults().toString());
        Button deleteBTN = popupView.findViewById(R.id.GameDialogDeleteBTN);

        setUpSportSpecificGameDialog(popupView, deleteBTN, game);

        // Show dialog
        dialogBuilder.setView(popupView);
        dialog = dialogBuilder.create();
    }

    /**
     * This method should initialize sport specific recycler views, btn functionality
     * and send request to load group data via API.getGameStats();
     * @param popupView - the dialog layout view
     * @param deleteBTN - the delete btn in the view
     * @param game - the game whose stats are to be loaded.
     */
    protected abstract void setUpSportSpecificGameDialog(View popupView, Button deleteBTN, Game game);

    @Override
    public void openMemberSettingsDialog(Member member) {
        // Build dialog
        dialogBuilder = new AlertDialog.Builder(activity);
        final View popupView = getLayoutInflater().inflate(R.layout.group_member_settings_dialog, null);

        // Init views
        this.memberSettingsDialogPB = popupView.findViewById(R.id.memberSettingsDialogPB);
        TextView nameTV = popupView.findViewById(R.id.memberSettingsDialogNameTV);
        nameTV.setText(member.getNickname());
        TextView roleTV = popupView.findViewById(R.id.memberSettingsDialogRoleTV);
        roleTV.setText(member.getRole().toString());
        Button roleBTN1 = popupView.findViewById(R.id.memberSettingsDialogRoleBTN1);
        Button roleBTN2 = popupView.findViewById(R.id.memberSettingsDialogRoleBTN2);
        if (member.getRole() == MemberRole.GROUP_ADMIN) {
            roleBTN1.setText("Demote to Game Maker");
            roleBTN1.setOnClickListener(v -> setRoleToGameMaker(member));

            roleBTN2.setText("Demote to Member");
            roleBTN2.setOnClickListener(v -> setRoleToMember(member));

            // TODO: Set colors
        } else if (member.getRole() == MemberRole.GAME_MAKER) {
            roleBTN1.setText("Promote to Admin");
            roleBTN1.setOnClickListener(v -> setRoleToAdmin(member));

            roleBTN2.setText("Demote to Member");
            roleBTN2.setOnClickListener(v -> setRoleToMember(member));

            // TODO: Set colors
        } else { // role is member
            roleBTN1.setText("Promote to Admin");
            roleBTN1.setOnClickListener(v -> setRoleToAdmin(member));

            roleBTN2.setText("Promote to Game Maker");
            roleBTN2.setOnClickListener(v -> setRoleToGameMaker(member));

            // TODO: Set colors
        }
        Button removeBTN = popupView.findViewById(R.id.memberSettingsDialogRemoveBTN);
        removeBTN.setOnClickListener(v -> deleteMember(member));

        // Show dialog
        dialogBuilder.setView(popupView);
        dialog = dialogBuilder.create();
        dialog.show();
    }

    @Override
    public abstract void onMemberSelected(Member member);

    @Override
    public abstract void onViewAllStatsClicked(Member member);

    // ================= END LISTENER'S IMPLEMENTATION ===================================

    // ================= START HELPER FUNCTIONS ===================================

    /**
     * The method fills in this.membersWithoutUsers with members from
     * MyGlobals.group who do not have associated user.
     */
    private void findMembersWithoutUsers() {
        if (membersWithoutUsers == null) {
            membersWithoutUsers = new ArrayList<>();
        } else {
            membersWithoutUsers.clear();
        }
        for (Member member : MyGlobals.getGroup().getMembersAbs()) {
            if (member.getUser() == null) {
                membersWithoutUsers.add(member);
            }
        }
    }

    /** This method should sort the games of sport specific group. */
    protected abstract void sortGames();

    /**
     * This method finishes the current activity, sets to null all the pointers to
     * group related objects and calls the garbage collection.
     */
    private void backToHomepage() {
        activity.finish();

        MyGlobals.gameCreatedListenerGroup = null;
        clearSportSpecificGroupData();

        System.gc();
    }

    protected abstract void clearSportSpecificGroupData();

}