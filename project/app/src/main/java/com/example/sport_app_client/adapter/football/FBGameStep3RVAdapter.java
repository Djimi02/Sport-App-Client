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
import com.example.sport_app_client.model.stats.FBStats;

import java.util.HashMap;
import java.util.List;

public class FBGameStep3RVAdapter extends RecyclerView.Adapter<FBGameStep3RVAdapter.ViewHolder> {

    private List<FBStats> stats; // needed separate list as we need ordering for the rv

    public FBGameStep3RVAdapter(List<FBStats> stats) {
        this.stats = stats;
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
        FBStats statsInRV = stats.get(position);
        holder.memberNameTV.setText(statsInRV.getMemberName().toString());
        holder.memberGoalsTV.setText(Integer.toString(statsInRV.getGoals()));
        holder.memberAssistsTV.setText(Integer.toString(statsInRV.getAssists()));
        holder.memberSavesTV.setText(Integer.toString(statsInRV.getSaves()));
        holder.memberFoulsTV.setText(Integer.toString(statsInRV.getFouls()));
    }

    @Override
    public int getItemCount() {
        return this.stats.size();
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
