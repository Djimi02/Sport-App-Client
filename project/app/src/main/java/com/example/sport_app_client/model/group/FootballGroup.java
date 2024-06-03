package com.example.sport_app_client.model.group;

import com.example.sport_app_client.model.Sports;
import com.example.sport_app_client.model.game.FootballGame;
import com.example.sport_app_client.model.member.FootballMember;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FootballGroup extends Group {

    protected List<FootballGame> games;

    protected List<FootballMember> members;

    public FootballGroup() {
        super.sport = Sports.FOOTBALL;
        initVars();
    }

    /* METHODS */

    private void initVars() {
        this.members = new ArrayList<>();
        this.games = new ArrayList<>();
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

    public void addGame(FootballGame game) {
        this.games.add(0, game);
    }

}