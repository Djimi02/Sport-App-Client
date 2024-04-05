package com.example.sport_app_client.model.group;

import com.example.sport_app_client.model.Sports;
import com.example.sport_app_client.model.game.Game;
import com.example.sport_app_client.model.member.Member;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class Group<MemberT extends Member<?>, GameT extends Game<?,?>> {

    protected Long id;

    protected UUID uuid;

    protected String name;

    protected Sports sport;

    protected List<MemberT> members;

    protected List<GameT> games;

    public Group() {initVars();}

    public Group(String name, Sports sport) {
        this.name = name;
        this.sport = sport;
        initVars();
    }

    private void initVars() {
        this.members = new ArrayList<>();
        this.games = new ArrayList<>();
    }

    public void addMember(MemberT member) {
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

    public void addGame(GameT game) {
        // Adding the new game to the first position
        this.games.add(0, game);
    }
}