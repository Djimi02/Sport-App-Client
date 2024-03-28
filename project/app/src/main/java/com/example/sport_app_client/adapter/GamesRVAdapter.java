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
import com.example.sport_app_client.interfaces.GameClickListener;
import com.example.sport_app_client.model.game.Game;

import java.util.List;

public class GamesRVAdapter extends RecyclerView.Adapter<GamesRVAdapter.ViewHolder> {

    private List<? extends Game<?,?>> games;
    private GameClickListener listener;

    public GamesRVAdapter(List<? extends Game<?,?>> members, GameClickListener listener) {
        this.games = members;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View spotView = inflater.inflate(R.layout.game_item_rv, parent, false);

        GamesRVAdapter.ViewHolder viewHolder = new GamesRVAdapter.ViewHolder(spotView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.gameDate.setText(games.get(position).getDate().toString());
        holder.gameResults.setText(games.get(position).getResults().toString());
        Game game = games.get(position);
        holder.btn.setOnClickListener(view -> {
            listener.openGameDialog(game);
        });
    }

    @Override
    public int getItemCount() {
        return games.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView gameDate;
        private final TextView gameResults;
        private final Button btn;

        public ViewHolder(View view) {
            super(view);

            this.gameDate = view.findViewById(R.id.gameItemGameDate);
            this.gameResults = view.findViewById(R.id.gameItemGameResults);
            this.btn = view.findViewById(R.id.gameItemGameBTN);
        }
    }
}
