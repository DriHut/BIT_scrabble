package fr.Adrien1106.BIT_scrabble.util;

public class BlankTile implements Tile {
	private static final int value = 0;
	private static final String letter = "$";
	private String sub_letter;

	BlankTile(String letter) {
		sub_letter = letter;
		if (letter.equals("$")) sub_letter = " ";
	}
	
	@Override
	public String getSubLetter() {
		return sub_letter;
	}

	@Override
	public void setSubLetter(String sub_letter) {
		this.sub_letter = sub_letter;
	}

	@Override
	public String getLetter() {
		return letter;
	}

	@Override
	public int getValue() {
		return value;
	}
}
