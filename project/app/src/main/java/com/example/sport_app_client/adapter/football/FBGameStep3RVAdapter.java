package com.example.sport_app_client.adapter.football;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sport_app_client.R;
import com.example.sport_app_client.model.member.FootballMember;

import java.util.List;

public class FBGameStep3RVAdapter extends RecyclerView.Adapter<FBGameStep3RVAdapter.ViewHolder> {

    private List<FootballMember> members;

    public FBGameStep3RVAdapter(List<FootballMember> members) {
        this.members = members;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View spotView = inflater.inflate(R.layout.fb_member_stats_view_rv_item, parent, false);

        FBGameStep3RVAdapter.ViewHolder viewHolder = new FBGameStep3RVAdapter.ViewHolder(spotView);
        return viewHolder;
    }

    // set values for each holder given its position
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.memberNameTV.setText(members.get(position).getNickname().toString());
        holder.memberGoalsTV.setText(Integer.toString(members.get(position).getGoals()));
        holder.memberAssistsTV.setText(Integer.toString(members.get(position).getAssists()));
        holder.memberSavesTV.setText(Integer.toString(members.get(position).getSaves()));
        holder.memberFoulsTV.setText(Integer.toString(members.get(position).getFouls()));
    }

    @Override
    public int getItemCount() {
        return this.members.size();
    }

    public List<FootballMember> getMembers() {
        return this.members;
    }

    public void setMembers(List<FootballMember> members) {
        this.members = members;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView memberNameTV;
        private final TextView memberGoalsTV;
        private final TextView memberAssistsTV;
        private final TextView memberSavesTV;
        private final TextView memberFoulsTV;


        public ViewHolder(View view) {
            super(view);

            this.memberNameTV = view.findViewById(R.id.footballMemberStatsItemNameTV);
            this.memberGoalsTV = view.findViewById(R.id.footballMemberStatsItemGoalTV);
            this.memberAssistsTV = view.findViewById(R.id.footballMemberStatsItemAssistsTV);
            this.memberSavesTV = view.findViewById(R.id.footballMemberStatsItemSavesTV);
            this.memberFoulsTV = view.findViewById(R.id.footballMemberStatsItemFoulsTV);
        }
    }
}
