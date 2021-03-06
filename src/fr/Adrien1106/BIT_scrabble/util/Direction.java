package fr.Adrien1106.BIT_scrabble.util;

public enum Direction {
UP(0,-1),
DOWN(0,1),
RIGHT(1,0),
LEFT(-1,0);
	
	private int x;
	private int y;
	
	Direction(int x, int y){
		this.x = x;
		this.y = y;
	}
	
	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
	
	/**
	 * get the opposite direction
	 * @return
	 */
	public Direction opposite() {
		switch (this) {
			case UP: return DOWN;
			case DOWN: return UP;
			case RIGHT: return LEFT;
			case LEFT: return RIGHT;
		}
		return null;
	}
}
