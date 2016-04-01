package com.cpsc441.project.dutchblitz.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.cpsc441.project.dutchblitz.Fragments.CreateRoomDialogFragment;
import com.cpsc441.project.dutchblitz.Fragments.ObserveFragment;
import com.cpsc441.project.dutchblitz.R;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class PlayerHomeActivity extends Activity {

    ArrayList<String> statisticNames = new ArrayList<String>();
    TextView titleText;
    String playerName;
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_home);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        playerName = intent.getStringExtra("message");
        id = intent.getStringExtra("id");

        titleText = (TextView) findViewById(R.id.usernameText);
        titleText.setText(playerName);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, statisticNames);
        setNames();

        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);
    }

    public void setNames() {
        statisticNames.add("Games Played");
        statisticNames.add("Games Won");
        statisticNames.add("Games Lost");
        statisticNames.add("Disconnections");
    }

    public void displayCreateFragment(View view) {
        showDialog();
    }

    private void showDialog() {
        Bundle bundle = new Bundle();
        bundle.putString("username", playerName);
        bundle.putString("id", id);

        CreateRoomDialogFragment dialogFrag = new CreateRoomDialogFragment();
        dialogFrag.setArguments(bundle);
        dialogFrag.show(getFragmentManager(), "Diag");
    }

    public void createJoinRoomFragment(View view) {
        Bundle bundle = new Bundle();
        bundle.putString("username", playerName);
        bundle.putString("id", id);

        JoinRoomFragment roomFragment = new JoinRoomFragment();
        roomFragment.setArguments(bundle);
        roomFragment.show(getFragmentManager(), "joinFrag");
    }

    public void createLeaderboardActivity(View view) {
        Intent i = new Intent(this, LeaderboardActivity.class);
        i.putExtra("id", id);
        startActivity(i);
    }

    public void createObserveRoomFragment(View view) {
        Bundle bundle = new Bundle();
        bundle.putString("username", playerName);
        bundle.putString("id", id);

        ObserveFragment observeFragment = new ObserveFragment();
        observeFragment.setArguments(bundle);
        observeFragment.show(getFragmentManager(), "Frag");
    }

    public static class JoinRoomFragment extends DialogFragment {

        EditText roomName;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            // Get the layout inflater
            LayoutInflater inflater = getActivity().getLayoutInflater();

            final String idm = getArguments().getString("id");

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
                            header = header | Integer.parseInt(idm);

                            try {
                                Socket sock = new Socket("162.246.157.144", 1234);
                                DataOutputStream out = new DataOutputStream(sock.getOutputStream());
                                BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));

                                // Send credentials to server - IP address is currently hard-coded
                                out.writeBytes(String.valueOf(header) + "\n" + body + "\n");

                                resp = in.readLine();
                            }
                            catch (UnknownHostException e) {
                                System.out.println("Attempted to contact unknown host.");
                                e.printStackTrace();
                            }
                            catch (IOException e) {
                                System.out.println("Failed to send packet.");
                                e.printStackTrace();
                            }


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
}
