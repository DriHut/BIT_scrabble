package fr.Adrien1106.BIT_scrabble.util.tests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.Adrien1106.BIT_scrabble.game.Player;
import fr.Adrien1106.BIT_scrabble.util.Tiles;

class PlayerTest {
	
	private Player player;

	@BeforeEach
	void setUp() throws Exception {
		player = new Player("ARandDude#0");
	}

	@Test
	void testTileMovements() {
		player.addTiles("abbcdd$");
		assertEquals("abbcdd$", player.getTiles());
		
		player.removeTiles("ad");
		assertEquals("bbcd$", player.getTiles());
		
		List<Tiles> tiles = new ArrayList<>();
		tiles.add(Tiles.B);
		tiles.add(Tiles.B);
		tiles.add(Tiles.C);
		tiles.add(Tiles.D);
		tiles.add(Tiles.BLANK);
		assertEquals(tiles, player.getTilesList());
		
		assertTrue(player.hasTiles("bb"));
		assertTrue(player.hasTiles("bbc$"));
		
		player.removeTiles("b");
		assertFalse(player.hasTiles("bb"));
		assertTrue(player.hasTiles("b"));
	}
	
	@Test
	void testScoring() {
		assertEquals(0, player.getScore());

		player.addScore(10);
		assertEquals(10, player.getScore());
		
		player.setScore(5);
		assertEquals(5, player.getScore());
	}
	
	@Test
	void testNaming() {
		assertEquals("ARandDude", player.getName());
		assertEquals("ARandDude#0", player.getIdentifier());
	}

}
