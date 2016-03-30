package com.cpsc441.project.dutchblitz.GameLogic;

public class Card {

	public String colour;
	public int value;
	int playerId;
	public Card(String c, int v, int id){
		colour = c;
		value = v;
		playerId = id;
	}
}
