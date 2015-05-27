package aiproj.lukemason;

import aiproj.squatter.Move;

//for output of minimax
public class Minimax {
	
	public double value;
	Move move = new Move();
	
	public Minimax(){
		this.setValue(0);
	}
	
	public Minimax(Double util_or_heur){
		this.setValue(util_or_heur);
	}
	
	public Minimax(Move move) {
		this.setMove(move.Row, move.Col, move.P);
	}
	
	public Minimax(Move move, Double value) {
		this.setMove(move.Row, move.Col, move.P);
		this.setValue(value);
	}

	public double getValue() {
		return value;
	}
	public void setValue(double value) {
		this.value = value;
	}
	public Move getMove() {
		return move;
	}
	public void setMove(Move move) {
		this.move = move;
	}
	public void setMove(int row, int col, int player) {
				
		move.Row = row;
		move.Col = col;
		move.P = player;
	}
	
}
