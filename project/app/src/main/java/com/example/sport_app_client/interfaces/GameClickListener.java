package com.example.sport_app_client.interfaces;

import com.example.sport_app_client.model.game.Game;

public interface GameClickListener {
    public void openGameDialog(Game<?> game);
}