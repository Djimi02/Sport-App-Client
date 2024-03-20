package com.example.sport_app_client.model.group;

import com.example.sport_app_client.model.Sports;
import com.example.sport_app_client.model.game.FootballGame;
import com.example.sport_app_client.model.member.FootballMember;

import java.util.ArrayList;
import java.util.List;

public class FootballGroup extends  Group {
    protected List<FootballGame> games;

    protected List<FootballMember> members;

    // TODO: ADD MORE GROUP STATS VARS

    public FootballGroup(String name) {
        super(name, Sports.FOOTBALL);

        initVars();
    }

    private void initVars() {
        this.members = new ArrayList<>();
        this.games = new ArrayList<>();
    }

    /* METHODS */

    public void addMember(FootballMember member) {
        this.members.add(member);
    }

    public void removeMember(FootballMember member) {
        int memberToBeRemoved = -1;
        for (int i = 0; i < this.members.size(); i++) {
            if (this.members.get(i).getId() == member.getId()) {
                memberToBeRemoved = i;
                break;
            }
        }
        if (memberToBeRemoved != -1) {
            this.members.remove(memberToBeRemoved);
        }
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
        // Adding the new game to the first position
        this.games.add(0, game);
    }

    public void removeGame(FootballGame game) {
        int gameToBeRemoved = -1;
        for (int i = 0; i < games.size(); i++) {
            if (this.members.get(i).getId() == game.getId()) {
                gameToBeRemoved = i;
                break;
            }
        }
        if (gameToBeRemoved != -1) {
            this.games.remove(gameToBeRemoved);
        }
    }

    public List<FootballGame> getGames() {
        return games;
    }

    public void setGames(List<FootballGame> games) {
        this.games = games;
    }

    public List<FootballMember> getMembers() {
        return members;
    }

    public void setMembers(List<FootballMember> members) {
        this.members = members;
    }
}
