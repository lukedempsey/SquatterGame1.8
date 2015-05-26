package aiproj.lukemason;
import aiproj.squatter.*;
/* COMP30024 Artificial Intelligence - Squat it like it's hot
 * Modified from Project Part A: Testing for a Win
 * Authors: Luke Dempsey <ldempsey - 638407>, Mason Rose-Campbell <mrose1 - 638370>
 */


/** Board Class to be used in conjunction with squatItLikeItsHot class.
 * For Finding the current state of the game based on input.
 */
public class Board {
	
	//Board variables
	private int boardDims;
	private static int[][] cells;
	
	private static boolean debug = false;
	
	/** Creates a new Board object
	 * @param n 
	 * @param cells The status of each tile on the board.
	 */
	public Board(int n) {
		
		//check to validate input
		boardDims = n;
		
		//initialise and fill the board
		initBoard(boardDims, cells);

		fillBoard(boardDims, cells);
		
		if(debug)printBoard(this);

	}
	
	
	/** Initialises the cells array
	 * @param boardDims The dimensions of the board size
	 */
	public static void initBoard(int boardDims, int[][] cells){
		Board.cells = new int[boardDims][boardDims];
		
	}	
	

	/** Reads in information from system input and fills cells array
	 * with this data
	 * @param cells Array to store board data
	 */
	public static void fillBoard(int boardDims, int[][] cells) {
		
		int col = 0;
		int row = 0;
		
		while(row<boardDims){
			while(col<boardDims){
				cells[row][col] = Piece.EMPTY;
				col++;
			}
			row++;
			col=0;
		}
	}
	
	public static boolean checkGameOver(Board board){
		for(int row = 0; row < (board.getBoardDims()-1); row++){
			for(int col = 0; col < (board.getBoardDims()-1); col++){
				
				//debug
				if(debug){System.out.println("Checking "+row+","+col+":"+board.getCells()[row][col]);}
				
				//Look to see if game is finished
				if (board.getCells()[row][col]==Piece.EMPTY) {
					
					//debug
					if(debug){System.out.println("Found empty cell");}
					
					//change game state to not over
					LukeMason.setGameOver(false);
					return false;
				}
			}
		}
		return true;
	}

	/** Finds the current game state by searching through the board
	 * @param debug Boolean to turn debugging mode on and off
	 * @param board Data will be examined about this board
	 * @param gameOver Whether or not the game has finished or not
	 */
	public static void state(Boolean debug, Board board, Boolean gameOver) {
		//TODO fix this sloppy initialisation
		int lastCol = Piece.INVALID;
		
		//Check for game over
		checkGameOver(board);
		
		//Search for a captured point
		//Skip cells on bottom & right edges
		for(int row = 0; row < (board.getBoardDims()-1); row++){
			for(int col = 0; col < (board.getBoardDims()-1); col++){
				
				//Check if captured
				if (board.getCells()[row][col]==Piece.DEAD){
					
					//debug
					if(debug){System.out.println("Found a captured cell: "+row+","+col);}
					
					//Add to tally
					if(lastCol==Piece.BLACK){
						LukeMason.setTallyB(LukeMason.getTallyB() + 1);
					} else {
						LukeMason.setTallyW(LukeMason.getTallyW() + 1);
					}
					
					//debug
					if(debug){System.out.println("TallyW: "+LukeMason.getTallyW()+", TallyB: "+LukeMason.getTallyB());}
					
				//If not captured, set to latest colour
				} else {
					
					lastCol = board.getCells()[row][col];
					
					//debug
					if(debug){System.out.println("Captured cell not found, last Colour set to "+lastCol);}
				}
			}
		}
	}

	public static void printBoard(Board board){
		int dims = board.getBoardDims();
		for(int i=0; i<dims; i++){
			for(int j=0; j<dims; j++){
				int tmp = board.getCells()[i][j];
				//TODO make cases for captured
				switch(tmp){
				case 0:
					System.out.print("+");
					break;
				case 1:
					System.out.print("W");
					break;
				case 2:
					System.out.print("B");
					break;
				case 3:
					System.out.print("fix this");
					break;
					//TODO make error 
				case -1:
					System.out.print("Make and err message");
					break;
				}
			}
		System.out.print("\n");
		}
	}
	
/** Prints the current state of the board
	 * @param gameOver Whether the game has finished or not
	 * @param tallyB How many cells Black player has claimed 
	 * @param tallyW How many cells White player has claimed
	 */
	public static int returnState(Boolean gameOver, int tallyB, int tallyW){
		if(gameOver) {
			if(tallyB==tallyW) {
				return Piece.DEAD;
			} else if(tallyB>tallyW) {
				return Piece.BLACK;
			} else {
				return Piece.WHITE;
			}
		}else{
			return Piece.EMPTY;
		}
	}
	
	
	/** Setter for cells
	 * @param cells Array of data about cells on the board
	 */
	public void setCells(int[][] cells) {
		Board.cells = cells;
	}
	
	/** Getter for board dimensions
	 */
	public int getBoardDims() {
		return boardDims;
	}
	
	/** Getter for cells data string
	 */
	public int[][] getCells() {
		return cells;
	}
}
