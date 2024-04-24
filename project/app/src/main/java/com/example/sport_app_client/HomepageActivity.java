package com.example.sport_app_client;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sport_app_client.adapter.UserGroupsRVAdapter;
import com.example.sport_app_client.groupActivities.GroupActivity;
import com.example.sport_app_client.helpers.GlobalMethods;
import com.example.sport_app_client.helpers.MyGlobals;
import com.example.sport_app_client.interfaces.CreateOrJoinOrLeaveGroupListener;
import com.example.sport_app_client.interfaces.GameCreatedListener;
import com.example.sport_app_client.interfaces.UserGroupClickListener;
import com.example.sport_app_client.model.Sports;
import com.example.sport_app_client.model.group.FootballGroup;
import com.example.sport_app_client.model.group.Group;
import com.example.sport_app_client.model.member.FootballMember;
import com.example.sport_app_client.model.member.Member;
import com.example.sport_app_client.retrofit.MyAuthManager;
import com.example.sport_app_client.retrofit.RetrofitService;
import com.example.sport_app_client.retrofit.api.FbAPI;

import java.io.EOFException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class HomepageActivity extends AppCompatActivity implements UserGroupClickListener, CreateOrJoinOrLeaveGroupListener, GameCreatedListener {

    /* Views */
    private ProgressBar progressBar;
    private TextView totalGroups;
    private TextView totalLosesTV;
    private TextView totalDrawsTV;
    private TextView totalWinsTV;
    private RecyclerView userGroupsRV;
    private Button settingsBTN;
    private Button createGroupBTN;
    private Button findGroupBTN;


    /* Dialog */
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;

    /* Vars */
    private Retrofit retrofit;
    private FbAPI groupAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        this.getSupportActionBar().hide();
        MyGlobals.gameCreatedListenerHomepage = this;

        // Empty back button implementation
        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {}
        });

        MyGlobals.createJoinLeaveGroupListenerHomepageActivity = this;

        initVars();
        initViews();
        computeGeneralStats();
    }

    /** ==================== START CODE INITIALIZATION ======================================= */

    private void initVars() {
        this.retrofit = new RetrofitService().getRetrofit();
        this.groupAPI = retrofit.create(FbAPI.class);
    }

    private void initViews() {
        this.progressBar = findViewById(R.id.homepagePB);

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
            onGoToSettingsBTNClick();
        }));

        this.createGroupBTN = findViewById(R.id.homepageCreateGroupBTN);
        createGroupBTN.setOnClickListener(view -> {
            openCreateGroupDialog();
        });

        this.findGroupBTN = findViewById(R.id.homepageFindGroupBTN);
        findGroupBTN.setOnClickListener(v -> openFindViewDialog());
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
            onCreateGroupBTNClick(groupNameET, spinner);
        });

        // Show dialog
        dialogBuilder.setView(popupView);
        dialog = dialogBuilder.create();
        dialog.show();
    }

    private void openFindViewDialog() {
        // Build dialog
        dialogBuilder = new AlertDialog.Builder(this);
        final View popupView = getLayoutInflater().inflate(R.layout.find_group_dialog, null);

        // Init views
        Button btn = popupView.findViewById(R.id.findGroupDialogBTN);
        EditText et = popupView.findViewById(R.id.findGroupDialogET);
        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (et.getText().toString().trim().length() != 36) {
                    btn.setEnabled(false);
                    return;
                }
                btn.setEnabled(true);
            }
        });
        btn.setOnClickListener(v -> onJoinGroupBTNClick(et.getText().toString().trim(), btn));

        // Show dialog
        dialogBuilder.setView(popupView);
        dialog = dialogBuilder.create();
        dialog.show();
    }


    /** ==================== END CODE INITIALIZATION ========================================= */

    /** ======================== START REQUEST DATA ======================================== */

    private void requestGroupData(Member<?,?> member) {
        if (member == null) { // Something went wrong with intent data
            Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT).show(); // delete later
            return;
        }

        GlobalMethods.showPGAndBlockUI(progressBar, this);

        // Request group data
        groupAPI.getFootballGroupByID(member.getGroup().getId()).enqueue(new Callback<FootballGroup>() {
            @Override
            public void onResponse(Call<FootballGroup> call, Response<FootballGroup> response) {
                if (response.code() == 200) { // OK
                    MyGlobals.footballGroup = response.body();
                    MyGlobals.group = MyGlobals.footballGroup;

                    // Start group activity
                    Intent intent = new Intent(HomepageActivity.this, GroupActivity.class);
                    intent.putExtra("fragment", "FOOTBALL");
                    startActivity(intent);
                } else {
                    Toast.makeText(HomepageActivity.this, MyGlobals.ERROR_MESSAGE_1, Toast.LENGTH_SHORT).show();
                    Toast.makeText(HomepageActivity.this, MyGlobals.ERROR_MESSAGE_2, Toast.LENGTH_LONG).show();
                }
                GlobalMethods.hidePGAndEnableUi(progressBar, HomepageActivity.this);
            }

            @Override
            public void onFailure(Call<FootballGroup> call, Throwable t) {
                Toast.makeText(HomepageActivity.this, MyGlobals.ERROR_MESSAGE_1, Toast.LENGTH_SHORT).show();
                Toast.makeText(HomepageActivity.this, MyGlobals.ERROR_MESSAGE_2, Toast.LENGTH_LONG).show();
                GlobalMethods.hidePGAndEnableUi(progressBar, HomepageActivity.this);
                System.out.println(t.toString());
            }
        });
    }

    private void requestGroupData(String uuid) {
        if (uuid == null) { // Something went wrong with intent data
            Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT).show(); // delete later
            return;
        } else if (uuid.isEmpty()) { // Something went wrong with intent data
            Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT).show(); // delete later
            return;
        }

        GlobalMethods.showPGAndBlockUI(progressBar, this);

        // Request group data
        groupAPI.getFootballGroupByUUID(uuid).enqueue(new Callback<FootballGroup>() {
            @Override
            public void onResponse(Call<FootballGroup> call, Response<FootballGroup> response) {
                if (response.code() == 200) { // OK
                    if (isUserPartOfGroup(response.body().getId())) {
                        Toast.makeText(HomepageActivity.this, "You are already in this group!", Toast.LENGTH_SHORT).show();
                    } else {
                        MyGlobals.footballGroup = response.body();
                        MyGlobals.group = MyGlobals.footballGroup;

                        // Start group activity
                        Intent intent = new Intent(HomepageActivity.this, GroupActivity.class);
                        intent.putExtra("fragment", "FOOTBALL");
                        intent.putExtra("join", true);
                        startActivity(intent);
                    }
                } else {
                    Toast.makeText(HomepageActivity.this, MyGlobals.ERROR_MESSAGE_1, Toast.LENGTH_SHORT).show();
                    Toast.makeText(HomepageActivity.this, MyGlobals.ERROR_MESSAGE_2, Toast.LENGTH_LONG).show();
                }
                GlobalMethods.hidePGAndEnableUi(progressBar, HomepageActivity.this);
                dialog.dismiss();
            }

            @Override
            public void onFailure(Call<FootballGroup> call, Throwable t) {
                if (t instanceof EOFException) {
                    Toast.makeText(HomepageActivity.this, "Incorrect group code!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(HomepageActivity.this, MyGlobals.ERROR_MESSAGE_1, Toast.LENGTH_SHORT).show();
                    Toast.makeText(HomepageActivity.this, MyGlobals.ERROR_MESSAGE_2, Toast.LENGTH_LONG).show();
                }
                GlobalMethods.hidePGAndEnableUi(progressBar, HomepageActivity.this);
                dialog.dismiss();
                System.out.println(t.toString());
            }
        });
    }

    private void requestGroupCreation(String groupName) {
        GlobalMethods.showPGAndBlockUI(progressBar, this);

        // Send request
        groupAPI.createFootballGroup(groupName, MyAuthManager.user.getId()).enqueue(new Callback<FootballGroup>() {
            @Override
            public void onResponse(Call<FootballGroup> call, Response<FootballGroup> response) {
                if (response.code() == 200) { // ok
                    MyGlobals.footballGroup = response.body();
                    MyGlobals.group = MyGlobals.footballGroup;

                    FootballMember initialMember = MyGlobals.footballGroup.getMembers().get(0);
                    // Set temporary group so that they don't have cyclic references to each
                    // other which will throw exception during request sending
                    FootballGroup tempGroup = new FootballGroup(MyGlobals.footballGroup.getName());
                    tempGroup.setId(MyGlobals.footballGroup.getId());
                    initialMember.setGroup(tempGroup);
                    onGroupCreated(initialMember);

                    // Start group activity
                    Intent intent = new Intent(HomepageActivity.this, GroupActivity.class);
                    intent.putExtra("fragment", "FOOTBALL");
                    startActivity(intent);
                    dialog.dismiss();

                    Toast.makeText(HomepageActivity.this, "Group created successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(HomepageActivity.this, MyGlobals.ERROR_MESSAGE_1, Toast.LENGTH_SHORT).show();
                    Toast.makeText(HomepageActivity.this, MyGlobals.ERROR_MESSAGE_2, Toast.LENGTH_LONG).show();
                }
                GlobalMethods.hidePGAndEnableUi(progressBar, HomepageActivity.this);
            }

            @Override
            public void onFailure(Call<FootballGroup> call, Throwable t) {
                Toast.makeText(HomepageActivity.this, MyGlobals.ERROR_MESSAGE_2, Toast.LENGTH_LONG).show();
                GlobalMethods.hidePGAndEnableUi(progressBar, HomepageActivity.this);
            }
        });
    }

    /** ======================== END REQUEST DATA ======================================== */

    /** ==================== START BTN IMPLEMENTATION ========================================== */

    private void onGoToSettingsBTNClick() {
        Intent intent = new Intent(this, SettingsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }

    private void onCreateGroupBTNClick(EditText groupNameET, Spinner spinner) {
        String groupName = groupNameET.getText().toString().trim();
        if (groupName.isEmpty()) {
            groupNameET.setError("Add group name!");
            return;
        } else if (spinner.getSelectedItemPosition() == 0) {
            Toast.makeText(HomepageActivity.this, "Select a sport!", Toast.LENGTH_SHORT).show();
        }

        switch (spinner.getSelectedItem().toString()) {
            case "Football":
                requestGroupCreation(groupName);
                break;
        }
    }

    @Override
    public void openGroupInActivity(Member<?,?> member) {
        switch (member.getSport()) {
            case FOOTBALL:
                requestGroupData(member);
                break;
            default:
                break;
        }
    }

    private void onJoinGroupBTNClick(String uuid, Button btn) {
        GlobalMethods.hideSoftKeyboard(this);
        btn.setEnabled(false);
        requestGroupData(uuid);
    }

    /** ==================== END BTN IMPLEMENTATION ========================================== */

    private void computeGeneralStats() {
        // Compute total groups
        this.totalGroups.setText(Integer.toString(MyAuthManager.user.getMembers().size()));

        // Compute stats
        int wins = 0;
        int draws = 0;
        int loses = 0;
        for (Member<?,?> member : MyAuthManager.user.getMembers()) {
            wins += member.getStats().getWins();
            draws += member.getStats().getDraws();
            loses += member.getStats().getLoses();
        }
        this.totalWinsTV.setText(Integer.toString(wins));
        this.totalDrawsTV.setText(Integer.toString(draws));
        this.totalLosesTV.setText(Integer.toString(loses));
    }

    /** ================= START LISTENER'S IMPLEMENTATION =================================== */

    @Override
    public void onGroupRemoved(long memberID) {
        for (int i = 0; i < MyAuthManager.user.getMembers().size(); i++) {
            if (MyAuthManager.user.getMembers().get(i).getId() == memberID) {
                MyAuthManager.user.getMembers().remove(i);
                userGroupsRV.getAdapter().notifyItemRemoved(i); // update the rv
                break;
            }
        }
        computeGeneralStats();
    }

    @Override
    public void onGroupCreated(Member<?,?> member) {
        MyAuthManager.user.getMembers().add(member);
        totalGroups.setText("Total groups: " + MyAuthManager.user.getMembers().size());
        userGroupsRV.getAdapter().notifyItemInserted(MyAuthManager.user.getMembers().size()-1); // update the rv
    }

    @Override
    public void onGroupJoined(Member<?,?> member, Group<?,?> group) {
        // Create copies so that there are no cyclic references
        FootballGroup tempGroup = new FootballGroup(group.getName());
        tempGroup.setId(group.getId());
        tempGroup.setName(group.getName());
        tempGroup.setUuid(group.getUuid());

        FootballMember tempMember = new FootballMember(member.getNickname(), tempGroup);
        tempMember.getStats().setWins(member.getStats().getWins());
        tempMember.getStats().setDraws(member.getStats().getDraws());
        tempMember.getStats().setLoses(member.getStats().getLoses());
        tempMember.setId(member.getId());

        MyAuthManager.user.getMembers().add(tempMember);
        computeGeneralStats();
        userGroupsRV.getAdapter().notifyItemInserted(MyAuthManager.user.getMembers().size()-1); // update the rv
    }

    @Override
    public void onGameCreatedOrDeletedHomepageIMPL(Member<?,?> member) {
        for (int i = 0; i < MyAuthManager.user.getMembers().size(); i++) {
            if (MyAuthManager.user.getMembers().get(i).getId() == member.getId()) {
                MyAuthManager.user.getMembers().get(i).getStats().setWins(member.getStats().getWins());
                MyAuthManager.user.getMembers().get(i).getStats().setDraws(member.getStats().getDraws());
                MyAuthManager.user.getMembers().get(i).getStats().setLoses(member.getStats().getLoses());
                userGroupsRV.getAdapter().notifyItemChanged(i);
                break;
            }
        }

        computeGeneralStats();
    }

    /** ================= END LISTENER'S IMPLEMENTATION =================================== */

    /** ================= START HELPER FUNCTIONS =================================== */

    private boolean isUserPartOfGroup(long groupID) {
        for (Member<?,?> member : MyAuthManager.user.getMembers()) {
            if (member.getGroup().getId() == groupID) {
                return true;
            }
        }
        return false;
    }
}