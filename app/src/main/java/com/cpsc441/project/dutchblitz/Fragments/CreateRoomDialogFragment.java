package com.cpsc441.project.dutchblitz.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;

import com.cpsc441.project.dutchblitz.Activities.WaitingRoomActivity;
import com.cpsc441.project.dutchblitz.R;


public class CreateRoomDialogFragment extends DialogFragment {


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        builder.setView(inflater.inflate(R.layout.fragment_create_room_dialog, null))
                .setPositiveButton(R.string.create, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Intent i = new Intent(getActivity(), WaitingRoomActivity.class);
                        startActivity(i);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        CreateRoomDialogFragment.this.getDialog().cancel();
                    }
                });

        return builder.create();
    }

}