package com.cpsc441.project.dutchblitz.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.cpsc441.project.dutchblitz.Fragments.ChatFragment;
import com.cpsc441.project.dutchblitz.Fragments.InviteFriendFragment;
import com.cpsc441.project.dutchblitz.R;

import java.util.ArrayList;

public class WaitingRoomActivity extends Activity {

    ArrayList<String> playerNames = new ArrayList<String>();
    ArrayAdapter<String> adapter;
    TextView titleOfRoom;
    String mePlayerUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_room);

        Intent intent = getIntent();
        String title = intent.getStringExtra("message");
        mePlayerUsername = intent.getStringExtra("username");

        titleOfRoom = (TextView) findViewById(R.id.roomName);
        titleOfRoom.setText(title);

        //Should grab user's user name
        playerNames.add(mePlayerUsername);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, playerNames);

        ListView listView = (ListView) findViewById(R.id.currentPlayers);
        listView.setAdapter(adapter);
    }


    public void addName(String playerName){
        playerNames.add(playerName);
        adapter.notifyDataSetChanged();

    }

    public void createInviteFriendsFrag(View view) {
        InviteFriendFragment frag = new InviteFriendFragment();
        frag.show(getFragmentManager(), "InvFrag");
    }

    public void startGameActivity(View view) {
        Intent i = new Intent(this, GameScreenActivity.class);
        startActivity(i);
    }

    public void createChatWindowFrag(View view) {
        Bundle bundle = new Bundle();
        bundle.putString("username", mePlayerUsername);

        ChatFragment frag = new ChatFragment();
        frag.setArguments(bundle);
        frag.show(getFragmentManager(), "ChatFrag");
    }
}
