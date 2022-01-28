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
	
	public String getIdentifier() {
		return identifier;
	}
	
	public String getName() {
		return identifier.split("#")[0];
	}
	
	public void addTiles(String letters) {
		for (String letter: letters.split("")) {
			tile_rack.add((Tiles) Tiles.fromLetter(letter));
		}
	}
	
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
	
	protected List<Tiles> copyRack() {
		List<Tiles> copy = new ArrayList<>();
		for (Tiles tile: tile_rack)
			copy.add(tile);
		return copy;
	}
	
	public void addScore(int amount) {
		score += amount;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}
	
	public String getTiles() {
		String tiles = "";
		for (Tiles tile: tile_rack)
			tiles += tile.getLetter();
		return tiles;
	}

	public List<Tiles> getTilesList() {
		return tile_rack;
	}
}
