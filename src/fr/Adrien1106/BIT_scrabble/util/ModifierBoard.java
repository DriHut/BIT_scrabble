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
	
	public String[][] getBoard(){
		return string_board;
	}
	
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
		
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				System.out.print(new_board[i][j] + ",");
			}
			System.out.println("");
		}
		return new_board;
	}
}
