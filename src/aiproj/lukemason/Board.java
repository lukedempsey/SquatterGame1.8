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
				} else if(board.getCells()[row][col]==(CustomPiece.DEADWHITE))
					{
					if(lastCol==CustomPiece.BLACK){
						player.setTallyB(player.getTallyB() + 1);
					}
					
				//Check captured black pieces
				} else if(board.getCells()[row][col]==(CustomPiece.DEADBLACK))
					{
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
	
	/** Finds current tally for White player
	 * @return tally of white pieces
	 */
	public int getTallyW(){
		int tally = 0;
		int[][]cells = this.getCells();
		for(int i=0; i<this.getBoardDims(); i++){
			for(int j=0; j<this.getBoardDims(); j++){
				if (cells[i][j]==Piece.WHITE){
					tally++;
				}
			}
		}
		return tally;
	}
	
	/** Finds current tally for Black player
	 * @return tally of black cells
	 */
	public int getTallyB(){
		int tally = 0;
		int[][]cells = this.getCells();
		for(int i=0; i<this.getBoardDims(); i++){
			for(int j=0; j<this.getBoardDims(); j++){
				if (cells[i][j]==Piece.BLACK){
					tally++;
				}
			}
		}
		return tally;
	}
	
	/** Fills the initialised board cells inputted 
	 * @param contents board cells to copy into the new board
	 * @param deadIn dead cell hashmap to copy in
	 */
	public void fillBoard(int[][] contents, HashMap<Integer, Integer> deadIn){
		
		// initialise board elements to empty
		cells = contents;
		dead = deadIn;
		
	}
	
	/** Places a move on the board and updates it
	 * @param b Board being played on
	 * @param m Move most recently played
	 * @return b updated board
	 */
	public Board placeMove(Board b, Move m){
		int[][] localBoard = b.getCells();
		
		//introduce move
		localBoard[m.Row][m.Col] = m.P;
		
		//update board
		b.setCells(localBoard);
		b.floodfill(m);
		b.update(b);
		
		return b;
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
	 * @return the winner, or a draw or none if no winner
	 */
	public int returnWinner(Boolean gameOver, int tallyB, int tallyW){
		
		if(gameOver) {
			//Draw
			if(tallyB==tallyW) {
				return Piece.DEAD;
			} //Black wins
			else if(tallyB>tallyW) {
				return Piece.BLACK;
			} //White wins
			else {
				return Piece.WHITE;
			}
		// None
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
		
		// check all the adjacent cells
		findNextUp(m.P, m.Row, m.Col);
		findNextDown(m.P, m.Row, m.Col);
		findNextLeft(m.P, m.Row, m.Col);
		findNextRight(m.P, m.Row, m.Col);
	}
	
	/** Looks at the above cell in the board and finds if it's dead
	 * @param colour the colour most recently placed (doing the capturing)
	 * @param row the row location in the board
	 * @param col the column location in the board
	 * @return whether or not this cell is dead (captured)
	 */
	public boolean findNextUp(int colour, int row, int col) {
		row = row -1;
		//if the row and col index is out of bounds
		if(row<0||col<0||row>=this.boardDims||col>=this.boardDims) {
			return false;
		} // if its a cell capturing the current arrangement
		else if(this.cells[row][col] == colour) {
			return true;
		} // check if cell has already been visited
		else if(dead.containsKey(row*this.boardDims + col)) {
			return true;
		}
		
		//if haven't been here, go here
		dead.put(row*this.boardDims + col, Piece.DEAD);
		
		if(!(findNextUp(colour, row, col))){
			dead.clear();
			return false;
		}
		if(!(findNextLeft(colour, row, col))){
			dead.clear();
			return false;
		}
		if(!(findNextRight(colour, row, col))){
			dead.clear();
			return false;
		}
		
		return true;
	}
	
	/** Looks at the below cell in the board and finds if it's dead
	 * @param colour the colour most recently placed (doing the capturing)
	 * @param row the row location in the board
	 * @param col the column location in the board
	 * @return whether or not this cell is dead (captured)
	 */
	public boolean findNextDown(int colour, int row, int col) {
		row = row + 1;
		//if the row and col index is out of bounds
		if(row<0||col<0||row>=this.boardDims||col>=this.boardDims) {
			return false;
		} // if its a cell capturing the current arrangement
		else if(this.cells[row][col] == colour) {
			return true;
		} // check if cell has already been visited
		else if(dead.containsKey(row*this.boardDims + col)) {
			return true;
		}
		
		//if haven't been here, go here
		dead.put(row*this.boardDims + col, Piece.DEAD);
		
		if(!(findNextDown(colour, row, col))){
			dead.clear();
			return false;
		}
		if(!(findNextLeft(colour, row, col))){
			dead.clear();
			return false;
		}
		if(!(findNextRight(colour, row, col))){
			dead.clear();
			return false;
		}
		
		return true;
	}
	
	/** Looks at the cell to the left in the board and finds if it's dead
	 * @param colour the colour most recently placed (doing the capturing)
	 * @param row the row location in the board
	 * @param col the column location in the board
	 * @return whether or not this cell is dead (captured)
	 */
	public boolean findNextLeft(int colour, int row, int col) {
		col = col - 1;
		//if the row and col index is out of bounds
		if(row<0||col<0||row>=this.boardDims||col>=this.boardDims) {
			return false;
		} // if its a cell capturing the current arrangement
		else if(this.cells[row][col] == colour) {
			return true;
		} // check if cell has already been visited
		else if(dead.containsKey(row*this.boardDims + col)) {
			return true;
		}
		
		//if haven't been here, go here
		dead.put(row*this.boardDims + col, Piece.DEAD);
		
		if(!(findNextLeft(colour, row, col))){
			dead.clear();
			return false;
		}
		if(!(findNextUp(colour, row, col))){
			dead.clear();
			return false;
		}
		if(!(findNextDown(colour, row, col))){
			dead.clear();
			return false;
		}
		
		return true;
	}
	
	/** Looks at the cell to the right in the board and finds if it's dead
	 * @param colour the colour most recently placed (doing the capturing)
	 * @param row the row location in the board
	 * @param col the column location in the board
	 * @return whether or not this cell is dead (captured)
	 */
	public boolean findNextRight(int colour, int row, int col) {
		col = col +1;
		//if the row and col index is out of bounds
		if(row<0||col<0||row>=this.boardDims||col>=this.boardDims) {
			return false;
		} // if its a cell capturing the current arrangement
		else if(this.cells[row][col] == colour) {
			return true;
		} // check if cell has already been visited
		else if(dead.containsKey(row*this.boardDims + col)) {
			return true;
		}
		
		//if haven't been here, go here
		dead.put(row*this.boardDims + col, Piece.DEAD);
		
		if(!(findNextRight(colour, row, col))){
			dead.clear();
			return false;
		}
		if(!(findNextUp(colour, row, col))){
			dead.clear();
			return false;
		}
		if(!(findNextDown(colour, row, col))){
			dead.clear();
			return false;
		}
		
		return true;
	}
	
	/** updates the board data to include the newly captured (dead) cells */
	public void update(Board b){
		int cell_is_dead = 0;
		int[][] cells = b.getCells();
		
	
		for(int i=0; i<this.boardDims; i++) {
			for(int j =0; j<this.boardDims; j++) {
				
				//check if the cell is dead
				cell_is_dead = dead.getOrDefault(i*this.boardDims + j, 
						Piece.INVALID);
				
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
		b.setCells(cells);
	}

	/** Prints the board configuration to standard output */
	public void printBoard(){
		int cell = 0;
		for(int i=0; i<this.boardDims; i++){
			for(int j=0; j<this.boardDims; j++){
				cell = getCells()[i][j];
				switch(cell) {
				case Piece.EMPTY:
					System.out.print("+ ");
					break;
				case Piece.WHITE:
					System.out.print("W ");
					break;
				case Piece.BLACK:
					System.out.print("B ");
					break;
				case CustomPiece.DEADSPACE:
					System.out.print("- ");
					break;
				case CustomPiece.DEADWHITE:
					System.out.print("w ");
					break;
				case CustomPiece.DEADBLACK:
					System.out.print("b ");
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
	
	/** SORT THIS OUT!!!!!!! PRINT STREAAAAAAAMMMMMMMMMMMMMMMMM
	 * @param b
	 */
	public void printBoard(Board b){
		int cell = 0;
		for(int i=0; i<b.boardDims; i++){
			for(int j=0; j<b.boardDims; j++){
				cell = b.getCells()[i][j];
				switch(cell) {
				case Piece.EMPTY:
					System.out.print("+ ");
					break;
				case Piece.WHITE:
					System.out.print("W ");
					break;
				case Piece.BLACK:
					System.out.print("B ");
					break;
				case CustomPiece.DEADSPACE:
					System.out.print("- ");
					break;
				case CustomPiece.DEADWHITE:
					System.out.print("w ");
					break;
				case CustomPiece.DEADBLACK:
					System.out.print("b ");
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
		int[][]output = new int[this.getBoardDims()][this.getBoardDims()];
		
		for (int i=0; i< this.getBoardDims(); i++){
			for (int j=0; j < this.getBoardDims(); j++){
				output[i][j] = this.cells[i][j];
			}
		}
		return output;
	}
	
	public int[][] getCells(Board b) {
		int[][]output = new int[b.getBoardDims()][b.getBoardDims()];
		
		for (int i=0; i< b.getBoardDims(); i++){
			for (int j=0; j < b.getBoardDims(); j++){
				output[i][j] = b.cells[i][j];
			}
		}
		return output;
	}

	public void setCells(int[][] cells) {
		this.cells = cells;
	}
	
	public HashMap<Integer, Integer> getDead() {
		return dead;
	}

	public void setDead(HashMap<Integer, Integer> dead) {
		this.dead = dead;
	}
	
}