package com.cpsc441.project.dutchblitz.GameLogic;

public class Card {

	public static final int RED = 0;
	public static final int BLUE = 1;
	public static final int YELLOW = 2;
	public static final int GREEN = 3;
	public static final int GREY = 4;

	//Player id is 5 because there is a max of four real players
	public static final Card defaultCard = new Card(GREY, 0, 5);
	public int colour;
	public int value;
	int playerId;
	public Card(int c, int v, int id){
		colour = c;
		value = v;
		playerId = id;
	}
}
