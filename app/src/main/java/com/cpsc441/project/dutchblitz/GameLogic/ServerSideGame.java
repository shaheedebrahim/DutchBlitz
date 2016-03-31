package com.cpsc441.project.dutchblitz.GameLogic;

public class ServerSideGame {
	
	private int NUMBER_OF_CANADIAN_PILES = 12;
	private int VISABLE_CARDS = 5;
	Card[] canadianPiles;
	Card[][] players;
	
	public ServerSideGame(int numberOfPlayers){
		canadianPiles = new Card[NUMBER_OF_CANADIAN_PILES];
		players = new Card[numberOfPlayers][VISABLE_CARDS];
	}
	
	//Function returns true, if move is valid
	public boolean playCardRequest(Card c, int indexRequested){
		if(canadianPiles[indexRequested]== null && c.value == 1){
			canadianPiles[indexRequested] = c;
			return true;
		}if(canadianPiles[indexRequested].colour == c.colour &&
				((canadianPiles[indexRequested].value+1) == c.value)){
			canadianPiles[indexRequested] = c;
			return true;
		}
		return false;
	}

	public void updatePlayerCards(int playerId, Card[] newCards){
		for(int i = 0; i < 5; i++){
			players[playerId][i] = newCards[i];
		}
	}
}
