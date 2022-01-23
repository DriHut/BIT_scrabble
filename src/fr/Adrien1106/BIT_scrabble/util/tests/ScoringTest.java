package fr.Adrien1106.BIT_scrabble.util.tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.Adrien1106.BIT_scrabble.exceptions.CantPlaceWordHereException;
import fr.Adrien1106.BIT_scrabble.util.Align;
import fr.Adrien1106.BIT_scrabble.util.Board;
import fr.Adrien1106.BIT_scrabble.util.ModifierBoard;
import fr.Adrien1106.BIT_scrabble.util.words.Dictionary;
import fr.Adrien1106.util.exceptions.WordOutOfBoundsException;
import fr.Adrien1106.util.exceptions.WrongCoordinateException;

class ScoringTest {

	private Board board;
	
	@BeforeAll
    public static void setAll() {
		Dictionary.loadFromRessource();
    }
	
	@BeforeEach
    public void setUp() {
		board = new Board(ModifierBoard.convertFromString(ModifierBoard.spreadBoard(Board.SIZE, ModifierBoard.BOARD_I)));
    }
	
	@Test
	void testPlaceI() {
		try {
			assertEquals(14,board.place("F8", "horn", Align.HORIZONTAL));
		} catch (NumberFormatException | WrongCoordinateException | WordOutOfBoundsException
				| CantPlaceWordHereException e) {}
	}

	@Test
	void testPlaceII() {
		try {
			board.place("F8", "horn", Align.HORIZONTAL);
			board.saveMove();
			assertEquals(9,board.place("H6", "farm", Align.VERTICAL));
		} catch (NumberFormatException | WrongCoordinateException | WordOutOfBoundsException
				| CantPlaceWordHereException e) {}
	}

	@Test
	void testPlaceIII() {
		try {
			board.place("F8", "horn", Align.HORIZONTAL);
			board.saveMove();
			board.place("H6", "farm", Align.VERTICAL);
			board.saveMove();
			assertEquals(25,board.place("F10", "paste", Align.HORIZONTAL));
		} catch (NumberFormatException | WrongCoordinateException | WordOutOfBoundsException
				| CantPlaceWordHereException e) {}
	}

	@Test
	void testPlaceIV() {
		try {
			board.place("F8", "horn", Align.HORIZONTAL);
			board.saveMove();
			board.place("H6", "farm", Align.VERTICAL);
			board.saveMove();
			board.place("F10", "paste", Align.HORIZONTAL);
			board.saveMove();
			assertEquals(16,board.place("H9", "mob", Align.HORIZONTAL));
		} catch (NumberFormatException | WrongCoordinateException | WordOutOfBoundsException
				| CantPlaceWordHereException e) {}
	}
}
