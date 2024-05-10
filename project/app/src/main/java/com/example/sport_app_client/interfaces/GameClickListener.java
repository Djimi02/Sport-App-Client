package com.example.sport_app_client.interfaces;

import com.example.sport_app_client.model.game.Game;

public interface GameClickListener {
    /**
     * This method initializes all the views in the layout R.layout.game_stats_dialog
     * and opens it as dialog window.
     * @param game - game's stats to be displayed
     */
    public void openGameDialog(Game game);
}