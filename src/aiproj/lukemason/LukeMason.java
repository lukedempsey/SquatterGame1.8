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
	
	//minimax states
	public final int MIN = 0;
	public final int MAX = 1;
	public final double NEGINF = -9999;
	public final double POSINF = 9999;
	
	@Override
	public int init(int n, int p) {

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

	
	public int getOtherPlayer(int player){
		if (player==1){
			return 2;
		}
		return 1;
	}
	
	@Override
	public Move makeMove() {

		/**TODO - This method is called by the referee to request a move by your player. 
		 * Based on the current board configuration, your player should select its next 
		 * move and return it as an object of the aiproj.squatter.Move class. 
		 * Note that each player needs to maintain its own internal state representation 
		 * of the current board configuration
		 */
		Move move = new Move();
		
		//This is just here for testing
		//minimax(board, this.depth, true);
		
		
			System.out.println("Minimax started!");
			move = minimax(board, this.depth, true, 0).getMove();
		
		
		
		//System.out.println("first depth: "+this.depth);
		
		
		
		//System.out.println("MOve"+move.P);
		
		
		/**int[][]cells = board.getCells();
		
		//Dumb play
		for(int i=0; i<board.getBoardDims(); i++){
			for(int j=0; j<board.getBoardDims(); j++){
				if (cells[i][j]==Piece.EMPTY){
					Move move = new Move();
					move.Row = i;
					move.Col = j;
					move.P = this.playerColour;	
<<<<<<< Updated upstream
					currentBoard[i][j] = this.playerColour;
					board.setCells(currentBoard);
					// check for newly captured cells
					board.floodfill(move);
<<<<<<< HEAD
					board.update();
=======
					board.updateDead();
=======
					cells[i][j] = this.playerColour;
					board.setCells(cells);
>>>>>>> Stashed changes
>>>>>>> origin/master
					return move;
				}
			}
		}*/
		
		board.placeMove(board, move);
		
		return move;
	}

	/**Check a move will be valid */
	public boolean checkMove(Move m){
		int[][] currentBoard = board.getCells();

		// check if move is valid
		if (currentBoard[m.Row][m.Col] != Piece.EMPTY || getGameOver()==true || 
				m.Row >= board.getBoardDims() || 
				m.Col >= board.getBoardDims()) {
			////System.out.println("heree");
			// move is invalid
			return false;
		}
		return true;
	}
	
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
		board.floodfill(m);
		board.update();
		
		return 0;
	}

	@Override
	public int getWinner() {

		//Update game state
		board.state(board, this);
		
		//was this.tally but changed it because no method was updating it
		return board.returnWinner(gameOver, board.getTallyB(), board.getTallyW());
	}
	
	@Override
	public void printBoard(PrintStream output) {
		board.printBoard();
	}
	

		
	public double utility(Board board){
		if (board.getTallyW()<board.getTallyB()){
			if (this.getPlayerColour() == board.getTallyW()){
				return -100;
			}else{
				return 100;
			}

		}else{
			if (this.getPlayerColour() == board.getTallyW()){
				return 100;
			}else{
				return -100;
			}
		}
	}
	
	public Minimax minimax(Board board, int depth, boolean max, double cur_optimal){
		double curVal = Double.NaN;
		double val;
		int dims = board.getBoardDims();
		Move optimalMove = null;
		
		//TODO add end game functionality (util)
		
		if (depth==0){
			return new Minimax(heuristic(board));
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
					tmpBoard.fillBoard(board.getCells(), board.getDead());
					tmpBoard.placeMove(tmpBoard, move);
					
					//Call minimax
					if (max){
						val = minimax(tmpBoard, depth-1, false, curVal).getValue();
						
						//if the value is better, update it
						if(optimalMove==null || val>curVal){
							curVal = val;
							optimalMove = move;
						}
					}else{
						val = minimax(tmpBoard, depth-1, true, curVal).getValue();
						
						//if the value is better, update it
						if(optimalMove==null || val<curVal){
							curVal = val;
							optimalMove = move;
						}
					}
				}
				
			}
		}
		return new Minimax(optimalMove, curVal);
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
	
	
	
	public double heuristic(Board board){
		double h = 0;
		h+=liberties(board);
		h+=aliveDead(board);
		//System.out.println("$$$Heuristic:  "+h);
		return h;
	}
	

	
	/**
	 * Calculates the alive/dead factor
	 * @return 
	 */
	public int aliveDead(Board board){
		int white = board.getTallyW();
		int black = board.getTallyB();
		
		if (this.getPlayerColour()==Piece.WHITE){
			return white - black;
		}else{
			return black - white;
		}
	}
	
	/**
	 * finds the amount of adjacent real-estate for each player
	 * @param board
	 * @param player
	 * @return current players real-estate minus the opponents
	 */
	public int liberties(Board board){
		int[][] cells = board.getCells();
		int dim = board.getBoardDims();
		
		int lib_player = 0;
		int lib_opponent = 0;
		
		//Count liberties
		for(int i=0; i<dim; i++){
			for(int j=0; j<dim; j++){
				//Find an occupied living cell
				if(cells[i][j]==Piece.WHITE || cells[i][j]==Piece.BLACK){
					//check adjacent
					
					for(int d_i=-1;d_i<=1;d_i++){
						for(int d_j=-1; d_j <=1; d_j++){
							try{
								if(cells[i+d_i][j+d_j]==Piece.EMPTY){	
									if(cells[i][j]==this.getPlayerColour()){
										lib_player++;
									}else if(cells[i][j]==this.getOpponentColour()){
										lib_opponent++;
									}else{
										//TODO change this
										//System.out.println("ERROR: cell is neither player or opponent colour");
										//System.out.println(cells[i][j]);
										//System.out.println(player.getPlayerColour());
									}
								}
							}catch (ArrayIndexOutOfBoundsException e){
							} finally {}
						}
					}
				}	
			}
		}
		//System.out.println(lib_player);
		//System.out.println(lib_opponent);
		return lib_player - lib_opponent;
	}

	public int stoneConnect(){
		//TODO
		
		return 0;
	}
}

	
	
	


