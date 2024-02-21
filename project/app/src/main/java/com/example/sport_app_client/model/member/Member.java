package com.example.sport_app_client.model.member;

import com.example.sport_app_client.model.Sports;
import com.example.sport_app_client.model.User;

public class Member {
    protected Long id;
    protected String nickname;
    protected User user;
    protected Sports sport;

    public Member(String nickname, Sports sport) {
        this.nickname = nickname;
        this.sport = sport;
    }
}
