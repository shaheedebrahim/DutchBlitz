package com.cpsc441.project.dutchblitz.GameLogic;

import android.content.Context;
import android.content.Intent;

import com.cpsc441.project.dutchblitz.Activities.GameScreenActivity;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * Created by Kendra on 2016-03-31.
 */
public class MoveRequest implements Runnable {

    private boolean terminated = false;
    private String id;
    private Card myCard;
    private int placeIndex = -1;
    GameScreenActivity game;

    private boolean moveAccepted = false;
    Context context;

    public MoveRequest(Context c, String i) {
        context = c;
        id = i;
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
                try {
                    /*Socket sock = new Socket("162.246.157.144", 1234);
                    DataOutputStream out = new DataOutputStream(sock.getOutputStream());
                    BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));

                    System.out.println("Both values set");

                    String body = String.valueOf(myCard.colour) + String.valueOf(myCard.value)
                            + String.valueOf(placeIndex) + "\n";

                    long header = 0;
                    header = header | 13;
                    header = header << 8;
                    header = header | body.length();
                    header = header << 16;
                    header = header | Integer.parseInt(id);

                    out.writeBytes(String.valueOf(header) + "\n" + body);

                    String resp = in.readLine();
                    if (resp.equals("1")) moveAccepted = true;
                    */
                    moveAccepted = true;
                    Intent intent = new Intent();
                    intent.setAction(GameScreenActivity.REFRESH_ACTIVITY);
                    context.sendBroadcast(intent);

                    Thread.sleep(1000);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
                /*catch (IOException e) {
                    e.printStackTrace();
                }*/

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
