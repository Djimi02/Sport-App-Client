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
import com.example.sport_app_client.interfaces.SelectMemberToJoinGroupListener;
import com.example.sport_app_client.model.member.Member;

import java.util.List;

public class SelectMemberToJoinGroupRVAdapter extends RecyclerView.Adapter<SelectMemberToJoinGroupRVAdapter.ViewHolder> {

    private List<? extends Member<?>> members;
    private SelectMemberToJoinGroupListener listener;

    public SelectMemberToJoinGroupRVAdapter(List<? extends Member<?>> members, SelectMemberToJoinGroupListener listener) {
        this.members = members;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SelectMemberToJoinGroupRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View spotView = inflater.inflate(R.layout.select_member_item_rv, parent, false);

        return new SelectMemberToJoinGroupRVAdapter.ViewHolder(spotView);
    }

    @Override
    public void onBindViewHolder(@NonNull SelectMemberToJoinGroupRVAdapter.ViewHolder holder, int position) {
        Member<?> member = members.get(position);
        holder.memberNameTV.setText(member.getNickname().toString());
        holder.btn.setOnClickListener(v -> listener.onMemberSelected(member));
    }

    @Override
    public int getItemCount() {
        return members.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView memberNameTV;
        private final Button btn;

        public ViewHolder(View view) {
            super(view);

            this.memberNameTV = view.findViewById(R.id.selectMemberItemTV);
            this.btn = view.findViewById(R.id.selectMemberItemBTN);
        }
    }
}