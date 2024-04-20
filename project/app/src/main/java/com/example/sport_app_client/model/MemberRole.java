package com.example.sport_app_client.model;

public enum MemberRole {

    MEMBER("Member"),
    GAME_MAKER("Game Maker"),
    GROUP_ADMIN("Admin");

    private final String stringValue;

    private MemberRole(String stringValue) {
        this.stringValue = stringValue;
    }

    @Override
    public String toString() {
        return this.stringValue;
    }

}