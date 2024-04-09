package com.example.sport_app_client.adapter.football;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sport_app_client.R;
import com.example.sport_app_client.interfaces.OpenFBMemberStatDialog;
import com.example.sport_app_client.model.member.FootballMember;
import com.example.sport_app_client.model.member.Member;
import com.example.sport_app_client.model.stats.FBStats;

import java.util.HashMap;
import java.util.List;

public class FBGameStep2RVAdapter extends RecyclerView.Adapter<FBGameStep2RVAdapter.ViewHolder> {

    private List<FootballMember> members;
    private HashMap<FootballMember, FBStats> membersWithStatsMap;
    private OpenFBMemberStatDialog openFBMemberStatDialog;

    public FBGameStep2RVAdapter(List<FootballMember> members, OpenFBMemberStatDialog openFBMemberStatDialog) {
        this.members = members;
        this.openFBMemberStatDialog = openFBMemberStatDialog;
        this.membersWithStatsMap = new HashMap<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View spotView = inflater.inflate(R.layout.game_member_item_rv, parent, false);

        return new FBGameStep2RVAdapter.ViewHolder(spotView);
    }

    // set values for each holder given its position
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FootballMember member = members.get(position);

        if (membersWithStatsMap.get(member) == null) {
            FBStats tempMemberStats = new FBStats();
            // Setting temporary member so that we know whose stats are these when displaying
            tempMemberStats.setMember(new FootballMember(member.getNickname(), null));
            membersWithStatsMap.put(members.get(position), tempMemberStats);
        }

        holder.memberNameTV.setText(member.getNickname().toString());
        holder.btn.setOnClickListener(view -> {
            openFBMemberStatDialog.openDialog(member.getNickname(), membersWithStatsMap.get(member));
        });
    }

    @Override
    public int getItemCount() {
        return this.members.size();
    }

    /**
    The method returns the stats that are currently viewable in step 2 layout,
     * where the key is a member of the group and the value is
     * their stat in the game
     */
    public HashMap<FootballMember, FBStats> getCurrentGameStats() {
        HashMap<FootballMember, FBStats> output = new HashMap<>();

        // Return only the relevant ones
        for (int i = 0; i < members.size(); i++) {
            output.put(members.get(i), membersWithStatsMap.get(members.get(i)));
        }

        return output;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView memberNameTV;
        private final Button btn;

        public ViewHolder(View view) {
            super(view);

            this.memberNameTV = view.findViewById(R.id.gameMemberNameTV);
            this.btn = view.findViewById(R.id.memberItemRemoveMemberBTN);
            this.btn.setText("Set Stats");
        }
    }
}