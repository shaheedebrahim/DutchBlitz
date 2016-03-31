package com.cpsc441.project.dutchblitz.GameLogic;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class MyPiles {

	Card[] postPiles;
	Queue<Card> woodPile;
	Stack<Card> blatzPile;
	int playerId = 1;
	CleanDeck cd;
	
	
	public MyPiles(){
		postPiles = new Card[3];
		woodPile = new LinkedList<Card>();
		blatzPile = new Stack<Card>();
		cd = new CleanDeck(playerId);
		deal();
	}
	
	private void deal(){
		//Dealing cards to post piles 
		for(int i = 0; i < 3; i++){
			postPiles[i] = cd.newDeck[i];
		}
		//Dealing cards to blatz pile
		for(int i = 3; i < 13; i++){
			blatzPile.push(cd.newDeck[i]);
		}
		//Dealing cards to wood piles
		for(int i = 13; i < 40; i++){
			woodPile.add(cd.newDeck[i]);
		}
	}
	
	//pileIndexFrom: 0,1,2 = one of the postPiles, 4 = blatzPile, 5 = woodPile
	//return true, if game is still going, false if game is over
	public boolean removeCard(int pileIndexFrom){
		if(pileIndexFrom == 5){
			woodPile.poll();
		}else{
			Card temp = blatzPile.pop();
			if(blatzPile.isEmpty()){
				return false;
			}
			if(pileIndexFrom < 4){
				postPiles[pileIndexFrom] = temp;
			}
		}
		return true;
	}
	
	public void flip(){
		Card temp;
		for(int i = 0; i < 3; i++){
			temp = woodPile.poll();
			woodPile.add(temp);
		}
	}
	
	public Card[] myCards(){
		Card[] temp = {woodPile.peek(), blatzPile.peek(), postPiles[0], postPiles[1], postPiles[2]};
		return temp;
	} 
	
}
