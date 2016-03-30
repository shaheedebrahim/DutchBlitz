package com.cpsc441.project.dutchblitz.GameLogic;

import java.util.Random;

public class CleanDeck {
	//Colours: Red, Blue, Green, Yellow
	private String[] cardColours = {"r", "b", "g", "y"};
	private int DECK_SIZE = 40;
	public Card[] newDeck;


	
	public CleanDeck(int playerId){
		newDeck = new Card[DECK_SIZE];
		int j = 0;
		for(String c: cardColours){
			for(int i = 1; i < 11; i++){
				newDeck[j] = new Card(c,i, playerId);
				j++;
			}
		}
		shuffle();
		
	}
	
	private void shuffle(){
		Random rand = new Random();
		int newIndex;
		Card temp;
		for(int i = 0; i < DECK_SIZE; i++){
			newIndex = rand.nextInt(DECK_SIZE);
			temp = newDeck[newIndex];
			newDeck[newIndex]= newDeck[i];
			newDeck[i] = temp;
		}
	}

}
