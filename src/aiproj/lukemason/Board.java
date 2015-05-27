package aiproj.lukemason;
import aiproj.squatter.*;

import java.util.HashMap;

public class Board {

	//Board variables
	private int boardDims;
	private int[][] cells = null;
	private HashMap<Integer, Integer> dead = null;
	
	/** Creates a new Board object
	 * @param n board dimensions as an int
	 */
	public Board(int n) {
		
		//set the board dimensions
		setBoardDims(n);
		
		//Initialises the cells and dead arrays
		cells = new int[n][n];
		dead = new HashMap<>(n*n);

		//Fill the cells and deadcell arrays
		fillBoard();

	}

	/** Finds the current game state (the scores of each player) by searching
	 *  through the board
	 * @param board Data will be examined about this board
	 */
	public void state(Board board, LukeMason player) {
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
				} else if(board.getCells()[row][col]==(CustomPiece.DEADWHITE)){
					if(lastCol==CustomPiece.BLACK){
						player.setTallyB(player.getTallyB() + 1);
					}
					
				//Check captured black pieces
				} else if(board.getCells()[row][col]==(CustomPiece.DEADBLACK)){
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
	
		System.out.println("\n\nstarting to floodfill from :" + m.Row + ", " + m.Col);
		
		// check all the adjacent cells
		findNextUp(m.P, m.Row-1, m.Col);
		findNextDown(m.P, m.Row+1, m.Col);
		findNextLeft(m.P, m.Row, m.Col-1);
		findNextRight(m.P, m.Row, m.Col+1);
		
	}
	
	/** Looks at the above cell in the board and finds if it's dead
	 * @param colour the colour most recently placed (doing the capturing)
	 * @param row the row location in the board
	 * @param col the column location in the board
	 * @return whether or not this cell is dead (captured)
	 */
	public boolean findNextUp(int colour, int row, int col) {
		
		//if the row and col index is out of bounds
		if(row<0||col<0||row>=this.boardDims||col>=this.boardDims) {
			System.out.println("found boundary");
			return false;
		} // if its a cell capturing the current arrangement
		else if(this.cells[row][col] == colour) {
			System.out.println("found capturing cell at " + row + ", " + col);
			return true;
		} // check if cell has already been visited
		else if(dead.containsKey(row*this.boardDims + col)) {
			System.out.println("Already been here" + row + ", " + col);
			return true;
		}
		
		//if haven't been here, go here
		dead.put(row*this.boardDims + col, Piece.DEAD);
		
		if(!(findNextUp(colour, row-1, col))){
			dead.put(row*this.boardDims + col, Piece.INVALID);
			return false;
		}
		if(!(findNextLeft(colour, row, col-1))){
			dead.put(row*this.boardDims + col, Piece.INVALID);
			return false;
		}
		if(!(findNextRight(colour, row, col+1))){
			dead.put(row*this.boardDims + col, Piece.INVALID);
			return false;
		}
		
		return true;
	}
	
	/** Looks at the above cell in the board and finds if it's dead
	 * @param colour the colour most recently placed (doing the capturing)
	 * @param row the row location in the board
	 * @param col the column location in the board
	 * @return whether or not this cell is dead (captured)
	 */
	public boolean findNextDown(int colour, int row, int col) {
		
		//if the row and col index is out of bounds
		if(row<0||col<0||row>=this.boardDims||col>=this.boardDims) {
			System.out.println("found boundary");
			return false;
		} // if its a cell capturing the current arrangement
		else if(this.cells[row][col] == colour) {
			System.out.println("found capturing cell at " + row + ", " + col);
			return true;
		} // check if cell has already been visited
		else if(dead.containsKey(row*this.boardDims + col)) {
			System.out.println("Already been here" + row + ", " + col);
			return true;
		}
		
		//if haven't been here, go here
		dead.put(row*this.boardDims + col, Piece.DEAD);
		
		if(!(findNextDown(colour, row+1, col))){
			dead.put(row*this.boardDims + col, Piece.INVALID);
			return false;
		}
		if(!(findNextLeft(colour, row, col-1))){
			dead.put(row*this.boardDims + col, Piece.INVALID);
			return false;
		}
		if(!(findNextRight(colour, row, col+1))){
			dead.put(row*this.boardDims + col, Piece.INVALID);
			return false;
		}
		
		return true;
	}
	
