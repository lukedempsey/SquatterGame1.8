package aiproj.random;
import aiproj.squatter.*;

import java.io.PrintStream;

/**
 * @author lukedempsey 638407, mason rose-campbell 638370
 *
 */

public class Random implements Player, Piece {
	
	//Initialises game objects
	private Board board;
	
	//Initialises game variables
	public int getPlayerColour() {
		return playerColour;
	}

	
	private int playerColour;
	private int opponentColour;
	
	private int tallyB = 0;
	private int tallyW = 0;
	private Boolean gameOver = false;

	
	
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

	/**Sometimes stackoverflows, wont worried because only for testing */
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
		
		Move move = new Move();
		int[][] currentBoard = board.getCells();
		int[] tmp_move;
		
		if (this.getGameOver()){
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
			this.setGameOver(true);
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
		board.state(board, this);
		return board.returnWinner(gameOver, tallyB, tallyW);

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
		board.printBoard();
		Heuristic.liberties(board, this);
	}
	
	public int getTallyB() {
		return tallyB;
	}
	
	public void setTallyB(int tallyB) {
		this.tallyB = tallyB;
	}
	
	public int getTallyW() {
		return tallyW;
	}
	
	public void setTallyW(int tallyW) {
		this.tallyW = tallyW;
	}
	
	public Boolean getGameOver() {
		return gameOver;
	}
	
	public void setGameOver(Boolean gameOver) {
		this.gameOver = gameOver;
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
