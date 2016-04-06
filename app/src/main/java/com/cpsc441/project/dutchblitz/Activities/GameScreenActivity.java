package com.cpsc441.project.dutchblitz.Activities;

import android.app.ActionBar;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.cpsc441.project.dutchblitz.GameLogic.Card;
import com.cpsc441.project.dutchblitz.GameLogic.GameTable;
import com.cpsc441.project.dutchblitz.GameLogic.MoveRequest;
import com.cpsc441.project.dutchblitz.R;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

public class GameScreenActivity extends Activity {

    ToggleButton[] piles;
    ToggleButton[] canadianPiles;
    GameTable game;
    Card[] myCanadian;
    Card[] myPiles;
    Card myCardSelected;
    Card canadianPressed;

    MoveRequest mover;
    Thread moveRequest;

    Button pauseResume;
    boolean pauseState;

    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_game_screen);

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        ActionBar actionBar = getActionBar();
        actionBar.hide();

        Intent i = new Intent();
        id = i.getStringExtra("id");

        pauseResume = (Button) findViewById(R.id.pauseResumeButton);

        //Give the correct number of players (must be less than 4)
        //Give the playerID for this game(should be 0 or 1 or 2 or 3)
        game = new GameTable(4, 0);
        myPiles = game.myCards();
        myCanadian = game.getCanadianPiles();

        piles = new ToggleButton[]{(ToggleButton) findViewById(R.id.woodPile), (ToggleButton) findViewById(R.id.blatz),
                (ToggleButton) findViewById(R.id.postPile0), (ToggleButton) findViewById(R.id.postPile1),
                (ToggleButton) findViewById(R.id.postPile2)};
        canadianPiles = new ToggleButton[]{(ToggleButton) findViewById(R.id.canadian0), (ToggleButton) findViewById(R.id.canadian1),
                (ToggleButton) findViewById(R.id.canadian2),(ToggleButton) findViewById(R.id.canadian3), (ToggleButton) findViewById(R.id.canadian4),
                (ToggleButton) findViewById(R.id.canadian5),(ToggleButton) findViewById(R.id.canadian6),(ToggleButton) findViewById(R.id.canadian7),
                (ToggleButton) findViewById(R.id.canadian8),(ToggleButton) findViewById(R.id.canadian9),(ToggleButton) findViewById(R.id.canadian10),
                (ToggleButton) findViewById(R.id.canadian11)};

        setPilesText(myPiles, piles);
        setPileColour(myPiles, piles);

        setPilesText(myCanadian, canadianPiles);
        setPileColour(myCanadian, canadianPiles);

        piles[0].setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                game.me.flip();
                updatePiles();
                return true;
            }
        });

        updatePiles();
        mover = new MoveRequest(getApplicationContext(), id);
        moveRequest = new Thread(mover);
        moveRequest.start();

    }


    public void updateCanadian(Card newCard, int index){
        game.updateCanadian(newCard, index);
        updatePiles();
    }
    private void updatePiles() {
        myPiles = game.myCards();
        myCanadian = game.getCanadianPiles();
        setPilesText(myPiles, piles);
        setPileColour(myPiles, piles);
        setPilesText(myCanadian, canadianPiles);
        setPileColour(myCanadian, canadianPiles);
    }

    public static String REFRESH_ACTIVITY = "com.domain.action.REFRESH_UI";
    public static String WIN_ACTIVITY = "com.domain.action.WIN_UI";
    public static String UPDATE_ACTIVITY = "com.domain.action.WIN_UI";

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (/*intent.getAction().equals(REFRESH_ACTIVITY)*/true) {
                Log.d("MOVER: ", "TEST");
                if (mover.getMoveAccepted()) {
                    if (!game.moveCard(mover.getMyCard(), mover.getPlaceIndex()))
                        new WinTask().execute(id);
                    updatePiles();
                }
                clearSelections();
                mover.resetCard();
                mover.resetPlaceIndex();
            }
            else if (intent.getAction().equals(UPDATE_ACTIVITY)) {
                int colour = Integer.parseInt(intent.getStringExtra("colour"));
                int value = Integer.parseInt(intent.getStringExtra("value"));
                int index = Integer.parseInt(intent.getStringExtra("index"));
                int identifier = Integer.parseInt(intent.getStringExtra("identifier"));
                Card newCard = new Card(colour, value, identifier);
                updateCanadian(newCard, index);
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();

        IntentFilter filter = new IntentFilter();
        filter.addAction(REFRESH_ACTIVITY);
        this.registerReceiver(broadcastReceiver, filter);
    }

    @Override
    public void onPause() {
        super.onPause();
        this.unregisterReceiver(broadcastReceiver);
    }

    public void woodPileFlip(View view) {
        game.me.flip();
        updatePiles();
    }

    public void clearSelections() {
        for (int i = 0; i < canadianPiles.length; i++) {
            canadianPiles[i].setChecked(false);
            setButtonColour(myCanadian[i], canadianPiles[i]);
        }
        for (int i = 0; i < piles.length; i++) {
            piles[i].setChecked(false);
            setButtonColour(myPiles[i], piles[i]);
        }
    }

    public void mineSelected(View view) {

        mover.resetCard();
        for (int i = 0; i < piles.length; i++) {
            if(view.getId() == piles[i].getId() && piles[i].isChecked()){
                mover.setCard(myPiles[i]);
                piles[i].setChecked(true);
                piles[i].setBackgroundColor(Color.MAGENTA);
                //Highlight selected card some how
            }else{
                piles[i].setChecked(false);
                setButtonColour(myPiles[i], piles[i]);
            }
        }
    }

    public void canadianSelected(View view) {
        mover.resetPlaceIndex();
        for (int i = 0; i < canadianPiles.length; i++) {
            if(view.getId() == canadianPiles[i].getId() && canadianPiles[i].isChecked()){
               mover.setPlaceIndex(i);
                canadianPiles[i].setBackgroundColor(Color.MAGENTA);
                canadianPiles[i].setChecked(true);
                //Highlight selected card some how
            }else{
                canadianPiles[i].setChecked(false);
                setButtonColour(myCanadian[i], canadianPiles[i]);
            }
        }
    }

    private void setPilesText(Card[] cards, ToggleButton[] tb) {
        for (int i = 0; i < tb.length; i++) {
            System.out.println(i);
            tb[i].setText(cards[i].value + "");
            tb[i].setTextOff(cards[i].value + "");
            tb[i].setTextOn(cards[i].value + "");
        }
    }

    private void setButtonColour(Card c, ToggleButton tb){
        switch (c.colour){
            case 0:
                tb.setBackgroundColor(Color.RED);
                break;
            case 1:
                tb.setBackgroundColor(Color.BLUE);
                break;
            case 2:
                tb.setBackgroundColor(Color.YELLOW);
                break;
            case 3:
                tb.setBackgroundColor(Color.GREEN);
                break;
            case 4:
                tb.setBackgroundColor(Color.GRAY);
        }

    }
    //Could add to setPileText for speed, but this is more more simplistic to read
    private void setPileColour(Card[] cards, ToggleButton[] tb){
        for (int i = 0; i < tb.length; i++) {
            setButtonColour(cards[i], tb[i]);

        }
    }

    public void pauseAndResume(View view) {
        if (pauseResume.getText().toString().equals("=")) {
            pauseGame();
        } else {
            resumeGame();
        }
    }

    public void playerDisconnectHandler() {
        if (!pauseState)
            pauseGame();
    }

    public void playerReconnectHandler() {
        if (pauseState)
            resumeGame();
    }

    public void pauseGame() {
        pauseResume.setText(">");
        pauseState = true;
        Toast.makeText(getApplicationContext(), "Game paused", Toast.LENGTH_LONG).show();
    }

    public void resumeGame() {
        pauseResume.setText("=");
        pauseState = false;
        Toast.makeText(getApplicationContext(), "Game resumed", Toast.LENGTH_LONG).show();
    }

    public static class WinTask extends AsyncTask<String, Void, Void> {
        final int PACKET_SIZE = 64;

        private Socket sock = null;
        private DataOutputStream out = null;
        private BufferedReader in = null;

        public WinTask() {
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Void doInBackground(String... params) {
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

            String body = "win\n", idm = params[0];

            long header = 0;
            header = header | 12;
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
