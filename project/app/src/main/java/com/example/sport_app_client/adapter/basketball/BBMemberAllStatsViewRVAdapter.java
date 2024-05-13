package com.example.sport_app_client.adapter.basketball;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sport_app_client.R;
import com.example.sport_app_client.model.member.BasketballMember;

import java.util.List;

public class BBMemberAllStatsViewRVAdapter extends RecyclerView.Adapter<BBMemberAllStatsViewRVAdapter.ViewHolder> {

    private List<BasketballMember> members;

    public BBMemberAllStatsViewRVAdapter(List<BasketballMember> members) {
        this.members = members;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View spotView = inflater.inflate(R.layout.bb_member_all_stats_rv_item, parent, false);

        return new BBMemberAllStatsViewRVAdapter.ViewHolder(spotView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.memberNameTV.setText(members.get(position).getNickname().toString());
        holder.memberWinsTV.setText(members.get(position).getStats().getWins().toString());
        holder.memberDrawsTV.setText(members.get(position).getStats().getDraws().toString());
        holder.memberLosesTV.setText(members.get(position).getStats().getLoses().toString());
        holder.memberPointsTV.setText(Integer.toString(members.get(position).getStats().getPoints()));
        holder.memberNumberOfThreePointsTV.setText(Integer.toString(members.get(position).getStats().getNumberOfThreePoints()));
        holder.memberNumOfDunksTV.setText(Integer.toString(members.get(position).getStats().getNumOfDunks()));
        holder.memberBlocksTV.setText(Integer.toString(members.get(position).getStats().getBlocks()));
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
        private final TextView memberPointsTV;
        private final TextView memberNumberOfThreePointsTV;
        private final TextView memberNumOfDunksTV;
        private final TextView memberBlocksTV;
        private final TextView memberFoulsTV;


        public ViewHolder(View view) {
            super(view);

            this.memberNameTV = view.findViewById(R.id.bbMemberNameTV);
            this.memberWinsTV = view.findViewById(R.id.bbMemberWinsTV);
            this.memberDrawsTV = view.findViewById(R.id.bbMemberDrawsTV);
            this.memberLosesTV = view.findViewById(R.id.bbMemberLosesTV);
            this.memberPointsTV = view.findViewById(R.id.bbMemberPointsTV);
            this.memberNumberOfThreePointsTV = view.findViewById(R.id.bbMemberNumberThreePointsTV);
            this.memberNumOfDunksTV = view.findViewById(R.id.bbMemberDunksTV);
            this.memberBlocksTV = view.findViewById(R.id.bbMemberBlocksTV);
            this.memberFoulsTV = view.findViewById(R.id.bbMemberFoulsTV);
        }
    }
}
