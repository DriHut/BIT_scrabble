package fr.Adrien1106.BIT_scrabble.util.IO.configuration.exceptions;

public class UnknownChildException extends ConfigException {

	private static final long serialVersionUID = 6995491264980119825L;
	
	public UnknownChildException(String child_name) {
		super("the child " + child_name + " doesn't exist");
	}

}
