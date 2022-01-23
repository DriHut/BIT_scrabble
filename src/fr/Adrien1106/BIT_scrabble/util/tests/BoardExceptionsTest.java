package fr.Adrien1106.BIT_scrabble.util.tests;

import static org.junit.jupiter.api.Assertions.assertThrows;

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

class BoardExceptionsTest {

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
	void testPlaceMiddleFirst() {
		assertThrows(CantPlaceWordHereException.class, () -> board.place("F9", "horn", Align.HORIZONTAL));
		assertThrows(CantPlaceWordHereException.class, () -> board.place("I8", "horn", Align.HORIZONTAL));
	}
	
	@Test
	void testOutOfBounds() {
		assertThrows(WordOutOfBoundsException.class, () -> board.place("M8", "horn", Align.HORIZONTAL));
	}

	@Test
	void testWrongCoordinates() {
		assertThrows(WrongCoordinateException.class, () -> board.place("P8", "horn", Align.HORIZONTAL));
		assertThrows(WrongCoordinateException.class, () -> board.place("H-1", "horn", Align.HORIZONTAL));
		assertThrows(WrongCoordinateException.class, () -> board.place("h8", "horn", Align.HORIZONTAL));
		assertThrows(NumberFormatException.class, () -> board.place("HH2", "horn", Align.HORIZONTAL));
	}
	

	@Test
	void testWrongWords() {
		assertThrows(CantPlaceWordHereException.class, () -> board.place("F8", "adfe", Align.HORIZONTAL));
		try {
			board.place("F8", "horn", Align.HORIZONTAL);
			board.saveMove();
		} catch (NumberFormatException | WrongCoordinateException | WordOutOfBoundsException
				| CantPlaceWordHereException e) {}
		assertThrows(CantPlaceWordHereException.class, () -> board.place("H5", "farm", Align.VERTICAL));
		try {
			board.place("H6", "farm", Align.VERTICAL);
			board.saveMove();
			board.place("F10", "paste", Align.HORIZONTAL);
			board.saveMove();
		} catch (NumberFormatException | WrongCoordinateException | WordOutOfBoundsException
				| CantPlaceWordHereException e) {}
		assertThrows(CantPlaceWordHereException.class, () -> board.place("E11", "bots", Align.HORIZONTAL));
	}
}
