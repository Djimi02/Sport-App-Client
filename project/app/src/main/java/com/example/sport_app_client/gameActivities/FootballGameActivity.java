package com.example.sport_app_client.gameActivities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ClipData;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.example.sport_app_client.R;
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
    private RecyclerView team1RV;
    private RecyclerView team2RV;


    /* Vars */
    private List<FootballMember> members;
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

                if (viewFlipper.getDisplayedChild() == 2) {
                    nextBTN.setEnabled(false);
                }
            }
        });

        this.membersRV = findViewById(R.id.footballgameStep1MembersRV);
        GameMembersRVAdapter membersAdapter = new GameMembersRVAdapter(members, this);
        membersRV.setAdapter(membersAdapter);
        membersRV.setLayoutManager(new LinearLayoutManager(this));

        this.team1RV = findViewById(R.id.footballgameStep1Team1RV);
        GameTeamsRVAdapter team1Adapter = new GameTeamsRVAdapter(team1);
        team1RV.setAdapter(team1Adapter);
        team1RV.setLayoutManager(new LinearLayoutManager(this));
        team1RV.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View view, DragEvent dragEvent) {
                switch (dragEvent.getAction()) {
                    case DragEvent.ACTION_DROP:
                        if (!(team2.contains(draggedMember) || team1.contains(draggedMember))) {
                            team1.add(draggedMember);
                        }
                        team1Adapter.notifyDataSetChanged();
                        break;
                }
                return true;
            }
        });

        this.team2RV = findViewById(R.id.footballgameStep1Team2RV);
        GameTeamsRVAdapter team2Adapter = new GameTeamsRVAdapter(team2);
        team2RV.setAdapter(team2Adapter);
        team2RV.setLayoutManager(new LinearLayoutManager(this));
        team2RV.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View view, DragEvent dragEvent) {
                switch (dragEvent.getAction()) {
                    case DragEvent.ACTION_DROP:
                        if (!(team2.contains(draggedMember) || team1.contains(draggedMember))) {
                            team2.add(draggedMember);
                        }
                        team2Adapter.notifyDataSetChanged();
                        break;
                }
                return true;
            }
        });
    }


    @Override
    public void draggedMember(Member member) {
        this.draggedMember = (FootballMember) member;
        this.membersRV.getAdapter().notifyDataSetChanged();
    }
}