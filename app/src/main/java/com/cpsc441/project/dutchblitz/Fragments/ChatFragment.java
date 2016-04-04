package com.cpsc441.project.dutchblitz.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.cpsc441.project.dutchblitz.Activities.WaitingRoomActivity;
import com.cpsc441.project.dutchblitz.ChatService;
import com.cpsc441.project.dutchblitz.R;
import com.cpsc441.project.dutchblitz.WaitingRoomService;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

public class ChatFragment extends DialogFragment {

    ArrayList<String> messages = new ArrayList<String>();
    ListView listView;
    EditText chatText;
    ArrayAdapter<String> adapter;
    String username, playerName, idm;

    public static final ReentrantLock lock = new ReentrantLock();

    public static String CHAT_ACTIVITY = "com.domain.action.CHAT_UI";
    private BroadcastReceiver bcast = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            Log.d("WROOM: ", "RECEIVED");
            if (intent.getStringExtra("a").equals("chat")) {
                Log.d("CHAT: ", "CHAT MESSAGE RECEIVED");
                String uname = intent.getStringExtra("username");
                String message = intent.getStringExtra("message");
                username = uname;
                addMessage(message);
            }
        }
    };

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        username = playerName = getArguments().getString("username");
        idm = getArguments().getString("id");

        View rootView = inflater.inflate(R.layout.fragment_chat, null, false);
        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, messages);

        listView = (ListView) rootView.findViewById(R.id.chatList);
        listView.setAdapter(adapter);

        chatText = (EditText) rootView.findViewById(R.id.chatEditText);

        Intent messIntent = new Intent(getActivity().getApplicationContext(), ChatService.class);
        getActivity().startService(messIntent);

        builder.setView(rootView)
                .setPositiveButton(R.string.send, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Log.d("CHATFRAGMENT: ", "STARTED SERVICE AGAIN");
                        ((WaitingRoomActivity) getActivity()).startWaitingRoomService(idm, false);
                        getActivity().unregisterReceiver(bcast);
                        IntentFilter filter = new IntentFilter();
                        filter.addAction(WaitingRoomActivity.JOIN_ACTIVITY);
                        getActivity().registerReceiver(((WaitingRoomActivity) getActivity()).bcast, filter);
                        ChatFragment.this.getDialog().cancel();
                    }
                });


        return builder.create();
    }

    public void addMessage(String msg) {
        if (msg.length() >= 2 && msg.startsWith("/w")) {
            privateMessage(msg);
        } else
            publicMessage(msg);
    }

    public void publicMessage(String msg) {
        messages.add(username + ": " + msg);
        adapter.notifyDataSetChanged();
    }

    public void privateMessage(String msg) {
        //Check username valid send a message to client
        String suppliedUsername = msg.split(" ")[1];
        //Check if username is valid, then send message to that user only
        boolean valid = true;
        if (valid) {
            messages.add("To [" + suppliedUsername + "]: " + msg.substring(3 + suppliedUsername.length() + 1, msg.length()));
            adapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onStart() {
        super.onStart();    //super.onStart() is where dialog.show() is actually called on the underlying dialog, so we have to do it after this point
        AlertDialog d = (AlertDialog) getDialog();

        IntentFilter filter = new IntentFilter();
        filter.addAction(CHAT_ACTIVITY);
        getActivity().registerReceiver(bcast, filter);

        if (d != null) {
            Button positiveButton = d.getButton(Dialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Boolean wantToCloseDialog = false;
                    //Do stuff, possibly set wantToCloseDialog to true then...
                    if (wantToCloseDialog) {
                        dismiss();
                    }
                    Log.d("CHAT SEND BUTTON: ", "PRESSED");
                    String message = chatText.getText().toString();
                    username = playerName;
                    //addMessage(message);

                    new ChatTask(message).execute(idm);
                    chatText.setText("");
                    //else dialog stays open. Make sure you have an obvious way to close the dialog especially if you set cancellable to false.
                }
            });
        }
    }

    public static class ChatTask extends AsyncTask<String, Void, Void> {
        final int PACKET_SIZE = 64;

        private Socket sock = null;
        private DataOutputStream out = null;
        private BufferedReader in = null;
        private String message;

        public ChatTask(String m) {
            message = m;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Void doInBackground(String... params) {
            lock.lock();
            Log.d("init", "test");
            try {
                sock = new Socket("162.246.157.144", 1234);
                Log.d("SOCKET STATISTICS: ", String.valueOf(WaitingRoomActivity.sock.getLocalPort()));
                out = new DataOutputStream(WaitingRoomActivity.sock.getOutputStream());
            }
            catch (UnknownHostException e) {
                System.out.println("Failed to create client socket.");
                e.printStackTrace();
            }
            catch (IOException e) {
                System.out.println("Socket creation caused error.");
                e.printStackTrace();
            }

            String body = message + "\n", idm = params[0];

            long header = 0;
            header = header | 10;
            header = header << 8;
            header = header | body.length(); header = header << 16;
            header = header | Integer.parseInt(idm);

            try {
                // Send credentials to server - IP address is currently hard-coded
                out.writeBytes(String.valueOf(header) + "\n" + body);
            }
            catch (UnknownHostException e) {
                System.out.println("Attempted to contact unknown host.");
                e.printStackTrace();
            }
            catch (IOException e) {
                System.out.println("Failed to send packet.");
                e.printStackTrace();
            }

            Log.d("Android: ", "Create Room");

            lock.unlock();

            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            try {
                sock.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
