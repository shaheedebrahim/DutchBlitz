package com.cpsc441.project.dutchblitz.GameLogic;

import com.cpsc441.project.dutchblitz.Activities.GameScreenActivity;

/**
 * Created by Kendra on 2016-03-31.
 */
public class MoveRequest implements Runnable {

    private boolean terminated = false;
    private Card myCard;
    private int placeIndex;
    GameScreenActivity game;

    public MoveRequest(GameScreenActivity gsa){
        game = gsa;
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

    @Override
    public void run() {
        while(!terminated){
            if(myCard != null && placeIndex != -1){
                System.out.println("Both values set");

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
