package aiproj.random;
import aiproj.squatter.*;

import java.util.concurrent.ConcurrentHashMap;

public class Board {

	//Board variables
	private int boardDims;
	private int[][] cells = null;
	private ConcurrentHashMap<Integer, Integer> dead = null;
	
	/** Creates a new Board object
	 * @param n board dimensions as an int
	 */
	public Board(int n) {
		
		//set the board dimensions
		setBoardDims(n);
		
		//Initialises the cells and dead arrays
		cells = new int[getBoardDims()][getBoardDims()];
		dead = new ConcurrentHashMap<>((this.getBoardDims()-2)*(this.getBoardDims()-2));

		//Fill the cells and deadcell arrays
		fillBoard();

	}

	/** Finds the current game state (the scores of each player) by searching
	 *  through the board
	 * @param board Data will be examined about this board
	 */
	public void state(Board board, Random player) {
		int lastCol = 0;
		
		//Update whether or not the game is over
		player.setGameOver(checkGameOver(board));
		
		//Search for a captured point
		//Skip cells on bottom & right edges
		for(int row = 0; row < (board.getBoardDims()-1); row++){
			for(int col = 0; col < (board.getBoardDims()-1); col++){
				
				//Check points based on captured piece in each cell
				//Check captured spaces
				if (board.getCells()[row][col]==(CustomPiece.DEADSPACE)) {
					
					//Add to tally
					if(lastCol==CustomPiece.BLACK){
						player.setTallyB(player.getTallyB() + 1);
					} else {
						player.setTallyW(player.getTallyW() + 1);
					}	
					
				//Check captured white pieces
				} else if (board.getCells()[row][col]==(CustomPiece.DEADWHITE)) {
					if(lastCol==CustomPiece.BLACK){
						player.setTallyB(player.getTallyB() + 1);
					}
					
				//Check captured black pieces
				} else if (board.getCells()[row][col]==(CustomPiece.DEADBLACK)) {
					if(lastCol==CustomPiece.WHITE){
						player.setTallyW(player.getTallyW() + 1);
					}
					
				//If not captured, set to latest colour
				} else {
					lastCol = board.getCells()[row][col];
				}
			}
		}
	}
	
	/** Fills the initialised board with empty cells */
	public void fillBoard() {
		
		// initialise board elements to empty
		for(int row=0;row<getBoardDims(); row++){
			for(int col=0;col<getBoardDims(); col++){
				cells[row][col] = Piece.EMPTY;
			}
		}
		
	}

	/** Checks if the current board state satisfys an end game condition 
	 * @param board The board from which the game state is checked
	 * @return A boolean for whether the game has ended or not
	 */
	public boolean checkGameOver(Board board){
		
		for(int row = 0; row < this.getBoardDims(); row++){
			for(int col = 0; col < this.getBoardDims(); col++){
				
				// game state isn't over because an empty piece is found
				if (this.cells[row][col]==Piece.EMPTY) {
					return false;
				}
			}
		}
		//game state is over
		return true;
	}

	/** Returns the results of the game
	 * @param gameOver Whether the game has finished or not
	 * @param tallyB How many cells Black player has claimed 
	 * @param tallyW How many cells White player has claimed
	 */
	public int returnWinner(Boolean gameOver, int tallyB, int tallyW){
		
		if(gameOver) {
			if(tallyB==tallyW) {
				return Piece.DEAD;
			} else if(tallyB>tallyW) {
				return Piece.BLACK;
			} else {
				return Piece.WHITE;
			}
		} else {
			return Piece.EMPTY;
		}
	}

	/** Finds the dead cells and stores their location in dead
	 * @param row row location of current cell being analysed
	 * @param col column location of current cell being analysed
	 * @param colour colour of the boundary (capturing) cells
	*/
	public void floodfill(Move m){
		dead.clear();
		
		int colour = m.P;
		int row = m.Row;
		int col = m.Col;
			
		// check all the adjacent cells
		findNext(colour, row-1, col);
		findNext(colour, row+1, col);
		findNext(colour, row, col+1);
		findNext(colour, row, col-1);
		
	}
	
	/** Looks at the next cell in the board and recursively finds it's dead
	 * @param colour the colour most recently placed (doing the capturing)
	 * @param row the row location in the board
	 * @param col the column location in the board
	 * @return whether or not this cell is dead (captured)
	 */
	public boolean findNext(int colour, int row, int col) {
		
		// checks if the cell is out of bounds
		if(row<0|col<0|row>=this.boardDims|col>=this.boardDims) {
			return false;
		} // check if this cell has been visited already
		else if (dead.containsKey((row-1)*(this.boardDims-2)+(col-1))){
			return true;
		}
		// check this cell of as visited (assume dead first)
		dead.put((row-1)*(this.boardDims-2)+(col-1), 0);
		
		// check if this cell one of the capturing cells (a boundary)
		if (getCells()[row][col] == colour) {
			return true;
		}
		
		// check the surrounding cells
		boolean up = findNext(colour, row-1, col);
		boolean right = findNext(colour, row, col+1);
		boolean left = findNext(colour, row, col-1);
		boolean down = findNext(colour, row+1, col);
		
		// cell is captured
		if (up & down & left & right) {
			return true;
		}
		
		// isn't captured, change from visited to dead
		dead.put((row-1)*(this.boardDims-2)+(col-1), -1);
		return false;
	}
	
	/** updates the board data to include the newly captured (dead) cells */
	public void updateDead(){
	
		for(int i=1; i<getCells().length-1; i++) {
			for(int j =1; j<getCells().length-1; j++) {
				
				// check is the piece was visited by the floodfill
				int tmp = dead.getOrDefault((i-1)*(getCells().length-2)+(j-1), -1);
				
				// if it was visited, mark it as captured
				if(tmp != Piece.INVALID) {
					if(getCells()[i][j]==Piece.WHITE) {
						getCells()[i][j] = CustomPiece.DEADWHITE;
					} else if(getCells()[i][j]==Piece.BLACK) {
						getCells()[i][j] = CustomPiece.DEADBLACK;
					} else if(getCells()[i][j]==Piece.EMPTY) {
						getCells()[i][j] = CustomPiece.DEADSPACE;
					}
				}	
			}
		}
	}

	/** Prints the board configuration to standard output */
	public void printBoard(){
		int dims = this.boardDims;
		for(int i=0; i<dims; i++){
			for(int j=0; j<dims; j++){
				int tmp = getCells()[i][j];
				switch(tmp) {
				case Piece.EMPTY:
					System.out.print("+");
					break;
				case Piece.WHITE:
					System.out.print("W");
					break;
				case Piece.BLACK:
					System.out.print("B");
					break;
				case CustomPiece.DEADSPACE:
					System.out.print("-");
					break;
				case CustomPiece.DEADWHITE:
					System.out.print("w");
					break;
				case CustomPiece.DEADBLACK:
					System.out.print("b");
					break;
				case Piece.INVALID:
					System.out.print("Invalid board data. Probs Check that"); 	//Need to sort out when an invalid piece of data would get as far as this method
					break;
				}
			}
		System.out.print("\n");
		}
	}

	public int getBoardDims() {
		return boardDims;
	}

	public void setBoardDims(int boardDims) {
		this.boardDims = boardDims;
	}

	public int[][] getCells() {
		return this.cells;
	}

	public void setCells(int[][] cells) {
		this.cells = cells;
	}
	
}