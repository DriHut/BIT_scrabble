package fr.Adrien1106.BIT_scrabble.util;

import fr.Adrien1106.BIT_scrabble.exceptions.CantPlaceLetterHereException;
import fr.Adrien1106.BIT_scrabble.exceptions.CantPlaceWordHereException;
import fr.Adrien1106.BIT_scrabble.util.words.Dictionary;
import fr.Adrien1106.util.exceptions.WordOutOfBoundsException;
import fr.Adrien1106.util.exceptions.WrongCoordinateException;

public class Board {
	
	public static final int SIZE = 15;
	
	private Tile[][] tiles;
	private Tile[][] tiles_copy;
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
	 * Place a word on a board throws error if not achieved and return a score if achieved
	 * @param word - word to be placed
	 * @param align - the alignment of the placed word (horizontal or vertical)
	 * @return - the score achieved by that move
	 * @throws WrongCoordinateException - when coordinates too short or its out of the board
	 * @throws NumberFormatException - when coordinates couldn't be converted to numerical value
	 * @throws WordOutOfBoundsException - when the word exceeds board boundaries
	 * @throws CantPlaceWordHereException - when the word place has been unauthorised
	 */
	public int place(String coordinate, String word, Align align) throws WrongCoordinateException, NumberFormatException, WordOutOfBoundsException, CantPlaceWordHereException {
		if (coordinate.length() < 2 ) throw new WrongCoordinateException(coordinate);
		int x = letterToCoord(coordinate.charAt(0));
		int y = Integer.valueOf(coordinate.substring(1)) - 1;
		
		if (!isOnBoard(x, y)) throw new WrongCoordinateException(coordinate);
		if (!isOnBoard(x + word.length()*align.getDirection().getX(), y + word.length()*align.getDirection().getY())) throw new WordOutOfBoundsException();
		
		tiles_copy = tiles.clone();
		
		// try placing letter one by one
		int new_x = x, new_y = y;
		int score = 0;
		for (String letter: word.split("")) {
			try {
				score += placeLetter(new_x, new_y, letter, align);
			} catch (CantPlaceLetterHereException e) {
				e.printStackTrace();
				throw new CantPlaceWordHereException();
			}
			new_x += align.getDirection().getX();
			new_y += align.getDirection().getY();
		}
		
		// reverse the last move
		new_x += align.getDirection().getX();
		new_y += align.getDirection().getY();
		
		// replace the last letter to score the word that has been placed
		tiles_copy[new_x][new_y] = Tile.EMPTY;
		try {
			score += placeLetter(new_x, new_y, word.substring(word.length()-1), align.other());
		} catch (CantPlaceLetterHereException e) {
			throw new CantPlaceWordHereException();
		}
		
		// set the board to the copy since the placing has been approved
		tiles = tiles_copy.clone();
		return score;
	}
	
	/**
	 * Tries to place a letter by checking if a newly word is created and is valid
	 * @param x
	 * @param y
	 * @param letter - letter to be placed
	 * @param align - the align of the placed word
	 * @return the score if the letter can be placed
	 * @throws CantPlaceLetterHereException
	 */
	protected int placeLetter(int x, int y, String letter, Align align) throws CantPlaceLetterHereException {
		// ignore if letter already placed
		if (tiles_copy[x][y].getLetter().equals(letter)) return 0;
		
		Direction check_direction = align.other().getDirection().opposite();
		// skip the process if no new words formed
		if (hasAdjacent(x, y, check_direction) && hasAdjacent(x, y, check_direction.opposite())) return 0;
		
		// place the letter on the copy
		tiles_copy[x][y] = Tile.fromLetter(letter);
		
		// go up until there is not letter before
		while(hasAdjacent(x, y, check_direction)) {
			x += check_direction.getX();
			y += check_direction.getY();
		}
		check_direction.opposite(); // reverse direction to get the word that has been formed
		
		// save word starting point
		int new_x = x, new_y = y;
		
		// forms the new word and check its validity
		String word = tiles_copy[x][y].getLetter();
		while(hasAdjacent(x, y, check_direction)) {
			x += check_direction.getX();
			y += check_direction.getY();
			word += tiles_copy[x][y].getLetter();
		}
		
		// check if the word is valid
		if (!Dictionary.isWord(word)) throw new CantPlaceLetterHereException(letter, "a new formed word is not valid");
		
		return score(new_x, new_y, word, check_direction);
	}
	
	/**
	 * Convert a letter to a coordinate (A = 0)
	 * @param letter - to be converted to numerical coordinate
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
		x += dir.getX();
		y += dir.getY();
		
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
	
	/**
	 * Determines the score of a given word on the given board depending on the multipliers
	 * @param x
	 * @param y
	 * @param word - the word being scored
	 * @param dir - the direction of the word
	 * @param tiles - the word on which the scoring must be applied
	 * @return the calculated score
	 */
	protected int score(int x, int y, String word, Direction dir) {
		int score = 0;
		
		// will be used for word multiplication
		int const_multiplier = 1;
		for (int i = 0; i < word.length(); i++) {
			
			// update word multiplier if needed
			if (modifiers[x][y].isWordModifier()) const_multiplier *= modifiers[x][y].getMultiplier();
			
			// update the score for the current letter depending on the multipliers that needs to be applied
			score += tiles_copy[x][y].getValue() * (!modifiers[x][y].isWordModifier()? modifiers[x][y].getMultiplier(): 1);
			
			// update the coordinates
			x += dir.getX();
			y += dir.getY();
		}
		
		// apply word multiplier
		score *= const_multiplier;
		
		return score;
	}
}
