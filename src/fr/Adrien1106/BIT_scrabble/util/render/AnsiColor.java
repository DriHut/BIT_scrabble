package fr.Adrien1106.BIT_scrabble.util.render;

public enum AnsiColor {
	RESET("\u001b[0m"),
	BOLD("\u001b[1m"),
	UNDERLINE("\u001b[4m"),
	TEXT_BLACK("\u001b[30m"),
	TEXT_RED("\u001b[31m"),
	TEXT_GREEN("\u001b[32m"),
	TEXT_YELLOW("\u001b[33m"),
	TEXT_BLUE("\u001b[34m"),
	TEXT_MAGENTA("\u001b[35m"),
	TEXT_CYAN("\u001b[36m"),
	TEXT_WHITE("\u001b[37m"),
	BACK_BLACK("\u001b[40m"),
	BACK_RED("\u001b[41m"),
	BACK_GREEN("\u001b[42m"),
	BACK_YELLOW("\u001b[43m"),
	BACK_BLUE("\u001b[44m"),
	BACK_MAGENTA("\u001b[45m"),
    BACK_CYAN("\u001b[46m"),
   	BACK_WHITE("\u001b[47m");

	private String color_code;
	
	AnsiColor(String color_code) {
		this.color_code = color_code;
	}
	
	public String getCode() {
		return color_code;
	}
	
	public static String removeAll(String message) {
		for (AnsiColor color: AnsiColor.values()) 
			message = message.replace(color.getCode(), "");
		return message;
	}

}
