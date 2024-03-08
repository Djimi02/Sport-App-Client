package com.example.sport_app_client.gameActivities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.DragEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ViewFlipper;

import com.example.sport_app_client.R;
import com.example.sport_app_client.adapter.FootballGameMembersRVAdapter;
import com.example.sport_app_client.adapter.GameMembersRVAdapter;
import com.example.sport_app_client.adapter.GameTeamsRVAdapter;
import com.example.sport_app_client.helpers.MyGlobals;
import com.example.sport_app_client.interfaces.OnGameMemberDragListener;
import com.example.sport_app_client.model.member.FootballMember;
import com.example.sport_app_client.model.member.Member;

import java.util.ArrayList;
import java.util.List;

public class FootballGameActivity extends AppCompatActivity implements OnGameMemberDragListener {

    /* Views */
    private ViewFlipper viewFlipper;
    private Button backBTN;
    private Button nextBTN;
    private RecyclerView membersRV;
    private RecyclerView step1Team1RV;
    private RecyclerView step1Team2RV;
    private RecyclerView step2Team1RV;
    private RecyclerView step2Team2RV;


    /* Vars */
    private List<FootballMember> members; // links to the real members so modification is propagated
    private List<FootballMember> team1;
    private List<FootballMember> team2;
    private FootballMember draggedMember;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_football_game);
        this.getSupportActionBar().hide();

        initVars();
        initViews();
    }

    private void initVars() {
        this.members = MyGlobals.footballMembers;
        this.team1 = new ArrayList<>();
        this.team2 = new ArrayList<>();
    }

    private void initViews() {
        this.viewFlipper = findViewById(R.id.footballgameactivityVF);

        this.backBTN = findViewById(R.id.footballgameactivityBackBTN);
        backBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewFlipper.showPrevious();
                nextBTN.setEnabled(true);

                if (viewFlipper.getDisplayedChild() == 0) {
                    backBTN.setEnabled(false);
                }
            }
        });
        backBTN.setEnabled(false);

        this.nextBTN = findViewById(R.id.footballgameactivityNextBTN);
        nextBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewFlipper.showNext();
                backBTN.setEnabled(true);

                if (viewFlipper.getDisplayedChild() == 1) {
                    step2Team1RV.getAdapter().notifyDataSetChanged();
                    step2Team2RV.getAdapter().notifyDataSetChanged();
                }

                if (viewFlipper.getDisplayedChild() == 2) {
                    nextBTN.setEnabled(false);
                }
            }
        });

        initRecyclerViews();
    }

    private void initRecyclerViews() {
        this.membersRV = findViewById(R.id.footballgameStep1MembersRV);
        GameMembersRVAdapter membersAdapter = new GameMembersRVAdapter(members, this);
        membersRV.setAdapter(membersAdapter);
        membersRV.setLayoutManager(new LinearLayoutManager(this));

        this.step1Team1RV = findViewById(R.id.footballgameStep1Team1RV);
        GameTeamsRVAdapter step1Team1Adapter = new GameTeamsRVAdapter(team1);
        step1Team1RV.setAdapter(step1Team1Adapter);
        step1Team1RV.setLayoutManager(new LinearLayoutManager(this));
        step1Team1RV.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View view, DragEvent dragEvent) {
                switch (dragEvent.getAction()) {
                    case DragEvent.ACTION_DROP:
                        if (!(team2.contains(draggedMember) || team1.contains(draggedMember))) {
                            team1.add(draggedMember);
                        }
                        step1Team1Adapter.notifyDataSetChanged();
                        break;
                }
                return true;
            }
        });

        this.step1Team2RV = findViewById(R.id.footballgameStep1Team2RV);
        GameTeamsRVAdapter step1Team2Adapter = new GameTeamsRVAdapter(team2);
        step1Team2RV.setAdapter(step1Team2Adapter);
        step1Team2RV.setLayoutManager(new LinearLayoutManager(this));
        step1Team2RV.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View view, DragEvent dragEvent) {
                switch (dragEvent.getAction()) {
                    case DragEvent.ACTION_DROP:
                        if (!(team2.contains(draggedMember) || team1.contains(draggedMember))) {
                            team2.add(draggedMember);
                        }
                        step1Team2Adapter.notifyDataSetChanged();
                        break;
                }
                return true;
            }
        });

        this.step2Team1RV = findViewById(R.id.footballgameStep2Team1RV);
        FootballGameMembersRVAdapter step2Team1Adapter = new FootballGameMembersRVAdapter(team1);
        step2Team1RV.setAdapter(step2Team1Adapter);
        step2Team1RV.setLayoutManager(new LinearLayoutManager(this));

        this.step2Team2RV = findViewById(R.id.footballgameStep2Team2RV);
        FootballGameMembersRVAdapter step2Team2Adapter = new FootballGameMembersRVAdapter(team2);
        step2Team2RV.setAdapter(step2Team2Adapter);
        step2Team2RV.setLayoutManager(new LinearLayoutManager(this));
    }


    @Override
    public void draggedMember(Member member) {
        this.draggedMember = (FootballMember) member;
        this.membersRV.getAdapter().notifyDataSetChanged();
    }
}