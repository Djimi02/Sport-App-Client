package com.example.sport_app_client.model.game;

import com.example.sport_app_client.model.Sports;
import com.example.sport_app_client.model.group.FootballGroup;
import com.example.sport_app_client.model.member.FootballMember;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FootballGame extends Game {
    private FootballGroup group;

    private List<FootballMember> members;

    private Integer victory; // -1 -> team 1 won, 0 -> draw, 1 -> team 2 won

    public FootballGame(Date date, FootballGroup group) {
        super(date, Sports.FOOTBALL);
        this.group = group;

        initVars();
    }

    private void initVars() {
        this.members = new ArrayList<>();
        this.victory = null;
    }

    public void addMember(FootballMember member) {
        this.members.add(member);
    }

    public void removeMember(Long memberID) {
        int memberToBeRemoved = -1;
        for (int i = 0; i < this.members.size(); i++) {
            if (this.members.get(i).getId() == memberID) {
                memberToBeRemoved = i;
                break;
            }
        }

        if (memberToBeRemoved != -1) {
            this.members.remove(memberToBeRemoved);
        }
    }

    public FootballGroup getGroup() {
        return group;
    }

    public void setGroup(FootballGroup group) {
        this.group = group;
    }

    public List<FootballMember> getMembers() {
        return members;
    }

    public void setMembers(List<FootballMember> members) {
        this.members = members;
    }

    public Integer getVictory() {
        return victory;
    }

    public void setVictory(Integer victory) {
        this.victory = victory;
    }
}
