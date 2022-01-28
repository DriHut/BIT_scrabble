package fr.Adrien1106.BIT_scrabble.client.GUI;

import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class CommandPane extends Pane {
	
	private Font font;
	private Font sub_font;
	
	private TextField input;
	
	public CommandPane() {
		setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));
		input = new TextField("where");
	}
	
	public void updateScale() {
		input.setMaxSize(getWidth() * 0.9, getHeight()*0.1);
		input.setMinSize(getWidth() * 0.9, getHeight()*0.1);
		input.setLayoutX(getWidth()*0.1/2);
		input.setLayoutY(getHeight()*0.8);
	}
}
