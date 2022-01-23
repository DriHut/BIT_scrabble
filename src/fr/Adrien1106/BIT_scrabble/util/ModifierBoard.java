package fr.Adrien1106.BIT_scrabble.util;

public enum ModifierBoard {
BOARD_I(new String[][] {
	{"TW", "  ", "  ", "DL", "  ", "  ", "  ", "TW"},
	{"  ", "DW", "  ", "  ", "  ", "TL", "  ", "  "},
	{"  ", "  ", "DW", "  ", "  ", "  ", "DL", "  "},
	{"DL", "  ", "  ", "DW", "  ", "  ", "  ", "DL"},
	{"  ", "  ", "  ", "  ", "DW", "  ", "  ", "  "},
	{"  ", "TL", "  ", "  ", "  ", "TL", "  ", "  "},
	{"  ", "  ", "DL", "  ", "  ", "  ", "DL", "  "},
	{"TW", "  ", "  ", "DL", "  ", "  ", "  ", "DW"},
});

	private String[][] string_board;
	
	ModifierBoard(String[][] string_board) {
		this.string_board = string_board;
	}
	
	/**
	 * Gives the string board
	 * @return board of strings modifier
	 */
	public String[][] getBoard(){
		return string_board;
	}
	
	/**
	 * Mirror a Modifier board to the rest of the board considering it is a quarter of the the whole board
	 * @param size - size of the output board
	 * @param modifier_board - board template to apply
	 * @return the board mirrored to the other quarters
	 */
	public static String[][] spreadBoard(int size, ModifierBoard modifier_board) {
		if (modifier_board.getBoard().length < size/2f) return null;
		String[][] new_board = new String[size][size];
		String[][] board = modifier_board.getBoard();
		
		for (int i = 0; i < size/2f; i++) {
			for (int j = 0; j < size/2f; j++) {
				new_board[i           ][j           ] = board[i][j];
				new_board[(size-1) - i][j           ] = board[i][j];
				new_board[(size-1) - i][(size-1) - j] = board[i][j];
				new_board[i           ][(size-1) - j] = board[i][j];
			}
		}
		return new_board;
	}
	
	/**
	 * Converts a board of modifier strings to the correct modifiers
	 * @param template - the board of strings
	 * @return the associated modifier board
	 */
	public static Modifier[][] convertFromString(String[][] template) {
		final int size = template.length;
		Modifier[][] board = new Modifier[size][size];

		for (int i = 0; i < size; i++)
			for (int j = 0; j < size; j++)
				board[i][j] = Modifier.fromId(template[i][j]);
		
		return board;
	}
}
