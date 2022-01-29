package fr.Adrien1106.BIT_scrabble.game;

import java.util.ArrayList;
import java.util.List;

import fr.Adrien1106.BIT_scrabble.util.Tiles;
import fr.Adrien1106.util.interfaces.IPlayer;

public class Player implements IPlayer {

	private List<Tiles> tile_rack;
	private String identifier;
	private int score = 0;
	
	public Player(String identifier) {
		this.identifier = identifier;
		this.tile_rack = new ArrayList<>();
	}
	
	/**
	 * @return the player identifier
	 */
	public String getIdentifier() {
		return identifier;
	}
	
	/**
	 * @return the player name
	 */
	public String getName() {
		return identifier.split("#")[0];
	}
	
	/**
	 * Add tiles to the player rack
	 * @param letters - the tiles letter
	 */
	public void addTiles(String letters) {
		for (String letter: letters.split("")) {
			tile_rack.add((Tiles) Tiles.fromLetter(letter));
		}
	}
	
	/**
	 * remove the tiles from the player rack
	 * @param letters - the tile letters
	 */
	public void removeTiles(String letters) {
		List<Tiles> rack_copy = copyRack();
		for (String letter: letters.split("")) {
			for(Tiles tile: tile_rack)
				if (letter.equals(tile.getLetter())) {
					rack_copy.remove(tile);
					break;
				}
		}
		tile_rack = rack_copy;
	}
	
	/**
	 * check if player has all the given tiles
	 * @param letters - of the tiles to check for
	 * @return if player has the tiles
	 */
	public boolean hasTiles(String letters) {
		List<Tiles> rack_copy = copyRack();
		for (String letter: letters.split("")) {
			boolean has = false;
			for(Tiles tile: rack_copy)
				if (letter.equals(tile.getLetter())) {
					rack_copy.remove(tile);
					has = true;
					break;
				}
			if (!has) return false;
		}
		return true;
	}
	
	/**
	 * @return a copy of the rack
	 */
	protected List<Tiles> copyRack() {
		List<Tiles> copy = new ArrayList<>();
		for (Tiles tile: tile_rack)
			copy.add(tile);
		return copy;
	}
	
	/**
	 * add score
	 * @param amount - the score to add
	 */
	public void addScore(int amount) {
		score += amount;
	}

	/**
	 * @return the player score
	 */
	public int getScore() {
		return score;
	}

	/**
	 * set the player score
	 * @param score - new score to apply
	 */
	public void setScore(int score) {
		this.score = score;
	}
	
	/**
	 * get the tiles as string
	 * @return the tiles
	 */
	public String getTiles() {
		String tiles = "";
		for (Tiles tile: tile_rack)
			tiles += tile.getLetter();
		return tiles;
	}

	/**
	 * get the tiles as tile list
	 * @return the tiles
	 */
	public List<Tiles> getTilesList() {
		return tile_rack;
	}
}
