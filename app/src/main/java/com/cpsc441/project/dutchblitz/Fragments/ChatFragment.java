package com.cpsc441.project.dutchblitz.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.cpsc441.project.dutchblitz.R;

import java.util.ArrayList;

public class ChatFragment extends DialogFragment {

    ArrayList<String> messages = new ArrayList<String>();
    ListView listView;
    EditText chatText;
    ArrayAdapter<String> adapter;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View rootView = inflater.inflate(R.layout.fragment_chat, null, false);
        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, messages);

        listView = (ListView) rootView.findViewById(R.id.chatList);
        listView.setAdapter(adapter);

        chatText = (EditText) rootView.findViewById(R.id.chatEditText);

        builder.setView(rootView)
                .setPositiveButton(R.string.send, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ChatFragment.this.getDialog().cancel();
                    }
                });


        return builder.create();
    }

    public void addMessage(String msg) {
        messages.add(msg);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onStart() {
        super.onStart();    //super.onStart() is where dialog.show() is actually called on the underlying dialog, so we have to do it after this point
        AlertDialog d = (AlertDialog) getDialog();
        if (d != null) {
            Button positiveButton = d.getButton(Dialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Boolean wantToCloseDialog = false;
                    //Do stuff, possibly set wantToCloseDialog to true then...
                    if (wantToCloseDialog)
                        dismiss();
                    addMessage(chatText.getText().toString());
                    chatText.setText("");
                    //else dialog stays open. Make sure you have an obvious way to close the dialog especially if you set cancellable to false.
                }
            });
        }
    }

}
