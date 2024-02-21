package com.example.sport_app_client.model;

import com.example.sport_app_client.model.member.Member;

import java.util.ArrayList;
import java.util.List;

public class User {
    private Long id;
    private String userName;
    private String email;
    private String password;
    private Roles role;
    private List<Member> members;

    public User(String userName, String email, String password) {
        this.userName = userName;
        this.email = email;
        this.password = password;

        initVars();
    }

    private void initVars() {
        this.members = new ArrayList<>();
        this.role = Roles.USER;
    }

}
