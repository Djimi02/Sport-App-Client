package com.example.sport_app_client.model.member;

import com.example.sport_app_client.model.Sports;
import com.example.sport_app_client.model.game.FootballGame;
import com.example.sport_app_client.model.group.FootballGroup;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeName;

import java.util.List;


public class FootballMember extends Member {


    private List<FootballGame> games;

    private int goals;
    private int assists;
    private int saves;
    private int fouls;

    public FootballMember() {}

    public FootballMember(String nickname, FootballGroup group) {
        super(nickname, Sports.FOOTBALL, group);

        initVars();
    }

    private void initVars() {
        this.goals = 0;
        this.assists = 0;
        this.saves = 0;
        this.fouls = 0;
    }

    /* GETTER AND SETTERS */

    public List<FootballGame> getGames() {
        return games;
    }

    public void setGames(List<FootballGame> games) {
        this.games = games;
    }

    public int getGoals() {
        return goals;
    }

    public void setGoals(int goals) {
        this.goals = goals;
    }

    public int getAssists() {
        return assists;
    }

    public void setAssists(int assists) {
        this.assists = assists;
    }

    public int getSaves() {
        return saves;
    }

    public void setSaves(int saves) {
        this.saves = saves;
    }

    public int getFouls() {
        return fouls;
    }

    public void setFouls(int fouls) {
        this.fouls = fouls;
    }
}