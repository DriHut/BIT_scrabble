package fr.Adrien1106.BIT_scrabble.main;

import fr.Adrien1106.BIT_scrabble.util.Align;
import fr.Adrien1106.BIT_scrabble.util.Board;
import fr.Adrien1106.BIT_scrabble.util.ModifierBoard;
import fr.Adrien1106.BIT_scrabble.util.Tile;
import fr.Adrien1106.BIT_scrabble.util.TileBag;
import fr.Adrien1106.BIT_scrabble.util.words.Dictionary;
import fr.Adrien1106.util.exceptions.CantPlaceWordHereException;
import fr.Adrien1106.util.exceptions.WordOutOfBoundsException;
import fr.Adrien1106.util.exceptions.WrongCoordinateException;

public class Main {

	public static void main(String[] args) {
		Dictionary.loadFromRessource();
		Board board = new Board(ModifierBoard.convertFromString(ModifierBoard.spreadBoard(Board.SIZE, ModifierBoard.BOARD_I)));
		System.out.print(board.toString());
		try {
			board.place("F8", "horn", Align.HORIZONTAL);
			board.saveMove();
		} catch (NumberFormatException | WrongCoordinateException | WordOutOfBoundsException
				| CantPlaceWordHereException e) {}
		System.out.print(board.getString());
	}
}
