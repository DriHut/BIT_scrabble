package fr.Adrien1106.BIT_scrabble.util;

import fr.Adrien1106.BIT_scrabble.exceptions.CantPlaceLetterHereException;
import fr.Adrien1106.BIT_scrabble.util.words.Dictionary;
import fr.Adrien1106.util.exceptions.*;
import fr.Adrien1106.util.interfaces.IBoard;

public class Board implements IBoard {
	
	public static final int SIZE = 15;
	
	private Tile[][] tiles;
	private Tile[][] tiles_copy;
	private Modifier[][] modifiers;
	
	public Board(Modifier[][] modifiers) {
		this.tiles = new Tile[SIZE][SIZE];
		this.tiles_copy = new Tile[SIZE][SIZE];
		flushBoard();
		this.modifiers = modifiers;
	}
	
	/**
	 * Clear the board from all its tiles
	 */
	public void flushBoard() {
		for(int x = 0; x < SIZE; x++)
			for(int y = 0; y < SIZE; y++)
				tiles[x][y] = Tiles.EMPTY;
	}
	
	/**
	 * Create a copy of the tiles do we don't work on the verified board
	 */
	private void buildCopy() {
		for(int x = 0; x < SIZE; x++)
			for(int y = 0; y < SIZE; y++)
				tiles_copy[x][y] = tiles[x][y];
	}
	

	/**
	 * Saves the copy when accepted move
	 */
	private void saveCopy() {
		for(int x = 0; x < SIZE; x++)
			for(int y = 0; y < SIZE; y++)
				tiles[x][y] = tiles_copy[x][y];
	}
	
	
	/**
	 * Place a word on a board throws error if not achieved and return a score if achieved
	 * @param coordinate - the string coordinates
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
		
		buildCopy();
		
		// try placing letter one by one
		int new_x = x, new_y = y;
		int score = 0;
		Tile tile = Tiles.EMPTY;
		boolean is_sub_letter = false;
		for (String letter: word.split("")) {
			if (!is_sub_letter) tile = Tiles.fromLetter(letter);
			if (letter.equals(Tiles.BLANK.getLetter())) {
				is_sub_letter = true;
				continue;
			}
			try {
				if (is_sub_letter) tile = new BlankTile(letter);;
				score += placeTile(new_x, new_y, tile, align);
			} catch (CantPlaceLetterHereException e) {
				e.printStackTrace();
				throw new CantPlaceWordHereException();
			}
			new_x += align.getDirection().getX();
			new_y += align.getDirection().getY();
			is_sub_letter = false;
		}
		
		// no new connection == not linked except for first turn
		int used_length = getUsedTiles(coordinate, word, align).length();
		if (tiles_copy[SIZE/2][SIZE/2].equals(Tiles.EMPTY) 
				|| (score == 0 && used_length == word.length() && !tiles[SIZE/2][SIZE/2].equals(Tiles.EMPTY)) 
				|| used_length == 0) 
			throw new CantPlaceWordHereException();
		
		// reverse the last move
		new_x -= align.getDirection().getX();
		new_y -= align.getDirection().getY();
		
		// replace the last letter to score the word that has been placed
		tiles_copy[new_x][new_y] = Tiles.EMPTY;
		try {
			score += placeTile(new_x, new_y, tile, align.other());
		} catch (CantPlaceLetterHereException e) {
			throw new CantPlaceWordHereException();
		}
		
		return score;
	}
	
	/**
	 * Saves the last move after being verified
	 */
	public void saveMove() {
		// set the board to the copy since the placing has been approved
		saveCopy();
		// remove modifiers so they don't get used again
		for(int x = 0; x < SIZE; x++)
			for(int y = 0; y < SIZE; y++)
				if (!tiles[x][y].equals(Tiles.EMPTY)) modifiers[x][y] = Modifier.NONE;
	}
	
