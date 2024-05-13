package com.example.sport_app_client.gameActivities;

import android.app.Activity;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.ViewFlipper;

import com.example.sport_app_client.R;
import com.example.sport_app_client.adapter.GameTeamsRVAdapter;
import com.example.sport_app_client.adapter.DraggableGroupMembersRVAdapter;
import com.example.sport_app_client.helpers.MyGlobals;
import com.example.sport_app_client.interfaces.OnGameMemberDragListener;
import com.example.sport_app_client.interfaces.OpenMemberStatSelectionDialog;
import com.example.sport_app_client.model.member.Member;
import com.example.sport_app_client.model.stats.Stats;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class GameFragment extends Fragment implements OnGameMemberDragListener, OpenMemberStatSelectionDialog {

    protected Activity activity;
    protected View view;

    public GameFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.game_fragment_layout, container, false);

        initVars();
        initViews();

        return view;
    }

    /** Views */
    protected ProgressBar progressBar;
    protected LinearProgressIndicator horizontalPB;
    protected ViewFlipper viewFlipper;
    protected Button backBTN;
    protected Button nextBTN;
    protected RecyclerView membersRV;
    protected RecyclerView randomMembersRV;
    protected RadioButton step1ManualSelectionRB; // I am using only this btn to get info
    protected View step1RandomMembersLayout;
    protected Button step1RandomBTN;
    protected RecyclerView step1Team1RV;
    protected RecyclerView step1Team2RV;
    protected RecyclerView step2Team1RV;
    protected RecyclerView step2Team2RV;
    protected RecyclerView step3Team1RV;
    protected RecyclerView step3Team2RV;

    /* Dialog */
    protected AlertDialog.Builder dialogBuilder;
    protected AlertDialog dialog;

    /* Vars */
    protected Member draggedMember;
    protected List<Member> team1;
    protected List<Member> team2;
    protected List<Member> step1RandomMembers;


    /* ==================== START CODE INITIALIZATION ======================================= */

    private void initVars() {
        this.team1 = new ArrayList<>();
        this.team2 = new ArrayList<>();
        this.step1RandomMembers = new ArrayList<>();
        initSportDependentVars();
    }

    protected abstract void initSportDependentVars();

    private void initViews() {
        this.viewFlipper = view.findViewById(R.id.gameFragmentVF);

        this.progressBar = view.findViewById(R.id.gameFragmentProgressBar);
        this.horizontalPB = view.findViewById(R.id.gameFragmentPBHorizontal);
        horizontalPB.setMin(0);
        horizontalPB.setMax(100);
        horizontalPB.setProgress(33);

        this.backBTN = view.findViewById(R.id.gameFragmentBackBTN);
        backBTN.setOnClickListener((view -> {
            backBtnPressed();
        }));
        backBTN.setEnabled(false);

        this.nextBTN = view.findViewById(R.id.gameFragmentNextBTN);
        this.nextBTN.setText("Confirm Teams");
        nextBTN.setOnClickListener((view -> {
            nextBtnPressed();
        }));

        this.step1RandomMembersLayout = view.findViewById(R.id.gameFragmentStep1RandomLayout);
        step1RandomMembersLayout.setVisibility(View.GONE);

        // I am using only this btn to get info
        this.step1ManualSelectionRB = view.findViewById(R.id.gameFragmentStep1RB1);
        step1ManualSelectionRB.setChecked(true);
        step1ManualSelectionRB.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                step1RandomMembersLayout.setVisibility(View.GONE);
            } else {
                step1RandomMembersLayout.setVisibility(View.VISIBLE);
            }
        });

        this.step1RandomBTN = view.findViewById(R.id.gameFragmentStep1RandomBTN);
        step1RandomBTN.setOnClickListener(view -> generateRandomTeams());

        initRecyclerViews();

        setSportSpecificDesign();
    }

    private void initRecyclerViews() {
        // Step 1
        this.membersRV = view.findViewById(R.id.gameFragmentStep1MembersRV);
        DraggableGroupMembersRVAdapter membersAdapter = new DraggableGroupMembersRVAdapter(MyGlobals.getGroup().getMembersAbs(), this);
        membersRV.setAdapter(membersAdapter);
        membersRV.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));

        this.step1Team1RV = view.findViewById(R.id.gameFragmentStep1Team1RV);
        GameTeamsRVAdapter step1Team1Adapter = new GameTeamsRVAdapter(team1, false);
        step1Team1RV.setAdapter(step1Team1Adapter);
        step1Team1RV.setLayoutManager(new LinearLayoutManager(activity));
        step1Team1RV.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View view, DragEvent dragEvent) {
                switch (dragEvent.getAction()) {
                    case DragEvent.ACTION_DROP:
                        if (!(team2.contains(draggedMember) || team1.contains(draggedMember) || !(step1ManualSelectionRB.isChecked()))) {
                            team1.add(draggedMember);
                            step1Team1Adapter.notifyItemInserted(team1.size()-1);
                        }
                        break;
                }
                return true;
            }
        });

        this.step1Team2RV = view.findViewById(R.id.gameFragmentStep1Team2RV);
        GameTeamsRVAdapter step1Team2Adapter = new GameTeamsRVAdapter(team2, false);
        step1Team2RV.setAdapter(step1Team2Adapter);
        step1Team2RV.setLayoutManager(new LinearLayoutManager(activity));
        step1Team2RV.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View view, DragEvent dragEvent) {
                switch (dragEvent.getAction()) {
                    case DragEvent.ACTION_DROP:
                        if (!(team2.contains(draggedMember) || team1.contains(draggedMember) || !(step1ManualSelectionRB.isChecked()))) {
                            team2.add(draggedMember);
                            step1Team2Adapter.notifyItemInserted(team2.size()-1);
                        }
                        break;
                }
                return true;
            }
        });

        this.randomMembersRV = view.findViewById(R.id.gameFragmentStep1RandomMembersRV);
        GameTeamsRVAdapter randomMembersAdapter = new GameTeamsRVAdapter(step1RandomMembers, true);
        randomMembersRV.setAdapter(randomMembersAdapter);
        randomMembersRV.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
        randomMembersRV.setOnDragListener((view, dragEvent) -> {
            switch (dragEvent.getAction()) {
                case DragEvent.ACTION_DROP:
                    if (!(step1RandomMembers.contains(draggedMember))) {
                        step1RandomMembers.add(draggedMember);
                        randomMembersAdapter.notifyItemInserted(step1RandomMembers.size()-1);
                    }
                    break;
            }
            return true;
        });

        initSportDependentRecyclerViews();
    }

    protected abstract void initSportDependentRecyclerViews();

    /**
     * This method should set the sport specific design of the views in the fragment
     * such as background color. The position of the views are fixed.
     */
    protected abstract void setSportSpecificDesign();

    /* ==================== END CODE INITIALIZATION ========================================= */

    /* ==================== START BTN IMPLEMENTATION ========================================== */

    /**
     * This method should spread randomly the members from this.step1RandomMembers to
     * this.team1 and this.team2.
     */
    protected void generateRandomTeams() {
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
            horizontalPB.setProgress(66, true);
        }
        else if (viewFlipper.getDisplayedChild() == 2) { // After confirm stats pressed
            nextBTN.setText("Confirm Game");
            horizontalPB.setProgress(100, true);
        }
    }

    /**
     * This method should check if the selected teams in Step 1 are valid.
     * @return - are the teams selected in Step 1 valid
     */
    protected abstract boolean goFromStep1ToStep2();

    /** This method should update the recyclers in step 3 with the data from step 2. */
    protected abstract void goFromStep2ToStep3();

    /**
     * This method should take the current game stats and send request to update them
     * using API.addNewGame();. Should call GlobalMethods.showPGAndBlockUI(); and
     * GlobalMethods.hidePGAndEnableUi(progressBar, activity); before the request is sent and
     * after response is received respectively.
     */
    protected abstract void finalizeStep3();

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
            horizontalPB.setProgress(33, true);
        }

        if (viewFlipper.getDisplayedChild() == 1) { // Back to step 2
            nextBTN.setText("Confirm Stats");
            horizontalPB.setProgress(66, true);
        }
    }

    /* ==================== END BTN IMPLEMENTATION ========================================== */

    /* ================= START LISTENER'S IMPLEMENTATION =================================== */
    @Override
    public void draggedMember(Member member) {
        this.draggedMember = member;
    }

    @Override
    public void openDialog(Stats stats) {
        // Build dialog
        dialogBuilder = new AlertDialog.Builder(activity);

        View popupView = initSportDependentDialog(stats);

        // Show dialog
        dialogBuilder.setView(popupView);
        dialog = dialogBuilder.create();
        dialog.show();
    }

    /**
     * This method should initialize the view for the dialog responsible for visualizing member
     * stats.
     * @param stats - the stats to be selected
     * @return - a view inflated with the correct layout for the dialog
     */
    protected abstract View initSportDependentDialog(Stats stats);

    /* ================= END LISTENER'S IMPLEMENTATION =================================== */
}