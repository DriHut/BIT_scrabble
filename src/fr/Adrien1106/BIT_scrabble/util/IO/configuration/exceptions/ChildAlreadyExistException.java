package fr.Adrien1106.BIT_scrabble.util.IO.configuration.exceptions;

public class ChildAlreadyExistException extends ConfigException {

	private static final long serialVersionUID = -6589309701399520813L;

	public ChildAlreadyExistException(String child_name) {
		super("the child " + child_name + " already exist");
	}

}