	/**
	 * Determines the letter used for the current move (called after place and before saveMove)
	 * @param coordinate - the string coordinates
	 * @param word - word to be placed
	 * @param align - the alignment of the placed word (horizontal or vertical)
	 * @return a list of the letter used
	 * @throws WrongCoordinateException
	 */
	public String getUsedTiles(String coordinate, String word, Align align) throws WrongCoordinateException {
		String letters = "";
		if (coordinate.length() < 2 ) throw new WrongCoordinateException(coordinate);
		int x = letterToCoord(coordinate.charAt(0));
		int y = Integer.valueOf(coordinate.substring(1)) - 1;

		boolean skip = false;
		for (String letter: word.split("")) {
			if (!tiles[x][y].getLetter().equals(letter) && !skip) letters += letter;
			x += align.getDirection().getX();
			y += align.getDirection().getY();
			if (letter.equals(Tiles.BLANK.getLetter())) {
				skip = true;
				continue;
			}
			skip = false;
		}
		
		return letters;
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
	protected int placeTile(int x, int y, Tile letter, Align align) throws CantPlaceLetterHereException {
		// ignore if letter already placed
		if (tiles_copy[x][y].getLetter().equals(letter.getLetter())) return 0;
		
		// place the letter on the copy
		tiles_copy[x][y] = letter;
		
		Direction check_direction = align.other().getDirection().opposite();
		// skip the process if no new words formed
		if (!hasAdjacent(x, y, check_direction) && !hasAdjacent(x, y, check_direction.opposite())) return 0;
		
		// go up until there is not letter before
		while(hasAdjacent(x, y, check_direction)) {
			x += check_direction.getX();
			y += check_direction.getY();
		}
		check_direction = check_direction.opposite(); // reverse direction to get the word that has been formed
		
		// save word starting point
		int new_x = x, new_y = y;
		
		// forms the new word and check its validity
		String word = tiles_copy[x][y].getSubLetter();
		while(hasAdjacent(x, y, check_direction)) {
			x += check_direction.getX();
			y += check_direction.getY();
			word += tiles_copy[x][y].getSubLetter();
		}
		
		// check if the word is valid
		if (!Dictionary.isWord(word)) throw new CantPlaceLetterHereException(letter.getSubLetter(), "a new formed word is not valid");
		
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
		
		return !tiles_copy[x][y].equals(Tiles.EMPTY);
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
	
	@Override
	public String toString() {
		String board = "";
		for(int y = 0; y < SIZE; y++) {
			for(int x = 0; x < SIZE-1; x++) {
				if (tiles[x][y] instanceof BlankTile) board += tiles[x][y].getLetter() + tiles[x][y].getSubLetter() +",";
				else board += tiles[x][y].getLetter() + ",";
			}
			if (tiles[SIZE-1][y] instanceof BlankTile) board += tiles[SIZE-1][y].getLetter() + tiles[SIZE-1][y].getSubLetter() +",";
			else board += tiles[SIZE-1][y].getLetter();
			if (y != SIZE-1) board += "//";
		}
		return board;
	}

	public void fromString(String board) {
		String[] rows = board.split("//");
		for(int y = 0; y < rows.length; y++) {
			String[] row = rows[y].split(",");
			for(int x = 0; x < row.length; x++) {
				if (row[x].length() == 2 && row[x].contains(Tiles.BLANK.getLetter())) tiles[x][y] = new BlankTile(row[x].split("")[1]);
				else tiles[x][y] = Tiles.fromLetter(row[x]);
			}
		}
	}
	

	/**
	 * Used for debugging prints the state of the board
	 */
	public String getString() {
		String board = "";
		for(int y = 0; y < SIZE; y++) {
			for(int x = 0; x < SIZE-1; x++) {
				if (tiles[x][y] instanceof BlankTile) board += tiles[x][y].getLetter() + tiles[x][y].getSubLetter();
				else if (!tiles[x][y].equals(Tiles.EMPTY)) board += tiles[x][y].getLetter() + " ";
				else board += modifiers[x][y].getId();
				board += ",";
			}
			if (tiles[SIZE-1][y] instanceof BlankTile) board += tiles[SIZE-1][y].getLetter() + tiles[SIZE-1][y].getSubLetter();
			else if (!tiles[SIZE-1][y].equals(Tiles.EMPTY)) board += tiles[SIZE-1][y].getLetter() + " ";
			else board += modifiers[SIZE-1][y].getId();
			if (y != SIZE - 1) board += "\n";
		}
		return board;
	}
}
