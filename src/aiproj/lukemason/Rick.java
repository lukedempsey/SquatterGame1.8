package aiproj.lukemason;

import aiproj.squatter.Move;
import aiproj.squatter.Piece;

public class Rick extends LukeMason {
	
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
		
		Heuristic util = new Heuristic();
		
		Move move = new Move();
		int[][] currentBoard = board.getCells();
		int[] tmp_move;
		
		if (this.getGameOver()){
			return null;
		}
		
		tmp_move = makeRandMove();
		
		//Testing heuristic
		System.out.println("\n\nHeuristic:  ");
		System.out.println(util.getHeuristic(this, board, tmp_move));
		System.out.println("\n");
		
		move.Row = tmp_move[0];
		move.Col = tmp_move[1];
		move.P = this.playerColour;
		currentBoard[tmp_move[0]][tmp_move[1]] = this.playerColour;

		board.state(board, this);
		board.setCells(currentBoard);
		
		
		
		return move;
	}

	
}
