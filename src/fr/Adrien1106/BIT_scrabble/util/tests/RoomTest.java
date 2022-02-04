package fr.Adrien1106.BIT_scrabble.util.tests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.Adrien1106.BIT_scrabble.game.Player;
import fr.Adrien1106.BIT_scrabble.game.Room;
import fr.Adrien1106.BIT_scrabble.main.References;
import fr.Adrien1106.BIT_scrabble.util.words.Dictionary;
import fr.Adrien1106.util.exceptions.TooFewPlayersException;
import fr.Adrien1106.util.exceptions.TooManyPlayersException;
import fr.Adrien1106.util.interfaces.IPlayer;

class RoomTest {
	
	private Room room;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		Dictionary.loadFromRessource();
	}

	@BeforeEach
	void setUp() throws Exception {
		room = new Room(100, 2, 2);
	}

	@Test
	void testInit() {
		assertNotNull(room.getBoard());
		assertEquals(room.getMinPlayers(), References.MIN_PLAYERS);
		assertEquals(room.getId(), 100);
		assertFalse(room.isFull());
	}
	
	@Test
	void testStartAndExit() throws TooManyPlayersException {
		List<IPlayer> players = Arrays.asList(new Player("P1#1"), new Player("P2#2"));
		room.addPlayer((Player) players.get(0));
		assertThrows(TooFewPlayersException.class, () -> room.start());
		room.addPlayer((Player) players.get(1));
		assertThrows(TooManyPlayersException.class, () -> room.addPlayer(new Player("P3#3")));
		
		room.tryStart();
		
		assertEquals(players, room.getPlayers());
		assertTrue(room.isTurn((Player) players.get(0)));
		assertFalse(room.isTurn((Player) players.get(1)));
		
		room.skip();
		assertFalse(room.isTurn((Player) players.get(0)));
		assertTrue(room.isTurn((Player) players.get(1)));
	}

}
