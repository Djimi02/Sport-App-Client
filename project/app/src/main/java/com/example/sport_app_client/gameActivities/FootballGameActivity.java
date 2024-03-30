package com.example.sport_app_client.gameActivities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.DragEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.example.sport_app_client.R;
import com.example.sport_app_client.adapter.football.FBMembersGameStatsSelectorRVAdapter;
import com.example.sport_app_client.adapter.GroupMembersRVAdapter;
import com.example.sport_app_client.adapter.GameTeamsRVAdapter;
import com.example.sport_app_client.adapter.football.FBMemberGameStatsViewRVAdapter;
import com.example.sport_app_client.groupActivities.FBGroupFragment;
import com.example.sport_app_client.helpers.MyGlobals;
import com.example.sport_app_client.interfaces.OnGameMemberDragListener;
import com.example.sport_app_client.model.game.FootballGame;
import com.example.sport_app_client.model.member.FootballMember;
import com.example.sport_app_client.model.member.Member;
import com.example.sport_app_client.retrofit.RetrofitService;
import com.example.sport_app_client.retrofit.api.FbAPI;
import com.example.sport_app_client.retrofit.request.AddNewFBGameRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class FootballGameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blank_layout);
        this.getSupportActionBar().hide();

        // Get the data from the intent
        String fragmentToLoad = getIntent().getStringExtra("fragment");
        loadFragment(fragmentToLoad);
    }

    public void loadFragment(String fragmentToLoad) {
        Fragment fragment = null;

        // Determine which fragment to load based on the data
        switch (fragmentToLoad) {
            case "FOOTBALL":
                fragment = FBGameFragment.newInstance();
                break;
            case "-":
                break;
        }

        // Replace the container with the new fragment
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.groupFragmentContainer, fragment)
                    .commit();
        }
    }

}