package com.example.sport_app_client.interfaces;

import com.example.sport_app_client.model.member.Member;

public interface SelectMemberToJoinGroupListener {
    public void onMemberSelected(Member<?> member);
}