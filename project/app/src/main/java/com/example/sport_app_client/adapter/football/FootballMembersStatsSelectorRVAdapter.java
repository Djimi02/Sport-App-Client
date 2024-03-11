package com.example.sport_app_client.adapter.football;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sport_app_client.R;
import com.example.sport_app_client.model.member.FootballMember;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FootballMembersStatsSelectorRVAdapter extends RecyclerView.Adapter<FootballMembersStatsSelectorRVAdapter.ViewHolder> {

    private List<FootballMember> members;
    private HashMap<FootballMember, List<EditText>> map;

    public FootballMembersStatsSelectorRVAdapter(List<FootballMember> members) {
        this.members = members;
        this.map = new HashMap<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View spotView = inflater.inflate(R.layout.football_game_member_item_rv, parent, false);

        FootballMembersStatsSelectorRVAdapter.ViewHolder viewHolder = new FootballMembersStatsSelectorRVAdapter.ViewHolder(spotView);
        return viewHolder;
    }

    // set values for each holder given its position
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        map.put(members.get(position), holder.ets);

        holder.memberNameTV.setText(members.get(position).getNickname()+"");

        holder.memberGoalsBTN1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int value = 0;
                try {
                    value = Integer.parseInt(holder.memberGoalsET.getText().toString());
                } catch (Exception e) {
                    value = 0;
                }
                if (value > 0) {
                    value--;
                    holder.memberGoalsET.setText(Integer.toString(value));
                }
            }
        });
        holder.memberGoalsET.setText(Integer.toString(0));
        holder.memberGoalsBTN2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int value = 0;
                try {
                    value = Integer.parseInt(holder.memberGoalsET.getText().toString());
                } catch (Exception e) {
                    value = 0;
                }
                if (value <= 99) {
                    value++;
                    holder.memberGoalsET.setText(Integer.toString(value));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.members.size();
    }

    /**
    The method returns the stats that are currently viewable in step 2 layout.
     */
    public List<FootballMember> getCurrentGameStats() {
        List<FootballMember> tempMembers = new ArrayList<>();
        for (int i = 0; i < members.size(); i++) {
            FootballMember member = members.get(i);
            FootballMember tempMember = new FootballMember();
            tempMember.setNickname(member.getNickname());
            tempMember.setGoals(Integer.parseInt(map.get(member).get(0).getText().toString()));
            tempMember.setAssists(Integer.parseInt(map.get(member).get(1).getText().toString()));
            tempMember.setSaves(Integer.parseInt(map.get(member).get(2).getText().toString()));
            tempMember.setFouls(Integer.parseInt(map.get(member).get(3).getText().toString()));
            tempMembers.add(tempMember);
        }
        return tempMembers;
    }

    /** The method updates the stats of the members in this.members, by summing
    * their values with the currently viewable stats in step 2 layout. */
    public void updateMembersWithNewStats() {
        for (FootballMember member : map.keySet()) {
            member.setGoals(member.getGoals() + Integer.parseInt(map.get(member).get(0).getText().toString()));
            member.setAssists(member.getAssists() + Integer.parseInt(map.get(member).get(1).getText().toString()));
            member.setSaves(member.getSaves() + Integer.parseInt(map.get(member).get(2).getText().toString()));
            member.setFouls(member.getFouls() + Integer.parseInt(map.get(member).get(3).getText().toString()));
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView memberNameTV;
        private final Button memberGoalsBTN1;
        private final EditText memberGoalsET;
        private final Button memberGoalsBTN2;
        private final Button memberAssistsBTN1;
        private final EditText memberAssistsET;
        private final Button memberAssistsBTN2;
        private final Button memberSavesBTN1;
        private final EditText memberSavesET;
        private final Button memberSavesBTN2;
        private final Button memberFoulsBTN1;
        private final EditText memberFoulsET;
        private final Button memberFoulsBTN2;

        private List<EditText> ets;

        public ViewHolder(View view) {
            super(view);

            this.memberNameTV = view.findViewById(R.id.footballGameMemberItemNameTV);

            this.memberGoalsBTN1 = view.findViewById(R.id.footballGameMemberItemGoalsBTN1);
            this.memberGoalsET = view.findViewById(R.id.footballGameMemberItemGoalsET);
            this.memberGoalsBTN2 = view.findViewById(R.id.footballGameMemberItemGoalsBTN2);

            this.memberAssistsBTN1 = view.findViewById(R.id.footballGameMemberItemAssistsBTN1);
            this.memberAssistsET = view.findViewById(R.id.footballGameMemberItemAssistsET);
            this.memberAssistsBTN2 = view.findViewById(R.id.footballGameMemberItemAssistsBTN2);

            this.memberSavesBTN1 = view.findViewById(R.id.footballGameMemberItemSavesBTN1);
            this.memberSavesET = view.findViewById(R.id.footballGameMemberItemSavesET);
            this.memberSavesBTN2 = view.findViewById(R.id.footballGameMemberItemSavesBTN2);

            this.memberFoulsBTN1 = view.findViewById(R.id.footballGameMemberItemFoulsBTN1);
            this.memberFoulsET = view.findViewById(R.id.footballGameMemberItemFoulsET);
            this.memberFoulsBTN2 = view.findViewById(R.id.footballGameMemberItemFoulsBTN2);

            this.ets = new ArrayList<>();
            ets.add(memberGoalsET);
            ets.add(memberAssistsET);
            ets.add(memberSavesET);
            ets.add(memberFoulsET);
        }
    }
}