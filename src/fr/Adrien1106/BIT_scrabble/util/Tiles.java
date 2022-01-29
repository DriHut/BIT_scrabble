package fr.Adrien1106.BIT_scrabble.util;

public enum Tiles implements Tile {
EMPTY(0," "),
BLANK(0,"$"),
A(1,"a"),
B(3,"b"),
C(3,"c"),
D(2,"d"),
E(1,"e"),
F(4,"f"),
G(2,"g"),
H(4,"h"),
I(1,"i"),
J(8,"j"),
K(5,"k"),
L(1,"l"),
M(3,"m"),
N(1,"n"),
O(1,"o"),
P(3,"p"),
Q(10,"q"),
R(1,"r"),
S(1,"s"),
T(1,"t"),
U(1,"u"),
V(4,"v"),
W(4,"w"),
X(8,"x"),
Y(4,"y"),
Z(10,"z");

	private int value;
	private String letter;

	Tiles(int value, String letter) {
		this.value = value;
		this.letter = letter;
	}
	
	@Override
	public String getSubLetter() {
		return letter;
	}

	@Override
	public void setSubLetter(String sub_letter) {
		
	}

	@Override
	public String getLetter() {
		return letter;
	}

	@Override
	public int getValue() {
		return value;
	}
	
	/**
	 * Determines a tile from a letter
	 * @param letter - the letter of the wanted tile
	 * @return the tile or the default one
	 */
	public static Tile fromLetter(String letter) {
		for (Tile tile: Tiles.values()) {
			if (tile.getLetter().equalsIgnoreCase(letter))
				return tile;
			
		}
		return Tiles.EMPTY;
	}
}
