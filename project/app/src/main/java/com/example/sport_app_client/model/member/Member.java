package com.example.sport_app_client.model.member;

import com.example.sport_app_client.model.Sports;
import com.example.sport_app_client.model.User;
import com.example.sport_app_client.model.game.Game;
import com.example.sport_app_client.model.group.Group;
import com.example.sport_app_client.model.stats.Stats;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class Member<GroupT extends Group<?,?>, StatsT extends Stats<?,?>> {
    protected long id;
    protected String nickname;
    protected User user;
    protected Sports sport;

    protected GroupT group;

    protected StatsT stats;

    protected Boolean isAdmin;

    public Member() {initVars();}

    public Member(String nickname, Sports sport, GroupT group, StatsT statsT) {
        this.nickname = nickname;
        this.sport = sport;
        this.group = group;
        this.stats = statsT;
        initVars();
    }

    private void initVars() {
        this.isAdmin = false;
    }
}