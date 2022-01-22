package fr.Adrien1106.BIT_scrabble.util;

import fr.Adrien1106.util.exceptions.WordOutOfBoundsException;
import fr.Adrien1106.util.exceptions.WrongCoordinateException;

public class Board {
	
	public static final int SIZE = 15;
	
	private Tile[][] tiles;
	private Modifier[][] modifiers;
	
	public Board(Modifier[][] modifiers) {
		this.tiles = new Tile[SIZE][SIZE];
		flushBoard();
		this.modifiers = modifiers;
	}
	
	/**
	 * Clear the board from all its tiles
	 */
	public void flushBoard() {
		for(Tile[] row: tiles)
			for(Tile tile: row)
				tile = Tile.EMPTY;
	}
	
	/**
	 * Place a word on a board return if achieved
	 * @param word - word to be placed
	 * @param align - the alignment of the placed word (horizontal or vertical)
	 * @throws WrongCoordinateException - when coordinates too short or its out of the board
	 * @throws NumberFormatException - when coordinates couldn't be converted to numerical value
	 * @throws WordOutOfBoundsException - when the word exceeds board boundaries
	 */
	public void place(String coordinate, String word, Align align) throws WrongCoordinateException, NumberFormatException, WordOutOfBoundsException {
		if (coordinate.length() < 2 ) throw new WrongCoordinateException(coordinate);
		int x = letterToCoord(coordinate.charAt(0));
		int y = Integer.valueOf(coordinate.substring(1)) - 1;
		
		if (!isOnBoard(x, y)) throw new WrongCoordinateException(coordinate);
		if (!isOnBoard(x + word.length()*align.getDirection().getX(), y + word.length()*align.getDirection().getY())) throw new WordOutOfBoundsException();
		
	}
	
	protected void placeWord() {
		
	}
	
	/**
	 * Convert a letter to a coordinate (A = 0)
	 * @param letter
	 * @return the coordinate
	 */
	public static int letterToCoord(char letter) {
		return letter - 65;
	}
	
	/**
	 * Determines if there is a tile on the given direction
	 * @param x
	 * @param y
	 * @param dir - direction to check if there is a tile
	 * @return if the tile on the given direction is occupied by a tile
	 */
	protected boolean hasAdjacent(int x, int y, Direction dir) {
		x = x+dir.getX();
		y = y+dir.getY();
		
		if (!isOnBoard(x, y)) return false;
		
		return !tiles[x][y].equals(Tile.EMPTY);
	}
	
	/**
	 * Determines if the given coordinates are in the board boundaries
	 * @param x
	 * @param y
	 * @return if the given coordinates are on the board
	 */
	protected boolean isOnBoard(int x, int y) {
		return x < SIZE && y < SIZE && x >= 0 && y >= 0;
	}
}
