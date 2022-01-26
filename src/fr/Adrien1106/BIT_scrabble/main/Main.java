package fr.Adrien1106.BIT_scrabble.main;

import fr.Adrien1106.BIT_scrabble.util.Align;
import fr.Adrien1106.BIT_scrabble.util.Board;
import fr.Adrien1106.BIT_scrabble.util.ModifierBoard;
import fr.Adrien1106.BIT_scrabble.util.words.Dictionary;
import fr.Adrien1106.util.exceptions.CantPlaceWordHereException;
import fr.Adrien1106.util.exceptions.WordOutOfBoundsException;
import fr.Adrien1106.util.exceptions.WrongCoordinateException;

public class Main {

	public static void main(String[] args) {
		Dictionary.loadFromRessource();
		Board board = new Board(ModifierBoard.convertFromString(ModifierBoard.spreadBoard(Board.SIZE, ModifierBoard.BOARD_I)));
		try {
			System.out.println(board.place("F8", "h$or$n", Align.HORIZONTAL));
			System.out.println(board.getUsedTiles("F8", "h$or$n", Align.HORIZONTAL));
			board.saveMove();
		} catch (NumberFormatException | WrongCoordinateException | WordOutOfBoundsException
				| CantPlaceWordHereException e) {e.printStackTrace();}
		System.out.println(board.toString());
		board.fromString(board.toString());
		System.out.print(board.getString());
	}
}
