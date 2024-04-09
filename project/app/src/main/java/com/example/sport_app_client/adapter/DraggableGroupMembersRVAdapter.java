package com.example.sport_app_client.adapter;

import android.content.ClipData;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sport_app_client.R;
import com.example.sport_app_client.interfaces.OnGameMemberDragListener;
import com.example.sport_app_client.model.member.Member;

import java.util.List;

public class DraggableGroupMembersRVAdapter extends RecyclerView.Adapter<DraggableGroupMembersRVAdapter.ViewHolder> {

    private List<? extends Member<?,?>> members;
    private OnGameMemberDragListener listener;

    public DraggableGroupMembersRVAdapter(List<? extends Member<?,?>> members, OnGameMemberDragListener listener) {
        this.members = members;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View spotView = inflater.inflate(R.layout.game_member_item_rv, parent, false);

        DraggableGroupMembersRVAdapter.ViewHolder viewHolder = new DraggableGroupMembersRVAdapter.ViewHolder(spotView);
        return viewHolder;
    }

    // set values for each holder given its position
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.memberNameTV.setText(members.get(position).getNickname().toString());
        Member<?,?> draggedMember = members.get(position);
        holder.itemView.setOnLongClickListener(v -> {
            ClipData dragData = ClipData.newPlainText("", "");
            View.DragShadowBuilder myShadow = new View.DragShadowBuilder(holder.itemView);
            v.startDragAndDrop(dragData, myShadow, null, 0);
            listener.draggedMember(draggedMember);
            return true;
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
            removeMemberBTN.setVisibility(View.GONE); // I use the same item layout but don't use the btn here

        }
    }
}
