package com.example.sport_app_client.model.member;

import com.example.sport_app_client.model.Sports;
import com.example.sport_app_client.model.group.FootballGroup;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FootballMember extends Member<FootballGroup> {

    /* Stats */
    private int goals;
    private int assists;
    private int saves;
    private int fouls;
    private Boolean isPartOfTeam1; // used for games

    public FootballMember() {
        super.setSport(Sports.FOOTBALL);
        initVars();
    }

    public FootballMember(String nickname, FootballGroup group) {
        super(nickname, Sports.FOOTBALL, group);

        initVars();
    }

    private void initVars() {
        this.goals = 0;
        this.assists = 0;
        this.saves = 0;
        this.fouls = 0;
        this.isPartOfTeam1 = null;
    }
}