package com.example.sport_app_client.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sport_app_client.R;
import com.example.sport_app_client.model.member.FootballMember;

import java.util.List;

public class FootballGameMembersRVAdapter extends RecyclerView.Adapter<FootballGameMembersRVAdapter.ViewHolder> {

    private List<FootballMember> members;

    public FootballGameMembersRVAdapter(List<FootballMember> members) {
        this.members = members;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View spotView = inflater.inflate(R.layout.football_game_member_item_rv, parent, false);

        FootballGameMembersRVAdapter.ViewHolder viewHolder = new FootballGameMembersRVAdapter.ViewHolder(spotView);
        return viewHolder;
    }

    // set values for each holder given its position
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.memberNameTV.setText(members.get(position).getNickname()+"");
        holder.memberGoalsTV.setText(Integer.toString(members.get(position).getGoals()));
        holder.memberAssistsTV.setText(Integer.toString(members.get(position).getAssists()));
        holder.memberSavesTV.setText(Integer.toString(members.get(position).getSaves()));
        holder.memberFoultsTV.setText(Integer.toString(members.get(position).getFouls()));
    }

    @Override
    public int getItemCount() {
        return this.members.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView memberNameTV;
        private final TextView memberGoalsTV;
        private final TextView memberAssistsTV;
        private final TextView memberSavesTV;
        private final TextView memberFoultsTV;

        public ViewHolder(View view) {
            super(view);

            this.memberNameTV = view.findViewById(R.id.footballGameMemberItemNameTV);
            this.memberGoalsTV = view.findViewById(R.id.footballGameMemberItemGoalsTV);
            this.memberAssistsTV = view.findViewById(R.id.footballGameMemberItemAssistsTV);
            this.memberSavesTV = view.findViewById(R.id.footballGameMemberItemSavesTV);
            this.memberFoultsTV = view.findViewById(R.id.footballGameMemberItemFoulsTV);
        }
    }
}