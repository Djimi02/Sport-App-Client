package com.example.sport_app_client.interfaces;

import com.example.sport_app_client.model.member.Member;

public interface GroupMemberDeletedListener {
    /**
     * This method should initialize all the views in layout group_member_settings_dialog and
     * open it as dialog.
     * @param member - member whose data to be used for the dialog
     */
    public void openMemberSettingsDialog(Member<?,?> member);
}
