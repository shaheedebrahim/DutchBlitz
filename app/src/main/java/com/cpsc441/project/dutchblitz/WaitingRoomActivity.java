package com.cpsc441.project.dutchblitz;

import android.os.Bundle;
import android.app.Activity;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

public class WaitingRoomActivity extends Activity {

    ArrayList<String> playerNames = new ArrayList<String>();
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_room);

        //Should grab user's user name
        playerNames.add("Me");
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, playerNames);
    }


    public void addName(String playerName){
        playerNames.add(playerName);
        adapter.notifyDataSetChanged();

    }
}