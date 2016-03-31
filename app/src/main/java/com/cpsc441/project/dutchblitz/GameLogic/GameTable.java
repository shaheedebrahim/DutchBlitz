package com.cpsc441.project.dutchblitz.GameLogic;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class GameTable {
	private int NUMBER_OF_CANADIAN_PILES = 12;
	private int MAX_NUMBER_OF_CARDS = 10;
	List<Stack<Card>> canadianPiles;
	private MyPiles me;
	Opponent[] opposition;

	
	
	//Will need server to pass number of players, id, and oppenents cards
	public GameTable(int numberOfPlayers, int id){
		canadianPiles=  new ArrayList<Stack<Card>>();
		for(int i = 0; i < NUMBER_OF_CANADIAN_PILES; i++){
			canadianPiles.add(i, (new Stack<Card>()));
			canadianPiles.get(i).push(Card.defaultCard);
		}
		me = new MyPiles();
		opposition = new Opponent[numberOfPlayers-1];
		me.playerId = id;		
	}

	public Card getTopCard(int index){
		Card temp = canadianPiles.get(index).peek();
		if(temp == null){
			return Card.defaultCard;
		}
		return temp;
	}

	public Card[] getCanadianPiles(){
		Card[] temp = new Card[12];
		for(int i = 0; i < NUMBER_OF_CANADIAN_PILES; i++){
			temp[i] = getTopCard(i);
		}
		return temp;
	}

	//Get user input.
	//Send request for move
	//Server will check if move is valid, will accept move or no
	//Server will update game Board for all players, (ie the server will update canadian piles, never player)
	//This player will remove the card from their myPiles
	//If the player returns false during remove, the game is over, inform server.
	
	public int countScore(){
		int total = 0;
		//Add up cards played
		for(int i = 0; i < NUMBER_OF_CANADIAN_PILES; i++){
			while (!canadianPiles.get(i).empty()){
				if(canadianPiles.get(i).pop().playerId == me.playerId) {
					total++;
				}
			}
		}
		//Subtract 2 points for each card left in blatz
		while(!me.blatzPile.isEmpty()){
			me.blatzPile.pop();
			total = Math.max(total-2, 0);
		}
		return total;
	}

	public Card[] myCards(){
		//Card[] temp = {me.woodPile.peek(), me.blatzPile.peek(), me.postPiles[0], me.postPiles[1], me.postPiles[2]};
		return me.myCards();
	}

}
