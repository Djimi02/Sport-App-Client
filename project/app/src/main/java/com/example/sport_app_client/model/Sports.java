package com.example.sport_app_client.model;

import androidx.annotation.NonNull;

public enum Sports {
    NO_SPORT("Select Sport"),
    FOOTBALL("Football"),
    BASKETBALL("Basketball"),
    TENNIS("Tennis"),
    TABLE_TENNIS("Table Tennis");

    private String displayName;

    Sports(String sport) {
        this.displayName = sport;
    }

    @NonNull
    @Override
    public String toString() {
        return this.displayName;
    }
}
