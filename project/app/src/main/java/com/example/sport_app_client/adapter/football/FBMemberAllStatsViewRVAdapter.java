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

public class FBMemberAllStatsViewRVAdapter extends RecyclerView.Adapter<FBMemberAllStatsViewRVAdapter.ViewHolder> {

    private List<FootballMember> members;

    public FBMemberAllStatsViewRVAdapter(List<FootballMember> members) {
        this.members = members;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View spotView = inflater.inflate(R.layout.fb_member_all_stats_rv_item, parent, false);

        return new FBMemberAllStatsViewRVAdapter.ViewHolder(spotView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.memberNameTV.setText(members.get(position).getNickname().toString());
        holder.memberWinsTV.setText(members.get(position).getStats().getWins().toString());
        holder.memberDrawsTV.setText(members.get(position).getStats().getDraws().toString());
        holder.memberLosesTV.setText(members.get(position).getStats().getLoses().toString());
        holder.memberGoalsTV.setText(Integer.toString(members.get(position).getStats().getGoals()));
        holder.memberAssistsTV.setText(Integer.toString(members.get(position).getStats().getAssists()));
        holder.memberSavesTV.setText(Integer.toString(members.get(position).getStats().getSaves()));
        holder.memberFoulsTV.setText(Integer.toString(members.get(position).getStats().getFouls()));
    }

    @Override
    public int getItemCount() {
        return this.members.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView memberNameTV;
        private final TextView memberWinsTV;
        private final TextView memberDrawsTV;
        private final TextView memberLosesTV;
        private final TextView memberGoalsTV;
        private final TextView memberAssistsTV;
        private final TextView memberSavesTV;
        private final TextView memberFoulsTV;


        public ViewHolder(View view) {
            super(view);

            this.memberNameTV = view.findViewById(R.id.fbMemberNameTV);
            this.memberWinsTV = view.findViewById(R.id.fbMemberWinsTV);
            this.memberDrawsTV = view.findViewById(R.id.fbMemberDrawsTV);
            this.memberLosesTV = view.findViewById(R.id.fbMemberLosesTV);
            this.memberGoalsTV = view.findViewById(R.id.fbMemberGoalsTV);
            this.memberAssistsTV = view.findViewById(R.id.fbMemberAssistsTV);
            this.memberSavesTV = view.findViewById(R.id.fbMemberSavesTV);
            this.memberFoulsTV = view.findViewById(R.id.fbMemberFoulsTV);
        }
    }
}
