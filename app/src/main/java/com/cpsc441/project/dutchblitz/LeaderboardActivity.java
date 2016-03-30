package com.cpsc441.project.dutchblitz;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class LeaderboardActivity extends Activity {

    ArrayList<String> playerNames = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        playerNames.add("Shaheed 1000000");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, playerNames);

        ListView listView = (ListView) findViewById(R.id.rankListView);
        listView.setAdapter(adapter);
    }

}
