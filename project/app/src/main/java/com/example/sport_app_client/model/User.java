package com.example.sport_app_client.model;

import com.example.sport_app_client.model.member.Member;

import java.util.ArrayList;
import java.util.List;

public class User {
    private Long id;
    private String userName;
    private String email;
    private Roles role;
    private List<Member> members;

    public User(String userName, String email) {
        this.userName = userName;
        this.email = email;

        initVars();
    }

    private void initVars() {
        this.members = new ArrayList<>();
        this.role = Roles.USER;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Roles getRole() {
        return role;
    }

    public void setRole(Roles role) {
        this.role = role;
    }

    public List<Member> getMembers() {
        return members;
    }

    public void setMembers(List<Member> members) {
        this.members = members;
    }
}
