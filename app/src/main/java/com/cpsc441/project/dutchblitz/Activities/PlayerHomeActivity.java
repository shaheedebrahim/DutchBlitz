package com.cpsc441.project.dutchblitz.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cpsc441.project.dutchblitz.Fragments.ObserveFragment;
import com.cpsc441.project.dutchblitz.R;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

public class PlayerHomeActivity extends Activity {

    ArrayList<String> statisticNames = new ArrayList<String>();
    TextView titleText;
    String playerName;
    String id;

    public static final ReentrantLock lock = new ReentrantLock();
    public static boolean createSuccess = false, joinSuccess = false;
    public static Context appCon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_home);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        playerName = intent.getStringExtra("message");
        id = intent.getStringExtra("id");
        appCon = getApplicationContext();

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

    @Override
    public void onDestroy() {

        super.onDestroy();
    }

    public static class JoinRoomFragment extends DialogFragment {

        EditText roomName;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            // Get the layout inflater
            LayoutInflater inflater = getActivity().getLayoutInflater();

            final String username = getArguments().getString("username");
            final String idm = getArguments().getString("id");

            builder.setView(inflater.inflate(R.layout.fragment_join_room, null))
                    .setPositiveButton(R.string.join_room_text, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            roomName = (EditText) getDialog().findViewById(R.id.joinRoom);
                            String body = roomName.getText().toString();

                            new CreateRoomTask("1").execute(body, idm);

                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } finally {
                                lock.lock();
                                if (joinSuccess) {
                                    Toast.makeText(appCon, "Joined", Toast.LENGTH_LONG).show();
                                    Intent i = new Intent(getActivity(), WaitingRoomActivity.class);
                                    i.putExtra("message", roomName.getText().toString());
                                    i.putExtra("username", username);
                                    i.putExtra("id", idm);
                                    startActivity(i);
                                }
                                else
                                    Toast.makeText(appCon, "Failed", Toast.LENGTH_LONG).show();
                                createSuccess = false;
                                lock.unlock();
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

    public static class CreateRoomDialogFragment extends DialogFragment {

        EditText roomName;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            // Get the layout inflater
            LayoutInflater inflater = getActivity().getLayoutInflater();

            final String username = getArguments().getString("username");
            final String idm = getArguments().getString("id");

            Log.d("test", "OMG WRITE SOMETHING");
            builder.setView(inflater.inflate(R.layout.fragment_create_room_dialog, null))
                    .setPositiveButton(R.string.create, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            roomName = (EditText) getDialog().findViewById(R.id.room_name);
                            String body = roomName.getText().toString();

                            new CreateRoomTask("0").execute(body, idm);

                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } finally {
                                lock.lock();
                                if (createSuccess) {
                                    Toast.makeText(appCon, "Joined", Toast.LENGTH_LONG).show();
                                    Intent i = new Intent(getActivity(), WaitingRoomActivity.class);
                                    i.putExtra("message", roomName.getText().toString());
                                    i.putExtra("username", username);
                                    i.putExtra("id", idm);
                                    startActivity(i);
                                }
                                else
                                    Toast.makeText(appCon, "Failed", Toast.LENGTH_LONG).show();
                                createSuccess = false;
                                lock.unlock();
                            }
                            //new GameRoomTask(getActivity().getApplicationContext()).execute(roomNameEditText.getText().toString());
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

    public static class CreateRoomTask extends AsyncTask<String, Void, Void> {
        final int PACKET_SIZE = 64;

        private Socket sock = null;
        private DataOutputStream out = null;
        private BufferedReader in = null;
        private String mode;

        public CreateRoomTask(String m) {
            mode = m;
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
                Log.d("init: ", sock.toString());
                out = new DataOutputStream(sock.getOutputStream());
                in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
                Log.d("Init: ", "Success");
            }
            catch (UnknownHostException e) {
                System.out.println("Failed to create client socket.");
                e.printStackTrace();
            }
            catch (IOException e) {
                System.out.println("Socket creation caused error.");
                e.printStackTrace();
            }

            String body = params[0] + "\n" + mode + "\n", idm = params[1], resp = "";

            long header = 0;
            header = header | 9;
            header = header << 8;
            header = header | body.length(); header = header << 16;
            header = header | Integer.parseInt(idm);

            try {
                // Send credentials to server - IP address is currently hard-coded
                out.writeBytes(String.valueOf(header) + "\n" + body);

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

            Log.d("Android: ", "Create Room");

            if (!resp.equals("0")) {
                if (mode.equals("0"))
                    createSuccess = true;
                else joinSuccess = true;
            }

            lock.unlock();

            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            Log.d("Android: ", "Exchange done");
            try {
                sock.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
