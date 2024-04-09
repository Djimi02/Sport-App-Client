package com.example.sport_app_client.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sport_app_client.R;
import com.example.sport_app_client.model.member.Member;

import java.util.List;

public class GameTeamsRVAdapter extends RecyclerView.Adapter<GameTeamsRVAdapter.ViewHolder> {

    private List<? extends Member<?,?>> members;

    public GameTeamsRVAdapter(List<? extends Member<?,?>> members) {
        this.members = members;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View spotView = inflater.inflate(R.layout.game_member_item_rv, parent, false);

        return new GameTeamsRVAdapter.ViewHolder(spotView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.memberNameTV.setText(members.get(position).getNickname().toString());

        Member<?,?> memberToBeDeleted = members.get(position);
        holder.removeMemberBTN.setOnClickListener(v -> {
            int position1 = members.indexOf(memberToBeDeleted);
            members.remove(memberToBeDeleted);
            notifyItemRemoved(position1);
        });
    }

    @Override
    public int getItemCount() {
        return members.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView memberNameTV;
        private final Button removeMemberBTN;

        public ViewHolder(View view) {
            super(view);

            this.memberNameTV = view.findViewById(R.id.gameMemberNameTV);
            this.removeMemberBTN = view.findViewById(R.id.memberItemRemoveMemberBTN);

        }
    }
}
