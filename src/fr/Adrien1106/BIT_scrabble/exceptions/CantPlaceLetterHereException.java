package fr.Adrien1106.BIT_scrabble.exceptions;

public class CantPlaceLetterHereException extends Exception {

	private static final long serialVersionUID = 7734042486906379197L;
	
	public CantPlaceLetterHereException(String letter, String reason) {
		super("the letter '" + letter + "' could not be placed due to:" + reason);
	}

}
