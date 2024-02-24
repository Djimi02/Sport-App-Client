package com.example.sport_app_client.model.member;

import com.example.sport_app_client.model.Sports;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeName;


public class FootballMember extends Member {

//    private FootballGroup group;

//    private List<FootballGame> games;

    private int numOfGoalsScored;

    public FootballMember() {}

    public FootballMember(String nickname) {
        super(nickname, Sports.FOOTBALL);
//        this.group = group;

        initVars();
    }

    public int getNumOfGoalsScored() {
        return numOfGoalsScored;
    }

    public void setNumOfGoalsScored(int numOfGoalsScored) {
        this.numOfGoalsScored = numOfGoalsScored;
    }

    private void initVars() {
        this.numOfGoalsScored = 0;
    }

    @Override
    public int numberOfGroups() {
        return 10;
    }
}
