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
import com.example.sport_app_client.interfaces.UserGroupClickListener;
import com.example.sport_app_client.model.member.Member;

import java.util.List;

public class UserGroupsRVAdapter extends RecyclerView.Adapter<UserGroupsRVAdapter.ViewHolder> {

    private List<Member<?>> members;
    private UserGroupClickListener listener;


    public UserGroupsRVAdapter(List<Member<?>> members, UserGroupClickListener listener) {
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
        holder.totalWins.setText("Wins: " + members.get(position).getWins());
        holder.totalDraws.setText("Draws: " + members.get(position).getDraws());
        holder.totalLoses.setText("Loses: " + members.get(position).getLoses());
    }

    @Override
    public int getItemCount() {
        return this.members.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView groupName;
        private final TextView totalWins;
        private final TextView totalDraws;
        private final TextView totalLoses;


        private final Button viewBTN;

        public ViewHolder(View view) {
            super(view);

            this.groupName = view.findViewById(R.id.groupNameRVItem);
            this.totalWins = view.findViewById(R.id.totalWinsRVItem);
            this.totalDraws = view.findViewById(R.id.totalDrawsRVItem);
            this.totalLoses = view.findViewById(R.id.totalLosesRVItem);
            this.viewBTN = view.findViewById(R.id.viewBTNRVItem);

            viewBTN.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    listener.openGroupInActivity(members.get(position).getGroup().getId(), members.get(position).getSport());
                }
            });
        }
    }
}
