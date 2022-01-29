package fr.Adrien1106.BIT_scrabble.util;

import java.util.ArrayList;
import java.util.List;

public enum TileBag {
	BAG_I("9xA,2xB,2xC,4xD,12xE,2xF,2xG,2xH,8xI,2xJ,2xK,4xL,2xM,6xN,8xO,2xP,1xQ,6xR,4xS,6xT,4xU,2xV,2xW,1xX,2xY,1xZ,2x$");

	private String tile_list;
	
	TileBag(String tile_list) {
		this.tile_list = tile_list;
	}
	
	/**
	 * convert the string to a tile bag
	 * @return the list tile of the bag
	 */
	public List<Tiles> getBag() {
		List<Tiles> bag = new ArrayList<>();
		String[] letters = tile_list.split(",");
		for (int i = 0; i < letters.length; i++) {
			int amount = Integer.valueOf(letters[i].split("x")[0]);
			Tile tile = Tiles.fromLetter(letters[i].split("x")[1]);
			for (int j = 0; j < amount; j++)
				bag.add((Tiles) tile);
		}
		return bag;
	}

}