	/** Looks at the above cell in the board and finds if it's dead
	 * @param colour the colour most recently placed (doing the capturing)
	 * @param row the row location in the board
	 * @param col the column location in the board
	 * @return whether or not this cell is dead (captured)
	 */
	public boolean findNextLeft(int colour, int row, int col) {
		
		//if the row and col index is out of bounds
		if(row<0||col<0||row>=this.boardDims||col>=this.boardDims) {
			System.out.println("found boundary");
			return false;
		} // if its a cell capturing the current arrangement
		else if(this.cells[row][col] == colour) {
			System.out.println("found capturing cell at " + row + ", " + col);
			return true;
		} // check if cell has already been visited
		else if(dead.containsKey(row*this.boardDims + col)) {
			System.out.println("Already been here" + row + ", " + col);
			return true;
		}
		
		//if haven't been here, go here
		dead.put(row*this.boardDims + col, Piece.DEAD);
		
		if(!(findNextLeft(colour, row, col-1))){
			dead.put(row*this.boardDims + col, Piece.INVALID);
			return false;
		}
		if(!(findNextUp(colour, row-1, col))){
			dead.put(row*this.boardDims + col, Piece.INVALID);
			return false;
		}
		if(!(findNextDown(colour, row+1, col))){
			dead.put(row*this.boardDims + col, Piece.INVALID);
			return false;
		}
		
		return true;
	}
	
	/** Looks at the above cell in the board and finds if it's dead
	 * @param colour the colour most recently placed (doing the capturing)
	 * @param row the row location in the board
	 * @param col the column location in the board
	 * @return whether or not this cell is dead (captured)
	 */
	public boolean findNextRight(int colour, int row, int col) {
		
		//if the row and col index is out of bounds
		if(row<0||col<0||row>=this.boardDims||col>=this.boardDims) {
			System.out.println("found boundary");
			return false;
		} // if its a cell capturing the current arrangement
		else if(this.cells[row][col] == colour) {
			System.out.println("found capturing cell at " + row + ", " + col);
			return true;
		} // check if cell has already been visited
		else if(dead.containsKey(row*this.boardDims + col)) {
			System.out.println("Already been here" + row + ", " + col);
			return true;
		}
		
		//if haven't been here, go here
		dead.put(row*this.boardDims + col, Piece.DEAD);
		
		if(!(findNextRight(colour, row, col+1))){
			dead.put(row*this.boardDims + col, Piece.INVALID);
			return false;
		}
		if(!(findNextUp(colour, row-1, col))){
			dead.put(row*this.boardDims + col, Piece.INVALID);
			return false;
		}
		if(!(findNextDown(colour, row+1, col))){
			dead.put(row*this.boardDims + col, Piece.INVALID);
			return false;
		}
		
		
		return true;
	}
	
	/** updates the board data to include the newly captured (dead) cells */
	public void update(){
		int cell_is_dead = 0;
		int[][] cells = getCells();
		
	
		for(int i=1; i<this.boardDims-1; i++) {
			for(int j =1; j<this.boardDims-1; j++) {
				
				//check if the cell is dead
				cell_is_dead = dead.getOrDefault(i*this.boardDims + j, Piece.INVALID);
				
				// if its dead, change the board
				if(cell_is_dead == Piece.DEAD) {
					if(cells[i][j]==Piece.WHITE) {
						cells[i][j] = CustomPiece.DEADWHITE;
					} else if(cells[i][j]==Piece.BLACK) {
						cells[i][j] = CustomPiece.DEADBLACK;
					} else if(cells[i][j]==Piece.EMPTY) {
						cells[i][j] = CustomPiece.DEADSPACE;
					}
				}	
			}
		}
	}

	/** Prints the board configuration to standard output */
	public void printBoard(){
		int cell = 0;
		for(int i=0; i<this.boardDims; i++){
			for(int j=0; j<this.boardDims; j++){
				cell = getCells()[i][j];
				switch(cell) {
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
					System.out.print("Invalid piece on board.  terminating");
					System.exit(0);
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