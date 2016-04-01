package com.cpsc441.project.dutchblitz.Activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cpsc441.project.dutchblitz.Fragments.ChatFragment;
import com.cpsc441.project.dutchblitz.Fragments.InviteFriendFragment;
import com.cpsc441.project.dutchblitz.R;
import com.cpsc441.project.dutchblitz.WaitingRoomService;

import java.util.ArrayList;

public class WaitingRoomActivity extends Activity {

    ArrayList<String> playerNames = new ArrayList<String>();
    ArrayAdapter<String> adapter;
    TextView titleOfRoom;
    String mePlayerUsername;
    String id;
    String roomName;

    public static String JOIN_ACTIVITY = "com.domain.action.JOIN_UI";
    private BroadcastReceiver bcast = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            Log.d("WROOM: ", "RECEIVED");
            if (intent.getAction().equals(JOIN_ACTIVITY)) {
                String uname = intent.getStringExtra("username");
                Toast.makeText(getApplicationContext(), "Player " + uname + " joined", Toast.LENGTH_LONG).show();
                addName(uname);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_room);

        Intent intent = getIntent();
        roomName = intent.getStringExtra("message");
        mePlayerUsername = intent.getStringExtra("username");
        id = intent.getStringExtra("id");

        titleOfRoom = (TextView) findViewById(R.id.roomName);
        titleOfRoom.setText(roomName);

        //Should grab user's user name
        playerNames.add(mePlayerUsername);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, playerNames);

        ListView listView = (ListView) findViewById(R.id.currentPlayers);
        listView.setAdapter(adapter);

        IntentFilter filter = new IntentFilter();
        filter.addAction(JOIN_ACTIVITY);
        this.registerReceiver(bcast, filter);

        Intent messIntent = new Intent(this, WaitingRoomService.class);
        startService(messIntent);
        Log.d("TEST: ", "a;slkdfjlasdjflskdfjlskdfj");
    }


    public void addName(String playerName){
        playerNames.add(playerName);
        adapter.notifyDataSetChanged();

    }

    public void createInviteFriendsFrag(View view) {
        Bundle bundle = new Bundle();
        bundle.putString("id", id);
        bundle.putString("roomName", roomName);

        InviteFriendFragment frag = new InviteFriendFragment();
        frag.setArguments(bundle);
        frag.show(getFragmentManager(), "InvFrag");
    }

    public void startGameActivity(View view) {
        Intent i = new Intent(this, GameScreenActivity.class);
        i.putExtra("id", id);
        startActivity(i);
    }

    public void createChatWindowFrag(View view) {
        Bundle bundle = new Bundle();
        bundle.putString("username", mePlayerUsername);
        bundle.putString("id", id);

        ChatFragment frag = new ChatFragment();
        frag.setArguments(bundle);
        frag.show(getFragmentManager(), "ChatFrag");
    }
}
