//Bit of a simple player

package aiproj.lukemason;

import aiproj.squatter.Move;

public class RandomRick extends LukeMason{


	@Override
	public Move makeMove() {
		
		Move move = new Move();
		//int[][] currentBoard = board.getCells();
		int[] tmp_move;
		
		if (this.getGameOver()){
			return null;
		}
		
		tmp_move = makeRandMove();
		
		move.Row = tmp_move[0];
		move.Col = tmp_move[1];
		move.P = this.playerColour;
		board.placeMove(board, move);
		return move;
	}
	
}
