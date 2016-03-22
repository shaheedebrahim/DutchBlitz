package com.cpsc441.project.dutchblitz;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class PlayerHomeActivity extends Activity {

    ArrayList<String> statisticNames = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_home);
        getActionBar().setDisplayHomeAsUpEnabled(true);

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

}
