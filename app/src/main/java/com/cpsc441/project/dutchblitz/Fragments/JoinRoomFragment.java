package com.cpsc441.project.dutchblitz.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.EditText;

import com.cpsc441.project.dutchblitz.R;

public class JoinRoomFragment extends DialogFragment {

    EditText roomName;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        final String id = getArguments().getString("id");

        builder.setView(inflater.inflate(R.layout.fragment_join_room, null))
                .setPositiveButton(R.string.join_room_text, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        roomName = (EditText) getDialog().findViewById(R.id.joinRoom);
                        String body = roomName.getText().toString(), resp = "";

                        long header = 0;
                        header = header | 9;
                        header = header << 8;
                        header = header | body.length(); header = header << 16;
                        header = header | Integer.parseInt(id);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        JoinRoomFragment.this.getDialog().cancel();
                    }
                });

        return builder.create();
    }
}
