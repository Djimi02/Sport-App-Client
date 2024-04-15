package com.example.sport_app_client.interfaces;

import com.example.sport_app_client.model.member.Member;

public interface GroupMemberDeletedListener {
    public void openMemberSettingsDialog(Member<?,?> member);
}
