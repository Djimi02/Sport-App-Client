package com.example.sport_app_client.gameActivities;

import android.app.Activity;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.example.sport_app_client.R;
import com.example.sport_app_client.adapter.GameTeamsRVAdapter;
import com.example.sport_app_client.adapter.GroupMembersRVAdapter;
import com.example.sport_app_client.adapter.football.FBMemberGameStatsViewRVAdapter;
import com.example.sport_app_client.adapter.football.FBMembersGameStatsSelectorRVAdapter;
import com.example.sport_app_client.groupActivities.FBGroupFragment;
import com.example.sport_app_client.helpers.GlobalMethods;
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

public class FBGameFragment extends Fragment implements OnGameMemberDragListener {

    private Activity activity;
    private View view;

    public FBGameFragment() {}

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment FBGroupFragment.
     */
    public static FBGameFragment newInstance() {
        FBGameFragment fragment = new FBGameFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fb_game_fragment_layout, container, false);

        initVars();
        initViews();

        return view;
    }

    private final int MAX_TEAM_SIZE = 11;

    /** Views */
    private ProgressBar progressBar;
    private ViewFlipper viewFlipper;
    private Button backBTN;
    private Button nextBTN;
    private RecyclerView membersRV;
    private RecyclerView randomMembersRV;
    private RadioButton step1ManualSelectionRB; // I am using only this btn to get info
    private LinearLayout step1RandomMembersLayout;
    private Button step1RandomBTN;
    private RecyclerView step1Team1RV;
    private RecyclerView step1Team2RV;
    private RecyclerView step2Team1RV;
    private RecyclerView step2Team2RV;
    private RecyclerView step3Team1RV;
    private RecyclerView step3Team2RV;


    /** Vars */
    private List<FootballMember> members; // links to the real members so modification is propagated
    private List<FootballMember> team1;
    private List<FootballMember> team2;
    private List<FootballMember> step1RandomMembers;
    private FootballMember draggedMember;
    private List<FootballMember> step3Team1;
    private List<FootballMember> step3Team2;
    private List<FootballMember> allMembersWithUpdatedStats;
    private List<FootballMember> allTemporaryMembers; // represent game stats
    private HashMap<FootballMember, FootballMember> currentGameStatsTeam1;
    private HashMap<FootballMember, FootballMember> currentGameStatsTeam2;
    private Integer victory;

    /** Retrofit */
    private FbAPI footballGroupAPI;

    /** ==================== START CODE INITIALIZATION ======================================= */

    private void initVars() {
        this.members = MyGlobals.footballGroup.getMembers();

        this.team1 = new ArrayList<>();
        this.team2 = new ArrayList<>();
        this.step1RandomMembers = new ArrayList<>();
        this.step3Team1 = new ArrayList<>();
        this.step3Team2 = new ArrayList<>();
        this.allMembersWithUpdatedStats = new ArrayList<>();
        this.allTemporaryMembers = new ArrayList<>();

        this.footballGroupAPI = new RetrofitService().getRetrofit().create(FbAPI.class);
    }

    private void initViews() {
        this.viewFlipper = view.findViewById(R.id.footballgameactivityVF);

        this.progressBar = view.findViewById(R.id.fbGameProgressBar);

        this.backBTN = view.findViewById(R.id.footballgameactivityBackBTN);
        backBTN.setOnClickListener((view -> {
            backBtnPressed();
        }));
        backBTN.setEnabled(false);

        this.nextBTN = view.findViewById(R.id.footballgameactivityNextBTN);
        this.nextBTN.setText("Confirm Teams");
        nextBTN.setOnClickListener((view -> {
            nextBtnPressed();
        }));

        this.step1RandomMembersLayout = view.findViewById(R.id.footballgameStep1RandomLayout);
        step1RandomMembersLayout.setVisibility(View.GONE);

        // I am using only this btn to get info
        this.step1ManualSelectionRB = view.findViewById(R.id.footballgameStep1RB1);
        step1ManualSelectionRB.setChecked(true);
        step1ManualSelectionRB.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                step1RandomMembersLayout.setVisibility(View.GONE);
            } else {
                step1RandomMembersLayout.setVisibility(View.VISIBLE);
            }
        });

        this.step1RandomBTN = view.findViewById(R.id.footballgameStep1RandomBTN);
        step1RandomBTN.setOnClickListener(view -> {
            GlobalMethods.showPGAndBlockUI(progressBar, activity);

            generateRandomTeams();

            GlobalMethods.hidePGAndEnableUi(progressBar, activity);
        });

        initRecyclerViews();
    }

    private void initRecyclerViews() {

        // Step 1
        this.membersRV = view.findViewById(R.id.footballgameStep1MembersRV);
        GroupMembersRVAdapter membersAdapter = new GroupMembersRVAdapter(members, this);
        membersRV.setAdapter(membersAdapter);
        membersRV.setLayoutManager(new LinearLayoutManager(activity));

        this.step1Team1RV = view.findViewById(R.id.footballgameStep1Team1RV);
        GameTeamsRVAdapter step1Team1Adapter = new GameTeamsRVAdapter(team1);
        step1Team1RV.setAdapter(step1Team1Adapter);
        step1Team1RV.setLayoutManager(new LinearLayoutManager(activity));
        step1Team1RV.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View view, DragEvent dragEvent) {
                switch (dragEvent.getAction()) {
                    case DragEvent.ACTION_DROP:
                        if (!(team2.contains(draggedMember) || team1.contains(draggedMember) || !(step1ManualSelectionRB.isChecked()))) {
                            team1.add(draggedMember);
                            step1Team1Adapter.notifyDataSetChanged();
                        }
                        break;
                }
                return true;
            }
        });

        this.step1Team2RV = view.findViewById(R.id.footballgameStep1Team2RV);
        GameTeamsRVAdapter step1Team2Adapter = new GameTeamsRVAdapter(team2);
        step1Team2RV.setAdapter(step1Team2Adapter);
        step1Team2RV.setLayoutManager(new LinearLayoutManager(activity));
        step1Team2RV.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View view, DragEvent dragEvent) {
                switch (dragEvent.getAction()) {
                    case DragEvent.ACTION_DROP:
                        if (!(team2.contains(draggedMember) || team1.contains(draggedMember) || !(step1ManualSelectionRB.isChecked()))) {
                            team2.add(draggedMember);
                            step1Team2Adapter.notifyDataSetChanged();
                        }
                        break;
                }
                return true;
            }
        });

        this.randomMembersRV = view.findViewById(R.id.footballgameStep1RandomMembersRV);
        GameTeamsRVAdapter randomMembersAdapter = new GameTeamsRVAdapter(step1RandomMembers);
        randomMembersRV.setAdapter(randomMembersAdapter);
        randomMembersRV.setLayoutManager(new LinearLayoutManager(activity));
        randomMembersRV.setOnDragListener((view, dragEvent) -> {
            switch (dragEvent.getAction()) {
                case DragEvent.ACTION_DROP:
                    if (!(step1RandomMembers.contains(draggedMember))) {
                        step1RandomMembers.add(draggedMember);
                    }
                    randomMembersAdapter.notifyDataSetChanged();
                    break;
            }
            return true;
        });

        // Step 2
        this.step2Team1RV = view.findViewById(R.id.footballgameStep2Team1RV);
        FBMembersGameStatsSelectorRVAdapter step2Team1Adapter = new FBMembersGameStatsSelectorRVAdapter(team1);
        step2Team1RV.setAdapter(step2Team1Adapter);
        step2Team1RV.setLayoutManager(new LinearLayoutManager(activity));

        this.step2Team2RV = view.findViewById(R.id.footballgameStep2Team2RV);
        FBMembersGameStatsSelectorRVAdapter step2Team2Adapter = new FBMembersGameStatsSelectorRVAdapter(team2);
        step2Team2RV.setAdapter(step2Team2Adapter);
        step2Team2RV.setLayoutManager(new LinearLayoutManager(activity));

        // Step 3
        this.step3Team1RV = view.findViewById(R.id.footballgameStep3Team1RV);
        FBMemberGameStatsViewRVAdapter step3Team1Adapter = new FBMemberGameStatsViewRVAdapter(step3Team1);
        step3Team1RV.setAdapter(step3Team1Adapter);
        step3Team1RV.setLayoutManager(new LinearLayoutManager(activity));

        this.step3Team2RV = view.findViewById(R.id.footballgameStep3Team2RV);
        FBMemberGameStatsViewRVAdapter step3Team2Adapter = new FBMemberGameStatsViewRVAdapter(step3Team2);
        step3Team2RV.setAdapter(step3Team2Adapter);
        step3Team2RV.setLayoutManager(new LinearLayoutManager(activity));
    }

    /** ==================== END CODE INITIALIZATION ========================================= */

    /** ==================== START BTN IMPLEMENTATION ========================================== */

    /**
     * This method spreads randomly the members from this.step1RandomMembers to
     * this.team1 and this.team2.
     */
    private void generateRandomTeams() {
        int membersCount = step1RandomMembers.size();
        boolean[] usedMemberIndexes = new boolean[membersCount];
        boolean nextMemberToBeInTeam1 = true;
        Random random = new Random();

        // Clear the teams initially
        this.team1.clear();
        this.team2.clear();

        // Spread members randomly
        for (int i = 0; i < membersCount; i++) {
            int randomMemberIndex = random.nextInt(membersCount);
            while (usedMemberIndexes[randomMemberIndex]) { // select unselected member
                randomMemberIndex = random.nextInt(membersCount);
            }
            usedMemberIndexes[randomMemberIndex] = true; // mark this member as selected

            if (nextMemberToBeInTeam1) {
                team1.add(step1RandomMembers.get(randomMemberIndex));
                nextMemberToBeInTeam1 = false;
            } else {
                team2.add(step1RandomMembers.get(randomMemberIndex));
                nextMemberToBeInTeam1 = true;
            }
        }

        // Update recyclers
        step1Team1RV.getAdapter().notifyDataSetChanged();
        step1Team2RV.getAdapter().notifyDataSetChanged();
    }

    /**
     * This method implements the functionality of "NEXT" button.
     */
    private void nextBtnPressed() {
        if (viewFlipper.getDisplayedChild() == 0) { // Confirm teams pressed
            if (!goFromStep1ToStep2()) {
                return;
            }
        }
        else if (viewFlipper.getDisplayedChild() == 1) { // Confirm stats
            goFromStep2ToStep3();
        }
        else if (viewFlipper.getDisplayedChild() == 2) { // Confirm game pressed
            finalizeStep3();
            return;
        }

        viewFlipper.showNext();
        backBTN.setEnabled(true);

        if (viewFlipper.getDisplayedChild() == 1) { // After confirm teams pressed
            nextBTN.setText("Confirm Stats");
        }
        else if (viewFlipper.getDisplayedChild() == 2) { // After confirm stats pressed
            nextBTN.setText("Confirm Game");
        }
    }

    private boolean goFromStep1ToStep2() {
        if (Math.abs(team1.size() - team2.size()) > 1) {
            Toast.makeText(activity, "Team size difference of more than 1 not allowed!", Toast.LENGTH_LONG).show();
            return false;
        } else if (team1.size() == 0 || team2.size() == 0) {
            Toast.makeText(activity, "Empty teams are not allowed!", Toast.LENGTH_SHORT).show();
            return false;
        } else if (team1.size() > MAX_TEAM_SIZE || team2.size() > MAX_TEAM_SIZE) {
            Toast.makeText(activity, "Teams bigger than " + MAX_TEAM_SIZE + "are not allowed!", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Update step 2 based on step 1
        step2Team1RV.getAdapter().notifyDataSetChanged();
        step2Team2RV.getAdapter().notifyDataSetChanged();

        return true;
    }

    private void goFromStep2ToStep3() {
        // update step 3 based on step 2
        step3Team1.clear();
        step3Team1.addAll(((FBMembersGameStatsSelectorRVAdapter)step2Team1RV.getAdapter()).getCurrentGameStats().values());
        step3Team1RV.getAdapter().notifyDataSetChanged();
        step3Team2.clear();
        step3Team2.addAll(((FBMembersGameStatsSelectorRVAdapter)step2Team2RV.getAdapter()).getCurrentGameStats().values());
        step3Team2RV.getAdapter().notifyDataSetChanged();
    }

    private void finalizeStep3() {
        collectAndUpdateGameStats(); // Collect game stats

        // Create request
        AddNewFBGameRequest request = new AddNewFBGameRequest();
        request.setUpdatedMembers(allMembersWithUpdatedStats);
        request.setGroupID(MyGlobals.footballGroup.getId());
        request.setVictory(victory);
        request.setMembersGameStats(allTemporaryMembers);

        GlobalMethods.showPGAndBlockUI(progressBar, activity);

        // Send request
        footballGroupAPI.addNewFootballGame(request).enqueue(new Callback<FootballGame>() {
            @Override
            public void onResponse(Call<FootballGame> call, Response<FootballGame> response) {
                if (response.code() == 200) {
                    Toast.makeText(activity, "Game created successfully!", Toast.LENGTH_SHORT).show();
                    MyGlobals.footballGroup.addGame(response.body());
                    // Update group and homepage
                    MyGlobals.gameCreatedListenerGroup.onGameCreatedGroupIMPL();
                    MyGlobals.gameCreatedListenerHomepage.onGameCreatedOrDeletedHomepageIMPL(MyGlobals.associatedFBMember);

                    activity.finish();
                } else {
                    Toast.makeText(activity, "Game creation failed!", Toast.LENGTH_SHORT).show();
                    Toast.makeText(activity, MyGlobals.ERROR_MESSAGE_2, Toast.LENGTH_SHORT).show();
                    undoCollectGameStats();
                    System.out.println("CODE = " + response.code());
                }
                GlobalMethods.hidePGAndEnableUi(progressBar, activity);
            }

            @Override
            public void onFailure(Call<FootballGame> call, Throwable t) {
                Toast.makeText(activity, "Game creation failed!", Toast.LENGTH_SHORT).show();
                Toast.makeText(activity, MyGlobals.ERROR_MESSAGE_2, Toast.LENGTH_SHORT).show();
                undoCollectGameStats();
                GlobalMethods.hidePGAndEnableUi(progressBar, activity);

                System.out.println(t.toString());
            }
        });
    }

    /**
     * Updates all selected members with their new stat selected in step 2
     * and adds them in this.allMembersWithUpdatedStats and adds all game stats
     * in this.allTemporaryMembers.
     */
    private void collectAndUpdateGameStats() {
        // Collect and update team1 members with stats from the game
        int team1TotalGoals = 0;
        currentGameStatsTeam1 =
                ((FBMembersGameStatsSelectorRVAdapter) step2Team1RV.getAdapter()).getCurrentGameStats();
        for (FootballMember member : currentGameStatsTeam1.keySet()) {
            FootballMember tempMember = currentGameStatsTeam1.get(member);
            tempMember.setIsPartOfTeam1(true);

            member.setGoals(member.getGoals() + tempMember.getGoals());
            member.setAssists(member.getAssists() + tempMember.getAssists());
            member.setSaves(member.getSaves() + tempMember.getSaves());
            member.setFouls(member.getFouls() + tempMember.getFouls());

            allMembersWithUpdatedStats.add(member);
            allTemporaryMembers.add(tempMember);

            team1TotalGoals += tempMember.getGoals();
        }

        // Collect and update team2 members with stats from the game
        int team2TotalGoals = 0;
        currentGameStatsTeam2 =
                ((FBMembersGameStatsSelectorRVAdapter) step2Team2RV.getAdapter()).getCurrentGameStats();
        for (FootballMember member : currentGameStatsTeam2.keySet()) {
            FootballMember tempMember = currentGameStatsTeam2.get(member);
            tempMember.setIsPartOfTeam1(false);

            member.setGoals(member.getGoals() + tempMember.getGoals());
            member.setAssists(member.getAssists() + tempMember.getAssists());
            member.setSaves(member.getSaves() + tempMember.getSaves());
            member.setFouls(member.getFouls() + tempMember.getFouls());

            allMembersWithUpdatedStats.add(member);
            allTemporaryMembers.add(tempMember);

            team2TotalGoals += tempMember.getGoals();
        }

        // Increment wins/draws/loses
        if (team1TotalGoals > team2TotalGoals) {
            this.victory = -1;
            new ArrayList<>(currentGameStatsTeam1.keySet()).forEach((member) -> member.setWins(member.getWins()+1));
            new ArrayList<>(currentGameStatsTeam2.keySet()).forEach((member) -> member.setLoses(member.getLoses()+1));
        } else if (team2TotalGoals > team1TotalGoals) {
            this.victory = 1;
            new ArrayList<>(currentGameStatsTeam2.keySet()).forEach((member) -> member.setWins(member.getWins()+1));
            new ArrayList<>(currentGameStatsTeam1.keySet()).forEach((member) -> member.setLoses(member.getLoses()+1));
        } else {
            this.victory = 0;
            new ArrayList<>(currentGameStatsTeam1.keySet()).forEach((member) -> member.setDraws(member.getDraws()+1));
            new ArrayList<>(currentGameStatsTeam2.keySet()).forEach((member) -> member.setDraws(member.getDraws()+1));
        }

    }

    /**
     * Undoes the updates over the selected members that were done by updateMembersWithNewStats()
     * and clears this.allMembersWithUpdatedStats and this.allTemporaryMembers.
     */
    private void undoCollectGameStats() {
        currentGameStatsTeam1 =
                ((FBMembersGameStatsSelectorRVAdapter) step2Team1RV.getAdapter()).getCurrentGameStats();
        for (FootballMember member : currentGameStatsTeam1.keySet()) {
            FootballMember tempMember = currentGameStatsTeam1.get(member);

            member.setGoals(member.getGoals() - tempMember.getGoals());
            member.setAssists(member.getAssists() - tempMember.getAssists());
            member.setSaves(member.getSaves() - tempMember.getSaves());
            member.setFouls(member.getFouls() - tempMember.getFouls());
        }

        currentGameStatsTeam2 =
                ((FBMembersGameStatsSelectorRVAdapter) step2Team2RV.getAdapter()).getCurrentGameStats();
        for (FootballMember member : currentGameStatsTeam2.keySet()) {
            FootballMember tempMember = currentGameStatsTeam2.get(member);

            member.setGoals(member.getGoals() - tempMember.getGoals());
            member.setAssists(member.getAssists() - tempMember.getAssists());
            member.setSaves(member.getSaves() - tempMember.getSaves());
            member.setFouls(member.getFouls() - tempMember.getFouls());
        }

        if (this.victory == -1) {
            new ArrayList<>(currentGameStatsTeam1.keySet()).forEach((member) -> member.setWins(member.getWins()-1));
            new ArrayList<>(currentGameStatsTeam2.keySet()).forEach((member) -> member.setLoses(member.getLoses()-1));
        } else if (this.victory == 1) {
            new ArrayList<>(currentGameStatsTeam2.keySet()).forEach((member) -> member.setWins(member.getWins()-1));
            new ArrayList<>(currentGameStatsTeam1.keySet()).forEach((member) -> member.setLoses(member.getLoses()-1));
        } else if (this.victory == 0) {
            new ArrayList<>(currentGameStatsTeam1.keySet()).forEach((member) -> member.setDraws(member.getDraws()-1));
            new ArrayList<>(currentGameStatsTeam2.keySet()).forEach((member) -> member.setDraws(member.getDraws()-1));
        }

        this.allMembersWithUpdatedStats.clear();
        this.allTemporaryMembers.clear();
    }

    /**
     * This method implements the functionality of "BACK" button.
     */
    private void backBtnPressed() {
        viewFlipper.showPrevious();
        if (!nextBTN.isEnabled()) {
            nextBTN.setEnabled(true);
        }

        if (viewFlipper.getDisplayedChild() == 0) { // Back to step 1
            backBTN.setEnabled(false);
            nextBTN.setText("Confirm Teams");
        }

        if (viewFlipper.getDisplayedChild() == 1) { // Back to step 2
            nextBTN.setText("Confirm Stats");
        }
    }

    /** ==================== END BTN IMPLEMENTATION ========================================== */

    /** ================= START LISTENER'S IMPLEMENTATION =================================== */
    @Override
    public void draggedMember(Member<?> member) {
        this.draggedMember = (FootballMember) member;
    }

    /** ================= END LISTENER'S IMPLEMENTATION =================================== */
}