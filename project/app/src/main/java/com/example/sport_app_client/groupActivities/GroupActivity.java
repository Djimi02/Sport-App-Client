package com.example.sport_app_client.groupActivities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.sport_app_client.R;
import com.example.sport_app_client.helpers.GlobalMethods;
import com.example.sport_app_client.helpers.GroupLoadConfig;
import com.example.sport_app_client.helpers.MyGlobals;
import com.example.sport_app_client.model.group.BasketballGroup;
import com.example.sport_app_client.model.group.FootballGroup;
import com.example.sport_app_client.model.member.Member;
import com.example.sport_app_client.retrofit.MyAuthManager;
import com.example.sport_app_client.retrofit.RetrofitService;
import com.example.sport_app_client.retrofit.api.BbApi;
import com.example.sport_app_client.retrofit.api.FbAPI;
import com.example.sport_app_client.retrofit.api.UuidAPI;
import com.example.sport_app_client.retrofit.response.GroupTypeResponse;

import java.io.EOFException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class GroupActivity extends AppCompatActivity {

    /* Views */
    private ProgressBar progressBar;

    /* Vars */
    private Retrofit retrofit;
    private UuidAPI uuidAPI;
    private FbAPI fbGroupAPI;
    private BbApi bbGroupAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blank_layout);
        this.getSupportActionBar().hide();

        progressBar = findViewById(R.id.blankLayoutPG);

        retrofit = new RetrofitService().getRetrofit();

        // Get the data from the intent
        boolean isJoiningGroup = getIntent().getBooleanExtra("join", false);
        String sport = getIntent().getStringExtra("sport");
        Long groupID = getIntent().getLongExtra("groupID", -1);
        String uuid = getIntent().getStringExtra("UUID");
        boolean isCreatingGroup = getIntent().getBooleanExtra("create", false);
        String groupNameToBeCreated = getIntent().getStringExtra("group_name");

        if (isCreatingGroup) { // Creating group
            createGroup(groupNameToBeCreated, sport);
        } else { // Loading group
            if (uuid == null) {
                loadGroupDataByID(sport, groupID, isJoiningGroup);
            } else {
                loadGroupDataByUUID(uuid, isJoiningGroup);
            }
        }

    }

    // =================== START Load by UUID ========================================

    private void loadGroupDataByUUID(String uuid, boolean isJoining) {
        this.uuidAPI = retrofit.create(UuidAPI.class);

        GlobalMethods.showPGAndBlockUI(progressBar, this);

        // Send request to get group type
        // On response send request to get group data
        uuidAPI.getGroupTypeByUUID(uuid).enqueue(new Callback<GroupTypeResponse>() {
            @Override
            public void onResponse(Call<GroupTypeResponse> call, Response<GroupTypeResponse> response) {
                if (response.code() == 200) { // OK
                    String sport = response.body().getGroupType();

                    switch (sport) {
                        case "FOOTBALL":
                            loadFBGroupDataByUUID(sport, uuid, isJoining);
                            break;
                        case "BASKETBALL":
                            loadBBGroupDataByUUID(sport, uuid, isJoining);
                            break;
                    }
                } else {
                    Toast.makeText(GroupActivity.this, MyGlobals.ERROR_MESSAGE_1, Toast.LENGTH_SHORT).show();
                    Toast.makeText(GroupActivity.this, MyGlobals.ERROR_MESSAGE_2, Toast.LENGTH_LONG).show();
                    GlobalMethods.hidePGAndEnableUi(progressBar, GroupActivity.this);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<GroupTypeResponse> call, Throwable t) {
                Toast.makeText(GroupActivity.this, MyGlobals.ERROR_MESSAGE_1, Toast.LENGTH_SHORT).show();
                Toast.makeText(GroupActivity.this, MyGlobals.ERROR_MESSAGE_2, Toast.LENGTH_LONG).show();
                GlobalMethods.hidePGAndEnableUi(progressBar, GroupActivity.this);
                finish();
            }
        });

    }

    private void loadFBGroupDataByUUID(String sport, String uuid, boolean isJoining) {
        this.fbGroupAPI = retrofit.create(FbAPI.class);

        // Request group data
        fbGroupAPI.getGroupByUUID(uuid).enqueue(new Callback<FootballGroup>() {
            @Override
            public void onResponse(Call<FootballGroup> call, Response<FootballGroup> response) {
                if (response.code() == 200) { // OK
                    if (isUserPartOfGroup(response.body().getId())) {
                        Toast.makeText(GroupActivity.this, "You are already in this group!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        GroupLoadConfig.configureFBGroupData(response.body());

                        loadFragment(sport, isJoining);
                    }
                } else {
                    Toast.makeText(GroupActivity.this, MyGlobals.ERROR_MESSAGE_1, Toast.LENGTH_SHORT).show();
                    Toast.makeText(GroupActivity.this, MyGlobals.ERROR_MESSAGE_2, Toast.LENGTH_LONG).show();
                    finish();
                }
                GlobalMethods.hidePGAndEnableUi(progressBar, GroupActivity.this);
            }

            @Override
            public void onFailure(Call<FootballGroup> call, Throwable t) {
                if (t instanceof EOFException) {
                    Toast.makeText(GroupActivity.this, "Incorrect group code!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(GroupActivity.this, MyGlobals.ERROR_MESSAGE_1, Toast.LENGTH_SHORT).show();
                    Toast.makeText(GroupActivity.this, MyGlobals.ERROR_MESSAGE_2, Toast.LENGTH_LONG).show();
                }
                finish();
                GlobalMethods.hidePGAndEnableUi(progressBar, GroupActivity.this);
                System.out.println(t.toString());
            }
        });
    }

    private void loadBBGroupDataByUUID(String sport, String uuid, boolean isJoining) {
        this.bbGroupAPI = retrofit.create(BbApi.class);

        // Request group data
        bbGroupAPI.getGroupByUUID(uuid).enqueue(new Callback<BasketballGroup>() {
            @Override
            public void onResponse(Call<BasketballGroup> call, Response<BasketballGroup> response) {
                if (response.code() == 200) { // OK
                    if (isUserPartOfGroup(response.body().getId())) {
                        Toast.makeText(GroupActivity.this, "You are already in this group!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        GroupLoadConfig.configureBBGroupData(response.body());

                        loadFragment(sport, isJoining);
                    }
                } else {
                    Toast.makeText(GroupActivity.this, MyGlobals.ERROR_MESSAGE_1, Toast.LENGTH_SHORT).show();
                    Toast.makeText(GroupActivity.this, MyGlobals.ERROR_MESSAGE_2, Toast.LENGTH_LONG).show();
                    finish();
                }
                GlobalMethods.hidePGAndEnableUi(progressBar, GroupActivity.this);
            }

            @Override
            public void onFailure(Call<BasketballGroup> call, Throwable t) {
                if (t instanceof EOFException) {
                    Toast.makeText(GroupActivity.this, "Incorrect group code!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(GroupActivity.this, MyGlobals.ERROR_MESSAGE_1, Toast.LENGTH_SHORT).show();
                    Toast.makeText(GroupActivity.this, MyGlobals.ERROR_MESSAGE_2, Toast.LENGTH_LONG).show();
                }
                finish();
                GlobalMethods.hidePGAndEnableUi(progressBar, GroupActivity.this);
                System.out.println(t.toString());
            }
        });
    }

    // =================== END Load by UUID ========================================

    // =================== START Load by ID ========================================

    private void loadGroupDataByID(String sport, Long groupID, boolean isJoining) {
        if (sport == null) {
            Toast.makeText(this, "Try again later!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        switch (sport) {
            case "FOOTBALL":
                loadFBGroupDataByID(sport, groupID, isJoining);
                break;

            case "BASKETBALL":
                loadBBGroupDataByID(sport, groupID, isJoining);
                break;
        }
    }

    private void loadFBGroupDataByID(String sport, Long groupID, boolean isJoining) {
        this.fbGroupAPI = retrofit.create(FbAPI.class);

        GlobalMethods.showPGAndBlockUI(progressBar, this);

        // Request group data
        fbGroupAPI.getGroupByID(groupID).enqueue(new Callback<FootballGroup>() {
            @Override
            public void onResponse(Call<FootballGroup> call, Response<FootballGroup> response) {
                if (response.code() == 200) { // OK
                    GroupLoadConfig.configureFBGroupData(response.body());

                    loadFragment(sport, isJoining);
                } else {
                    Toast.makeText(GroupActivity.this, MyGlobals.ERROR_MESSAGE_1, Toast.LENGTH_SHORT).show();
                    Toast.makeText(GroupActivity.this, MyGlobals.ERROR_MESSAGE_2, Toast.LENGTH_LONG).show();
                    finish(); // Exit activity on failure
                }
                GlobalMethods.hidePGAndEnableUi(progressBar, GroupActivity.this);
            }

            @Override
            public void onFailure(Call<FootballGroup> call, Throwable t) {
                Toast.makeText(GroupActivity.this, MyGlobals.ERROR_MESSAGE_1, Toast.LENGTH_SHORT).show();
                Toast.makeText(GroupActivity.this, MyGlobals.ERROR_MESSAGE_2, Toast.LENGTH_LONG).show();
                finish(); // Exit activity on failure
                GlobalMethods.hidePGAndEnableUi(progressBar, GroupActivity.this);
                System.out.println(t.toString());
            }
        });
    }

    private void loadBBGroupDataByID(String sport, Long groupID, boolean isJoining) {
        this.bbGroupAPI = retrofit.create(BbApi.class);

        GlobalMethods.showPGAndBlockUI(progressBar, this);

        // Request group data
        bbGroupAPI.getGroupByID(groupID).enqueue(new Callback<BasketballGroup>() {
            @Override
            public void onResponse(Call<BasketballGroup> call, Response<BasketballGroup> response) {
                if (response.code() == 200) { // OK
                    GroupLoadConfig.configureBBGroupData(response.body());

                    loadFragment(sport, isJoining);
                } else {
                    Toast.makeText(GroupActivity.this, MyGlobals.ERROR_MESSAGE_1, Toast.LENGTH_SHORT).show();
                    Toast.makeText(GroupActivity.this, MyGlobals.ERROR_MESSAGE_2, Toast.LENGTH_LONG).show();
                    finish(); // Exit activity on failure
                }
                GlobalMethods.hidePGAndEnableUi(progressBar, GroupActivity.this);
            }

            @Override
            public void onFailure(Call<BasketballGroup> call, Throwable t) {
                Toast.makeText(GroupActivity.this, MyGlobals.ERROR_MESSAGE_1, Toast.LENGTH_SHORT).show();
                Toast.makeText(GroupActivity.this, MyGlobals.ERROR_MESSAGE_2, Toast.LENGTH_LONG).show();
                finish(); // Exit activity on failure
                GlobalMethods.hidePGAndEnableUi(progressBar, GroupActivity.this);
                System.out.println(t.toString());
            }
        });
    }

    // =================== END Load by UUID ========================================

    // =================== START Group Creation ===================================

    private void createGroup(String groupName, String sport) {
        if (sport == null || groupName == null) {
            Toast.makeText(this, "Try again later!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        switch (sport) {
            case "FOOTBALL":
                createFBGroup(groupName, sport);
                break;

            case "BASKETBALL":
                createBBGroup(groupName, sport);
                break;
        }
    }

    private void createFBGroup(String groupName, String sport) {
        this.fbGroupAPI = retrofit.create(FbAPI.class);

        GlobalMethods.showPGAndBlockUI(progressBar, this);

        // Send request
        fbGroupAPI.createGroup(groupName, MyAuthManager.user.getId()).enqueue(new Callback<FootballGroup>() {
            @Override
            public void onResponse(Call<FootballGroup> call, Response<FootballGroup> response) {
                if (response.code() == 200) { // ok
                    GroupLoadConfig.configureFBGroupData(response.body());

                    MyGlobals.createJoinLeaveGroupListenerHomepageActivity.onGroupCreated(response.body());

                    loadFragment(sport, false);

                    Toast.makeText(GroupActivity.this, "Group created successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(GroupActivity.this, MyGlobals.ERROR_MESSAGE_1, Toast.LENGTH_SHORT).show();
                    Toast.makeText(GroupActivity.this, MyGlobals.ERROR_MESSAGE_2, Toast.LENGTH_LONG).show();
                    finish(); // Exit activity on failure
                }
                GlobalMethods.hidePGAndEnableUi(progressBar, GroupActivity.this);
            }

            @Override
            public void onFailure(Call<FootballGroup> call, Throwable t) {
                Toast.makeText(GroupActivity.this, MyGlobals.ERROR_MESSAGE_2, Toast.LENGTH_LONG).show();
                finish(); // Exit activity on failure
                GlobalMethods.hidePGAndEnableUi(progressBar, GroupActivity.this);
            }
        });
    }

    private void createBBGroup(String groupName, String sport) {
        this.bbGroupAPI = retrofit.create(BbApi.class);

        GlobalMethods.showPGAndBlockUI(progressBar, this);

        // Send request
        bbGroupAPI.createGroup(groupName, MyAuthManager.user.getId()).enqueue(new Callback<BasketballGroup>() {
            @Override
            public void onResponse(Call<BasketballGroup> call, Response<BasketballGroup> response) {
                if (response.code() == 200) { // ok
                    GroupLoadConfig.configureBBGroupData(response.body());

                    MyGlobals.createJoinLeaveGroupListenerHomepageActivity.onGroupCreated(response.body());

                    loadFragment(sport, false);

                    Toast.makeText(GroupActivity.this, "Group created successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(GroupActivity.this, MyGlobals.ERROR_MESSAGE_1, Toast.LENGTH_SHORT).show();
                    Toast.makeText(GroupActivity.this, MyGlobals.ERROR_MESSAGE_2, Toast.LENGTH_LONG).show();
                    finish(); // Exit activity on failure
                }
                GlobalMethods.hidePGAndEnableUi(progressBar, GroupActivity.this);
            }

            @Override
            public void onFailure(Call<BasketballGroup> call, Throwable t) {
                Toast.makeText(GroupActivity.this, MyGlobals.ERROR_MESSAGE_2, Toast.LENGTH_LONG).show();
                finish(); // Exit activity on failure
                GlobalMethods.hidePGAndEnableUi(progressBar, GroupActivity.this);
            }
        });
    }

    // =================== END Group Creation ===================================

    private void loadFragment(String sport, boolean isJoining) {
        Fragment fragment = null;

        // Determine which fragment to load based on the data
        switch (sport) {
            case "FOOTBALL":
                fragment = FBGroupFragment.newInstance(isJoining);
                break;

            case "BASKETBALL":
                fragment = BBGroupFragment.newInstance(isJoining);
                break;
        }

        // Replace the container with the new fragment
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, fragment)
                    .commit();
        }
    }

    private boolean isUserPartOfGroup(long groupID) {
        for (Member member : MyAuthManager.user.getMembers()) {
            if (member.getGroupAbs().getId() == groupID) {
                return true;
            }
        }
        return false;
    }

}