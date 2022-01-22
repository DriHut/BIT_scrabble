package fr.Adrien1106.BIT_scrabble.util;

public enum Modifier {
NONE         (1, "  "),
TRIPLE_WORD  (3, "TW"),
DOUBLE_WORD  (2, "DW"),
TRIPLE_LETTER(3, "TL"),
DOUBLE_LETTER(2, "DL");
	
	private int multiplier;
	private String id;
	
	Modifier(int multiplier, String id){
		this.multiplier = multiplier;
		this.id = id;
	}
	
	public boolean isWordModifier() {
		if (this.equals(TRIPLE_WORD) || this.equals(DOUBLE_WORD)) return true;
		return false;
	}
	
	public int getMultiplier() {
		return multiplier;
	}
	
	public String getId() {
		return id;
	}
	
	public static Modifier fromId(String id) {
		for(Modifier value: Modifier.values())
			if (value.getId().equalsIgnoreCase(id))
				return value;
		return NONE;
	}
}
