package fr.Adrien1106.BIT_scrabble.util;

public enum Align {
	VERTICAL(Direction.DOWN),
	HORIZONTAL(Direction.RIGHT);

	private Direction direction;
	
	Align(Direction direction) {
		this.direction = direction;
	}
	
	/**
	 * get the direction of the align
	 * @return the direction of the align
	 */
	public Direction getDirection() {
		return direction;
	}
	
	/**
	 * get the other align
	 * @return the other align
	 */
	public Align other() {
		return this.equals(HORIZONTAL)? VERTICAL: HORIZONTAL;
	}
}
