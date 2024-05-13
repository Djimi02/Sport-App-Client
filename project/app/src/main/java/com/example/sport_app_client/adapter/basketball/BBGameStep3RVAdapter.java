package com.example.sport_app_client.adapter.basketball;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sport_app_client.R;
import com.example.sport_app_client.model.stats.BBStats;

import java.util.List;

public class BBGameStep3RVAdapter extends RecyclerView.Adapter<BBGameStep3RVAdapter.ViewHolder> {

    private List<BBStats> stats; // needed separate list as we need ordering for the rv

    public BBGameStep3RVAdapter(List<BBStats> stats) {
        this.stats = stats;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View spotView = inflater.inflate(R.layout.bb_member_stats_view_rv_item, parent, false);

        BBGameStep3RVAdapter.ViewHolder viewHolder = new BBGameStep3RVAdapter.ViewHolder(spotView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BBStats statsInRV = stats.get(position);
        holder.memberNameTV.setText(statsInRV.getMemberName().toString());
        holder.memberPointsTV.setText(Integer.toString(statsInRV.getPoints()));
        holder.memberNumberOfThreePointsTV.setText(Integer.toString(statsInRV.getNumberOfThreePoints()));
        holder.memberNumOfDunksTV.setText(Integer.toString(statsInRV.getNumOfDunks()));
        holder.memberBlocksTV.setText(Integer.toString(statsInRV.getBlocks()));
        holder.memberFoulsTV.setText(Integer.toString(statsInRV.getFouls()));
    }

    @Override
    public int getItemCount() {
        return this.stats.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView memberNameTV;
        private final TextView memberPointsTV;
        private final TextView memberNumberOfThreePointsTV;
        private final TextView memberNumOfDunksTV;
        private final TextView memberBlocksTV;
        private final TextView memberFoulsTV;

        public ViewHolder(View view) {
            super(view);

            this.memberNameTV = view.findViewById(R.id.bbMemberStatsItemNameTV);
            this.memberPointsTV = view.findViewById(R.id.bbMemberStatsItemPointsTV);
            this.memberNumberOfThreePointsTV = view.findViewById(R.id.bbMemberStatsItemNumOfThreePointsTV);
            this.memberNumOfDunksTV = view.findViewById(R.id.bbMemberStatsItemDunksTV);
            this.memberBlocksTV = view.findViewById(R.id.bbMemberStatsItemBlocksTV);
            this.memberFoulsTV = view.findViewById(R.id.bbMemberStatsItemFoulsTV);
        }
    }
}