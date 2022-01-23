package fr.Adrien1106.BIT_scrabble.exceptions;

public class CantPlaceWordHereException extends Exception {

	private static final long serialVersionUID = -6430073858160677775L;
	
	public CantPlaceWordHereException() {
		super("the placing of the word hasn't been authorised");
	}

}
