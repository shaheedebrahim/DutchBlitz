package com.cpsc441.project.dutchblitz.Activities;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import com.cpsc441.project.dutchblitz.GameLogic.Card;
import com.cpsc441.project.dutchblitz.GameLogic.GameTable;
import com.cpsc441.project.dutchblitz.GameLogic.MoveRequest;
import com.cpsc441.project.dutchblitz.GameLogic.MyPiles;
import com.cpsc441.project.dutchblitz.R;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_game_screen);

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        ActionBar actionBar = getActionBar();
        actionBar.hide();
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

        mover = new MoveRequest();
        moveRequest = new Thread(mover);
        moveRequest.start();

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

    private void initButtons() {

    }

}
