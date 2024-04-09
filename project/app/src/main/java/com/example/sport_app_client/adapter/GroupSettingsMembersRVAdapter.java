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
import com.example.sport_app_client.interfaces.GroupMemberDeletedListener;
import com.example.sport_app_client.model.member.Member;

import java.util.List;

public class GroupSettingsMembersRVAdapter extends RecyclerView.Adapter<GroupSettingsMembersRVAdapter.ViewHolder> {

    private List<? extends Member<?,?>> members;
    private GroupMemberDeletedListener listener;
    private Member<?,?> userAssociatedMember;

    public GroupSettingsMembersRVAdapter(List<? extends Member<?,?>> members,
                                         GroupMemberDeletedListener listener, Member<?,?> userAssociatedMember) {
        this.members = members;
        this.listener = listener;
        this.userAssociatedMember = userAssociatedMember;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View spotView = inflater.inflate(R.layout.group_settings_rv_item, parent, false);

        GroupSettingsMembersRVAdapter.ViewHolder viewHolder = new GroupSettingsMembersRVAdapter.ViewHolder(spotView);
        return viewHolder;
    }

    // set values for each holder given its position
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.memberNameTV.setText(members.get(position).getNickname().toString());
        if (members.get(position).getUser() != null) {
            holder.memberUserNameTV.setText(members.get(position).getUser().getUserName().toString());
        } else {
            holder.memberUserNameTV.setText("None");
        }
        Member<?,?> member = members.get(position);
        if (member.getId() != userAssociatedMember.getId()) { // add func if the user is admin
            holder.btn.setOnClickListener(view -> {
                listener.deleteMember(member);
            });
        } else {
            holder.btn.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return members.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView memberNameTV;
        private final TextView memberUserNameTV;
        private final Button btn;

        public ViewHolder(View view) {
            super(view);

            this.memberNameTV = view.findViewById(R.id.groupSettingsRVItemName);
            this.memberUserNameTV = view.findViewById(R.id.groupSettingsRVItemUser);
            this.btn = view.findViewById(R.id.groupSettingsRVItemBTN);
            if (!userAssociatedMember.getIsAdmin()) {
                btn.setVisibility(View.GONE);
            }
        }
    }
}