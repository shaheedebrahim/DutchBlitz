package com.cpsc441.project.dutchblitz.Activities;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ToggleButton;

import com.cpsc441.project.dutchblitz.GameLogic.Card;
import com.cpsc441.project.dutchblitz.GameLogic.MyPiles;
import com.cpsc441.project.dutchblitz.R;

public class GameScreenActivity extends Activity {

    ToggleButton[] piles;
    MyPiles myPiles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_game_screen);

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        ActionBar actionBar = getActionBar();
        actionBar.hide();

        myPiles = new MyPiles();

        piles = new ToggleButton[]{(ToggleButton) findViewById(R.id.woodPile), (ToggleButton) findViewById(R.id.blatz),
                (ToggleButton) findViewById(R.id.postPile0), (ToggleButton) findViewById(R.id.postPile1),
                (ToggleButton) findViewById(R.id.postPile2)};

        setPilesText(myPiles.myCards());

    }

    public void toggleButtonSelected(View view) {
        ToggleButton pressed;
        for (int i = 0; i < piles.length; i++) {
            if (piles[i].isChecked()) {
                pressed = piles[i];
            }
        }
    }

    private void setPilesText(Card[] cards) {
        for (int i = 0; i < piles.length; i++) {
            piles[i].setText(cards[i].value + "");

        }
    }

}
