package com.example.sport_app_client.adapter.basketball;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sport_app_client.R;
import com.example.sport_app_client.interfaces.ViewAllStatsListener;
import com.example.sport_app_client.model.member.BasketballMember;

import java.util.List;
import java.util.function.Function;

public class BBMemberSingleStatRVAdapter extends RecyclerView.Adapter<BBMemberSingleStatRVAdapter.ViewHolder> {

    private List<BasketballMember> members;

    private ViewAllStatsListener listener;

    private Function<BasketballMember, Integer> getter;

    public BBMemberSingleStatRVAdapter(List<BasketballMember> members,
                                       ViewAllStatsListener listener,
                                       Function<BasketballMember, Integer> getter) {
        this.members = members;
        this.listener = listener;
        this.getter = getter;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View spotView = inflater.inflate(R.layout.group_member_filtered_stat, parent, false);

        return new BBMemberSingleStatRVAdapter.ViewHolder(spotView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.memberPositionTV.setText(Integer.toString(position + 1));
        holder.memberNameTV.setText(members.get(position).getNickname());
        holder.memberStatTV.setText(Integer.toString(getter.apply(members.get(position))));
        holder.viewAllStatsBTN.setOnClickListener(view -> listener.onViewAllStatsClicked(members.get(position)));
    }

    @Override
    public int getItemCount() {
        return this.members.size();
    }

    public void setMembers(List<BasketballMember> members) {
        this.members = members;
    }

    public void setGetter(Function<BasketballMember, Integer> getter) {
        this.getter = getter;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView memberPositionTV;
        private final TextView memberNameTV;
        private final TextView memberStatTV;
        private final Button viewAllStatsBTN;


        public ViewHolder(View view) {
            super(view);

            this.memberPositionTV = view.findViewById(R.id.filteredStatPositionTV);
            this.memberNameTV = view.findViewById(R.id.filteredStatMemberNameTV);
            this.memberStatTV = view.findViewById(R.id.filteredStatStatTV);
            this.viewAllStatsBTN = view.findViewById(R.id.filteredStatViewBTN);
        }
    }
}
