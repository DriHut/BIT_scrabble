package fr.Adrien1106.BIT_scrabble.game;

import java.util.ArrayList;
import java.util.List;

import fr.Adrien1106.BIT_scrabble.util.Tile;
import fr.Adrien1106.util.interfaces.IPlayer;

public class Player implements IPlayer {

	private List<Tile> tile_rack;
	private String identifier;
	private int score = 0;
	
	public Player(String identifier) {
		this.identifier = identifier;
	}
	
	public String getIdentifier() {
		return identifier;
	}
	
	public String getName() {
		return identifier.split("#")[0];
	}
	
	public void addTiles(String letters) {
		for (String letter: letters.split("")) {
			tile_rack.add(Tile.fromLetter(letter));
		}
	}
	
	public void removeTiles(String letters) {
		for (String letter: letters.split("")) {
			for(Tile tile: tile_rack)
				if (letter.equals(tile.getLetter())) {
					tile_rack.remove(tile);
					continue;
				}
		}
	}
	
	public boolean hasTiles(String letters) {
		List<Tile> rack_copy = copyRack();
		for (String letter: letters.split("")) {
			for(Tile tile: rack_copy)
				if (letter.equals(tile.getLetter())) {
					rack_copy.remove(tile);
					continue;
				}
			return false;
		}
		return true;
	}
	
	protected List<Tile> copyRack() {
		List<Tile> copy = new ArrayList<>();
		for (Tile tile: tile_rack)
			copy.add(tile);
		return copy;
	}
	
	public void addScore(int amount) {
		score += amount;
	}

	public int getScore() {
		return score;
	}
}
