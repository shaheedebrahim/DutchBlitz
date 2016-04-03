package com.cpsc441.project.dutchblitz.GameLogic;

import android.content.Context;
import android.content.Intent;

import com.cpsc441.project.dutchblitz.Activities.GameScreenActivity;

/**
 * Created by Kendra on 2016-03-31.
 */
public class MoveRequest implements Runnable {

    private boolean terminated = false;
    private Card myCard;
    private int placeIndex = -1;
    GameScreenActivity game;

    private boolean moveAccepted = false;
    Context context;

    public MoveRequest(Context c) {
        context = c;
    }

    public void endGame(){
        terminated = true;
    }

    public void setCard(Card c){
        System.out.println("Setting new Card");
        myCard = c;
    }
    public void resetCard(){
        myCard = null;
    }

    public void setPlaceIndex(int i){
        System.out.println("Setting new Index");
        placeIndex = i;
    }
    public void resetPlaceIndex(){
        placeIndex = -1;
    }

    public boolean getMoveAccepted() {
        return moveAccepted;
    }

    public Card getMyCard() {
        return myCard;
    }

    public int getPlaceIndex() {
        return placeIndex;
    }

    @Override
    public void run() {
        while(!terminated){
            if(myCard != null && placeIndex != -1){
                System.out.println("Both values set");

                moveAccepted = true;

                Intent intent = new Intent();
                intent.setAction(GameScreenActivity.REFRESH_ACTIVITY);
                context.sendBroadcast(intent);

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                //Unselect buttons
                //Send Message to Server
                //If move approved update piles
                    //By pushing new card onto canadain piles (in gameTable) and update bored
                    //Remove card from game.me, update piles
                //else maybe flash an X or something
            }

        }
    }
}
