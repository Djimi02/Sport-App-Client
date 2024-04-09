package com.example.sport_app_client.model;

import com.example.sport_app_client.model.member.Member;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User {
    private long id;
    private String userName;
    private String email;
    private Roles role;
    private List<Member<?,?>> members;

    public User() { initVars(); }

    public User(String userName, long id) {
        this.userName = userName;
        this.id = id;

        initVars();
    }

    public User(String userName, String email) {
        this.userName = userName;
        this.email = email;

        initVars();
    }

    private void initVars() {
        this.members = new ArrayList<>();
        this.role = Roles.USER;
    }

    public boolean doesMemberExists(long memberID) {
        for (Member<?,?> member : members) {
            if (member.getId() == memberID) {
                return true;
            }
        }
        return false;
    }
}
