package aiproj.lukemason;
/**Creating an enum that holds colours 
 * authors - luke dempsey 638407, mason rose-campbell 638370
 * */

/**
 * 
 * 
 * May not need, just realised that the PIECE interface is a thing
 *
 */

public enum Colour {
	//Int arguments correspond to Piece.java
	WHITE(1), BLACK(2);
	
	private int valueInt;

	private Colour(int value){
		valueInt = value;
	}
	/**
	 * @return the colour in a string of one char
	 */
	public int getColour(){
		return valueInt;
	}
	
}
