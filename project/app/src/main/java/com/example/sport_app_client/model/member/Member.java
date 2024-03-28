package com.example.sport_app_client.model.member;

import com.example.sport_app_client.model.Sports;
import com.example.sport_app_client.model.User;
import com.example.sport_app_client.model.group.Group;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class Member<GroupT extends Group<?,?>> {
    protected long id;
    protected String nickname;
    protected User user;
    protected Sports sport;

    protected GroupT group;

    protected Boolean isAdmin;

    /* Stats */

    protected Integer wins;
    protected Integer draws;
    protected Integer loses;

    public Member() {initVars();}

    public Member(String nickname, Sports sport, GroupT group) {
        this.nickname = nickname;
        this.sport = sport;
        this.group = group;

        initVars();
    }

    private void initVars() {
        this.wins = 0;
        this.draws = 0;
        this.loses = 0;
    }
}