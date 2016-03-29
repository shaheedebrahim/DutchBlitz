import java.lang.reflect.Array;


public class Opponent {
	private Card[] oppCards;
	
	public Opponent(Card[] startingCards){
		oppCards = new Card[4];
		for(int i = 0; i < oppCards.length; i++){
			oppCards[i]= startingCards[i];
		}
	}
	
	public void updateOpp(Card c, int index){
		oppCards[index] = c;
	}
	
}
