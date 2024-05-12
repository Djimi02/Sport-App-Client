package com.example.sport_app_client.model.group;

import com.example.sport_app_client.model.Sports;
import com.example.sport_app_client.model.game.BasketballGame;
import com.example.sport_app_client.model.member.BasketballMember;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BasketballGroup extends Group {

    private List<BasketballGame> games;

    private List<BasketballMember> members;

    public BasketballGroup() {
        super.sport = Sports.BASKETBALL;
        initVars();
    }

    private void initVars() {
        this.members = new ArrayList<>();
        this.games = new ArrayList<>();
    }

    public void addMember(BasketballMember member) {
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

    public void addGame(BasketballGame game) {
        this.games.add(game);
    }
}
