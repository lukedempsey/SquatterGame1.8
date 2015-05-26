package aiproj.random;
import aiproj.squatter.*;

import java.io.PrintStream;

/**
 *  v
 * @author lukedempsey 638407, mason rose-campbell 638370
 *
 */

public class Random implements Player, Piece {
	
	//private static final Boolean true = null;

	//Initialises game objects
	private static Board board;
	
	//Initialises game variables
	private static Boolean debug = false;
	public int getPlayerColour() {
		return playerColour;
	}

	
	private int playerColour;
	private int opponentColour;
	
	private static int tallyB = 0;
	private static int tallyW = 0;
	private static Boolean gameOver = false;

	
	
	@Override
	public int init(int n, int p) {
		/** TODO - This method is called by the referee to initialise the player. 
		The input parameters are as follows: n specifies the board dimension,
		and p specifies the piece that the player will use (according to the 
		Piece interface format) as assigned to your class by the referee. 
		Your implementation of this function should return a negative value 
		if it does not initialise successfully.
		
		********I think this is all sorted************/
		
		board = new Board(n);

		if(p==1){
			setPlayerColour(Piece.WHITE);
			setOpponentColour(Piece.BLACK);
		}else if(p==2){
			setPlayerColour(Piece.BLACK);
			setOpponentColour(Piece.WHITE);
		}else{
			//invalid input p != 1 or 2
			return -1;
		}
		return 0;
	}
	
	public int[] makeRandMove(){
		
		
		int[][] currentBoard = board.getCells();
		int dim = board.getBoardDims();
		int move_coords[] = {0,0};
		
		int i = (int) Math.ceil((dim-1)*Math.random());
		int j = (int) Math.ceil((dim-1)*Math.random());
		
		if (currentBoard[i][j]!=Piece.EMPTY){
			return makeRandMove();
		}
		
		move_coords[0] =i ;
		move_coords[1] = j;
		
		return move_coords;

		
	}

	@Override
	public Move makeMove() {
		/**TODO - This method is called by the referee to request a move by your player. 
		 * Based on the current board configuration, your player should select its next 
		 * move and return it as an object of the aiproj.squatter.Move class. 
		 * Note that each player needs to maintain its own internal state representation 
		 * of the current board configuration
		 */
		
		/** WILL SOMETIMES HAVE A STACK OVERFLOW DEPENDING TO HOW THE GAME GOES 
		 * AS IT TRIES TO FIND THE LAST PLACE TO MAKE A MOVE, CBF CHANGING, THIS IS ONLY FOR TESTS
		 */
		
		Move move = new Move();
		
		int dim = board.getBoardDims();
		int[][] currentBoard = board.getCells();
		int[] tmp_move;
		
		if (Random.getGameOver()){
			return null;
		}
		
		tmp_move = makeRandMove();
		
			move.Row = tmp_move[0];
			move.Col = tmp_move[1];
			move.P = this.playerColour;
			currentBoard[tmp_move[0]][tmp_move[1]] = this.playerColour;
			board.setCells(currentBoard);
			return move;

	}

	@Override
	public int opponentMove(Move m) {
		/**
		 This method is called by the referee to inform your player about the 
		 opponent's most recent move, so that you can maintain your board configuration. 
		 The input parameter is an object from the class aiproj.squatter.
		 Move, which includes the information about the last move made by the opponent. 
		 Based on your board configuration, if your player thinks this move is illegal 
		 you need to return -1 otherwise this function should return 0.
		 */
		int row = m.Row;
		int col = m.Col;
		int piece = m.P;
		
		int[][] currentBoard = board.getCells();
		
		//check legal move
		//TODO is this the only illegal move? 
		//Account for captured territories
		//Account for wrong colour placed
		//TODO account for suicidal move
		if (currentBoard[row][col] != Piece.EMPTY | piece!= getOpponentColour() | getGameOver()==true){
			Random.setGameOver(true);
			return -1;
		}
		
		//add move
		currentBoard[row][col] = piece;
		
		//change board
		board.setCells(currentBoard);
		
		
		
		return 0;
	}

	@Override
	public int getWinner() {
		/**This method should check the board configuration for a possible winner and return 
		 * the winner as an integer value according to the Piece interface 
		 * (-1=INVALID, 0=EMPTY, 1=WHITE, 2=BLACK, 3=DEAD). 
		 * Note that EMPTY corresponds to a non-terminal state of the game, 
		 * DEAD corresponds to a draw, and INVALID corresponds to the case that 
		 * the game has ended because the opponent move was illegal 
		 * (e.g., placing a piece on an already occupied or captured cell).
		*/
		
		//Update game state
		Board.state(debug, board, gameOver);
		return Board.returnState(gameOver, tallyB, tallyW);

	}

	@Override
	public void printBoard(PrintStream output) {
		/**This method is called by the referee to ask your player to print its 
		 * board configuration to a PrintStream object output. Note that this 
		 * output will be similar to the format specified in Project Part A, 
		 * i.e., we would suggest using B, W, or +, corresponding to a Black piece still in play, 
		 * a White piece still in play, or a free cell, respectively, and b, w, or -, 
		 * corresponding to a captured Black piece, a captured White piece, or a captured empty cell, 
		 * respectively.
		*/
		Board.printBoard(board);
		Heuristic.liberties(board, this);
	}
	
	public static int getTallyB() {
		return tallyB;
	}
	
	public static void setTallyB(int tallyB) {
		Random.tallyB = tallyB;
	}
	
	public static int getTallyW() {
		return tallyW;
	}
	
	public static void setTallyW(int tallyW) {
		Random.tallyW = tallyW;
	}
	
	public static Boolean getGameOver() {
		return gameOver;
	}
	
	public static void setGameOver(Boolean gameOver) {
		Random.gameOver = gameOver;
	}
	
	public void setPlayerColour(int playerColour) {
		this.playerColour = playerColour;
	}

	public int getOpponentColour() {
		return opponentColour;
	}

	public void setOpponentColour(int opponentColour) {
		this.opponentColour = opponentColour;
	}

}
