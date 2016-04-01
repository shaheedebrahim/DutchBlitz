package com.cpsc441.project.dutchblitz.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.cpsc441.project.dutchblitz.Fragments.CreateRoomDialogFragment;
import com.cpsc441.project.dutchblitz.Fragments.JoinRoomFragment;
import com.cpsc441.project.dutchblitz.Fragments.ObserveFragment;
import com.cpsc441.project.dutchblitz.R;

import java.util.ArrayList;

public class PlayerHomeActivity extends Activity {

    ArrayList<String> statisticNames = new ArrayList<String>();
    TextView titleText;
    String playerName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_home);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        playerName = intent.getStringExtra("message");

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

        CreateRoomDialogFragment dialogFrag = new CreateRoomDialogFragment();
        dialogFrag.setArguments(bundle);
        dialogFrag.show(getFragmentManager(), "Diag");
    }

    public void createJoinRoomFragment(View view) {
        Bundle bundle = new Bundle();
        bundle.putString("username", playerName);

        JoinRoomFragment roomFragment = new JoinRoomFragment();
        roomFragment.setArguments(bundle);
        roomFragment.show(getFragmentManager(), "joinFrag");
    }

    public void createLeaderboardActivity(View view) {
        Intent i = new Intent(this, LeaderboardActivity.class);
        startActivity(i);
    }

    public void createObserveRoomFragment(View view) {
        ObserveFragment observeFragment = new ObserveFragment();
        observeFragment.show(getFragmentManager(), "Frag");
    }

}
