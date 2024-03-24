package com.example.sport_app_client.helpers;

import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.example.sport_app_client.R;
import com.example.sport_app_client.interfaces.ActionDoer;

public class ConfirmActionDialog {
    private static AlertDialog.Builder dialogBuilder;
    private static AlertDialog dialog;

    private ConfirmActionDialog() {}

    public static void showDialog(Activity context, String text, ActionDoer actionDoer) {
        // Build dialog
        dialogBuilder = new AlertDialog.Builder(context);
        final View popupView = context.getLayoutInflater().inflate(R.layout.confirm_action_dialog, null);

        TextView textTV = popupView.findViewById(R.id.confirmActionTV);
        textTV.setText(text.toString());
        Button confirmBTN = popupView.findViewById(R.id.confirmActionBTN);
        confirmBTN.setOnClickListener(view -> {actionDoer.doAction(); dialog.dismiss();});
        Button rejectBTN = popupView.findViewById(R.id.rejectActionBTN);
        rejectBTN.setOnClickListener(view -> dialog.dismiss());

        // Show dialog
        dialogBuilder.setView(popupView);
        dialog = dialogBuilder.create();
        dialog.show();
    }
}
