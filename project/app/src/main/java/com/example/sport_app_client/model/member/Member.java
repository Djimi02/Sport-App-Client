package com.example.sport_app_client.model.member;

import com.example.sport_app_client.model.MemberRole;
import com.example.sport_app_client.model.Sports;
import com.example.sport_app_client.model.User;
import com.example.sport_app_client.model.game.Game;
import com.example.sport_app_client.model.group.Group;
import com.example.sport_app_client.model.stats.Stats;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class Member {

    protected long id;
    protected String nickname;
    protected User user;
    protected Sports sport;

    protected MemberRole role;

    protected Stats statsAbs;
    protected Group groupAbs;

    public Member() {}
}