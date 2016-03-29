import java.util.Stack;


public class GameTable {
	private int NUMBER_OF_CANADIAN_PILES = 12;
	private int MAX_NUMBER_OF_CARDS = 10;
	Card[][] canadianPiles;
	MyPiles me;
	Opponent[] opposition;

	
	
	//Will need server to pass number of players, id, and oppenents cards
	public GameTable(int numberOfPlayers, int id){
		canadianPiles= new Card[NUMBER_OF_CANADIAN_PILES][MAX_NUMBER_OF_CARDS];
		me = new MyPiles();
		opposition = new Opponent[numberOfPlayers-1];
		me.playerId = id;		
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
			for(int j = 0; j < MAX_NUMBER_OF_CARDS; j++){
				if (canadianPiles[i][j] != null){
					if(canadianPiles[i][j].playerId == me.playerId){
						total++;
					}
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

}
