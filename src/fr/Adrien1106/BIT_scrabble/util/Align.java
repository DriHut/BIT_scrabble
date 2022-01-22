package fr.Adrien1106.BIT_scrabble.util;

public enum Align {
	VERTICAL(Direction.RIGHT),
	HORIZONTAL(Direction.DOWN);

	private Direction direction;
	
	Align(Direction direction) {
		this.direction = direction;
	}
	
	public Direction getDirection() {
		return direction;
	}
	
}
