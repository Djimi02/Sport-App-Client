package com.example.sport_app_client.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sport_app_client.R;
import com.example.sport_app_client.interfaces.UserGroupClickListener;
import com.example.sport_app_client.model.member.Member;

import java.util.List;

public class UserGroupsRVAdapter extends RecyclerView.Adapter<UserGroupsRVAdapter.ViewHolder> {

    private List<Member> members;
    private UserGroupClickListener listener;


    public UserGroupsRVAdapter(List<Member> members, UserGroupClickListener listener) {
        this.members = members;
        this.listener = listener;
    }

    @NonNull
    @Override
    public UserGroupsRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View spotView = inflater.inflate(R.layout.user_group_item_rv, parent, false);

        UserGroupsRVAdapter.ViewHolder viewHolder = new UserGroupsRVAdapter.ViewHolder(spotView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull UserGroupsRVAdapter.ViewHolder holder, int position) {
        // set values for each holder given its position
        holder.groupName.setText("Group: " + members.get(position).getGroup().getName());
        holder.totalGames.setText("" + members.get(position).getTotalGames());
        holder.totalWins.setText("" + members.get(position).getTotalWins());
    }

    @Override
    public int getItemCount() {
        return this.members.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView groupName;
        private final TextView totalGames;
        private final TextView totalWins;
        private final Button viewBTN;

        public ViewHolder(View view) {
            super(view);

            this.groupName = view.findViewById(R.id.groupNameRVItem);
            this.totalGames = view.findViewById(R.id.totalGamesRVItem);
            this.totalWins = view.findViewById(R.id.totalWinsRVItem);
            this.viewBTN = view.findViewById(R.id.viewBTNRVItem);

            viewBTN.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onClick((long)getAdapterPosition());
                }
            });
        }
    }
}
