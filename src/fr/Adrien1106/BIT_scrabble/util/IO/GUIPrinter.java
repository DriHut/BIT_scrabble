package fr.Adrien1106.BIT_scrabble.util.IO;

import fr.Adrien1106.BIT_scrabble.client.GUI.CommandPane;
import fr.Adrien1106.BIT_scrabble.util.render.AnsiColor;
import javafx.application.Platform;

public class GUIPrinter implements Printer {

	@Override
	public void println(String msg) {
		print(msg + "\n");
	}

	@Override
	public void print(String msg) {
		Platform.runLater( () -> CommandPane.INSTANCE.getConsole().appendText(AnsiColor.removeAll(msg)));
	}

	
}
