package aiproj.lukemason;

import aiproj.squatter.*;

/**
 * 
 * @author lukedempsey
 * 
 * Currently three main factors influencing if the proposed move is worthwhile
 * 
 * liberties (free spaces adjacent)
 * 		for us - did this move remain mostly uncaptured?
 * 		for them - did our move encapsulate as much of them?
 * alive/dead
 * 		for us - did we capture more cells?
 * 		for them - did they capture no cells (we block them), or atleast less than us?
 * connecting the pieces
 * 		for us - did we make a longer chain of pieces?
 *  	for them - did we 
 *
 */

public class Heuristic {
	
	static boolean debug = false;
	
	/** Calculates the alive/dead factor
	 * @return the factor as an int
	 */
	public int aliveDead(LukeMason player){
		int white = player.getTallyW();
		int black = player.getTallyB();
		
		return white - black;
	}
	
	/** Finds the amount of adjacent real-estate for each player
	 * @param board the board to analyse
	 * @param player the data is from this players perspective
	 * @return current players real-estate minus the opponents
	 */
	public static int liberties(Board board, LukeMason player){
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
									if(cells[i][j]==player.getPlayerColour()){
										lib_player++;
									}else if(cells[i][j]==player.getOpponentColour()){
										lib_opponent++;
									}else{
										//TODO change this
										System.out.println("ERROR: cell is neither player or opponent colour");
										System.out.println(cells[i][j]);
										System.out.println(player.getPlayerColour());
									}
								}
							}catch (ArrayIndexOutOfBoundsException e){
								if(debug) System.out.println("Cell boundary encountered (player)");
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
		
		return 0;
	}
	
}
