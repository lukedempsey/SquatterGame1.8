//Bit of a simple player

package aiproj.lukemason;

import aiproj.squatter.Move;
import aiproj.squatter.Piece;

public class RandomRick extends LukeMason{

	/**Sometimes stackoverflows, wouldn't worry about it because only for testing */
	public int[] makeRandMove(){
		
		int[][] currentBoard = board.getCells();
		int dim = board.getBoardDims();
		int move_coords[] = {0,0};
		
		int i = (int) Math.ceil((dim)*Math.random()-1);
		int j = (int) Math.ceil((dim)*Math.random()-1);
		
		if (currentBoard[i][j]!=Piece.EMPTY){
			return makeRandMove();
		}
		
		move_coords[0] = i;
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
		
		board.floodfill(move);
		board.update();
		return move;
	}
	
}
