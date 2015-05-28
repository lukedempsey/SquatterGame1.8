package aiproj.lukemason;
import aiproj.squatter.*;

import java.io.PrintStream;

/**
 * @author lukedempsey 638407, mason rose-campbell 638370
 *
 */

public class LukeMason implements Player, Piece {

	
	//Initialises game objects
	protected Board board;
	
	//Initialises game variables
	protected int playerColour;
	protected int opponentColour;
	
	//Default foresight is two moves
	protected int depth = 3;
	
	private int tallyB = 0;
	private int tallyW = 0;
	private Boolean gameOver = false;	
	
	/**Initialise the player
	 * @param n Board Dimensions
	 * @param player integer
	 * 
	 * if invalid input, return -1
	 */
	@Override
	public int init(int n, int p) {

		board = new Board(n);
		
		alterDepth(n);

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
	
	/**
	 * Smart depth allocation for board sizes to cope with minimax
	 * @param dim
	 */
	public void alterDepth(int dim){
		if (dim<=6){
			setDepth(3);
		}else if (dim>=7){
			setDepth(2);
		}else {
			setDepth(1);
		}
		
	}

	/**
	 * Return the integer corresponding the other player
	 * @param player - integer corresponding to current player
	 * @return
	 */
	public int getOtherPlayer(int player){
		if (player==1){
			return 2;
		}
		return 1;
	}
	
	/**
	 * Performs a move of best choice by running through minimax and
	 * comparing heuristic values for different outcomes
	 * return move object
	 */
	@Override
	public Move makeMove() {
		Move move = new Move();
		move = minimax(board, this.depth, true, 0).getMove();
		board.placeMove(board, move);
		return move;
	}

	/**
	 * Checks that an intended move is legal
	 * @param m - Move object describing intended move
	 * @return True if legal, False if illegal
	 */
	public boolean checkMove(Move m){
		int[][] currentBoard = board.getCells();

		// check if move is valid
		if (currentBoard[m.Row][m.Col] != Piece.EMPTY || getGameOver()==true || 
				m.Row >= board.getBoardDims() || 
				m.Col >= board.getBoardDims()) {
			// move is invalid
			return false;
		}
		return true;
	}
	
	/**
	 * Checks legality of opponent's move. If move illegal return -1.
	 * If legal, add to our board.
	 * @param m - Opponents move
	 */
	@Override
	public int opponentMove(Move m) {				
		int[][] currentBoard = board.getCells();
		
		// check if move is valid
		if (checkMove(m)==false) {	
			// move is invalid
			return -1;
		}
		
		// otherwise add the move to the board
		currentBoard[m.Row][m.Col] = m.P;
		
		// update the board
		board.setCells(currentBoard);
		
		// check for newly captured cells
		board.placeMove(board, m);
		
		return 0;
	}

	/**
	 * Update the game state
	 * @return - winner
	 */
	@Override
	public int getWinner() {

		//Update game state
		board.state(board, this);
		
		//was this.tally but changed it because no method was updating it
		return board.returnWinner(gameOver, this.getTallyB(),
				this.getTallyW());
	}
	
	/**
	 * Print the board's contents to output
	 * @param output - where to print the contents to
	 */
	@Override
	public void printBoard(PrintStream output) {
		board.printBoard(output);
	}
		
	/**
	 * Utility function for when the minimax gets to an end of
	 * game scenario
	 * @param board
	 * @return utility value
	 */
	public double utility(Board board){
		board.state(board, this);
		
		if (this.getTallyW()<this.getTallyB()){
			if (this.getPlayerColour() == this.getTallyW()){
				return -100;
			}else{
				return 100;
			}

		}else{
			if (this.getPlayerColour() == this.getTallyW()){
				return 100;
			}else{
				return -100;
			}
		}
	}
	
	/**
	 * Binary tree used for evaluating different moves and their effect
	 * on the game.
	 * @param board - the current node in the tree. At first call, this will
	 * be the player's board
	 * @param depth - max depth to search. Eg: depth of 2 will look ahead
	 * 2 moves
	 * @param max - corresponding to the state of the level we are currently
	 * at in the tree Min, or Max. At first call, this will be Max
	 * @param cur_optimal - The value of the current best move which can be
	 * compared to by a new move
	 * @return
	 */
	public Minimax minimax(Board board, int depth, boolean max,
			double cur_optimal){
		double curVal = Double.NaN; //initialise currentvalue
		double val;
		int dims = board.getBoardDims();
		Move optimalMove = null;
		
		if (board.checkGameOver(board)){
			Minimax util = new Minimax(utility(board));
			return util;
		}
		
		if (depth==0){
			Minimax heuristic = new Minimax(heuristic(board));
			return heuristic;
		}
		
		//check for legal moves in all cells
		for(int i=0; i<dims; i++){
			for(int j=0; j<dims;j++){
				//Create move
				Move move = new Move();
				move.Row = i;
				move.Col = j;
				if (max){
					move.P = this.getPlayerColour();
				}else{
					move.P = this.getOpponentColour();
				}
				
				//Check if move is legal
				if(checkMove(move)){
					
					//Clone board and play move
					Board tmpBoard = new Board(board.getBoardDims());
					tmpBoard.fillBoard(board.getCells(board), board.getDead());
					tmpBoard.placeMove(tmpBoard, move);
					
					//Call minimax
					if (max){
						//return value of next level of simulation
						val = minimax(tmpBoard, depth-1, false, curVal)
								.getValue();
						
						//if the value is better, update it
						if(optimalMove==null || val>curVal){
							curVal = val;
							optimalMove = move;
						}
					}else{
						val = minimax(tmpBoard, depth-1, true, curVal)
								.getValue();
						
						//if the value is better, update it
						if(optimalMove==null || val<curVal){
							curVal = val;
							optimalMove = move;
						}
					}
				}
				
			}
		}
		Minimax out = new Minimax(optimalMove, curVal);
		return out;
	}
	
	/**
	 * Returns a weight for the heuristic function based on adjacent cells 
	 * @param row location of row to search from
	 * @param col location of column to search from
	 * @param colour colour of start cell
	 * @param cells cells of the board to search in
	 * @return
	 */
	public double adjacentSpace(int row, int col, int colour, int[][] cells){
		double playerSpace = 0.0;
		
		//checking adjacent cells
		for(int adj_r=-1; adj_r<1; adj_r++){
			for(int adj_c=-1; adj_c<1; adj_c++){
				if(colour==Piece.WHITE){
					if (cells[row+adj_r][col+adj_c] == Piece.EMPTY ||
							cells[row+adj_r][col+adj_c] == Piece.WHITE){
						playerSpace += 0.5;
					}
				} else {
					if (cells[row+adj_r][col+adj_c] == Piece.EMPTY ||
							cells[row+adj_r][col+adj_c] == Piece.BLACK){
						playerSpace += 0.5;
					}
				}
			
			}
		}
		return playerSpace;
	}
	
	/**
	 * Calculates the value of the move given heuristic formulae
	 * @param thisBoard - Board containing updated move to be evaluated
	 * @return heuristic - Value of board
	 */
	public double heuristic(Board thisBoard){
		double h = 0;
		int dim = thisBoard.getBoardDims();
		int[][]cells = thisBoard.getCells(thisBoard);
		int ourColour = this.getPlayerColour();
		int theirColour = this.getOpponentColour();
		int playerDead=0, opponentDead=0, spaceDead=0;
		double playerSpace=0, opponentSpace=0;
		double playerDiags=0, opponentDiags=0;
		
		
		for (int i=1; i<dim-1; i++){
			for(int j=1; j<dim-1; j++){
				
				//Dead cells
				if (cells[i][j] == CustomPiece.DEADWHITE){
					if (ourColour==Piece.WHITE){
						playerDead++;
						//System.out.println("KILLED");
					}else{
						opponentDead++;
					}
				}else if (cells[i][j] == CustomPiece.DEADBLACK){
					if (ourColour==Piece.BLACK){
						playerDead++;
					}else{
						opponentDead++;
						//System.out.println("KILL");
					}
				}else if (cells[i][j] == Piece.DEAD){
					//captured empty cells
					if(adjacentSpace(i,j,ourColour, cells)>0){
						spaceDead++;
					}
				}
				
				//Diagonals:
				if(cells[i][j] == ourColour){
					for(int x=-1; x<=1; x+=2){
						for(int y=-1; y<=1; y+=2){
							if (cells[i+x][j+y]==ourColour){
								playerDiags++;
							} else if(cells[i+x][j+y] == theirColour){
								opponentDiags++;
							}
						}
					}
				}
				
				//how much space player has
				if(cells[i][j]==ourColour){
					playerSpace = adjacentSpace(i, j, ourColour, cells);
				} else if (cells[i][j]==theirColour){
					opponentSpace = adjacentSpace(i, j, theirColour, cells);
				}
				
				
			}
		}
		
		h+=19*playerDiags;
		h-=20*opponentDiags;
		h+=50*opponentDead;
		h-=50*playerDead;
		h+=40*spaceDead;
		h+=5*playerSpace;
		h-=7*opponentSpace;
		//System.out.println("\n" +  playerDiags + "\n");
		return h;
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
	
	public int getPlayerColour() {
		return playerColour;
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
	
	public void setDepth(int depth){
		this.depth = depth;
	}
	
}

	
	
	


