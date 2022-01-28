package fr.Adrien1106.BIT_scrabble.client.GUI;

import java.util.ArrayList;
import java.util.List;

import fr.Adrien1106.BIT_scrabble.client.ClientGame;
import fr.Adrien1106.BIT_scrabble.main.References;
import fr.Adrien1106.BIT_scrabble.util.render.Scalable;
import fr.Adrien1106.util.protocol.ProtocolMessages;
import javafx.application.Platform;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class CommandPane extends Pane implements Scalable {
	
	public static final CommandPane INSTANCE = new CommandPane();
	private Font font;
	private Font sub_font;

	private Text console_title;
	private TextArea console;
	
	private Text input_title;
	private TextField input;
	
	private List<String> possibilities = new ArrayList<>();
	private int last_key;
	
	private CommandPane() {
		// setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));
		console_title = new Text("Console:");
		console_title.setFill(References.TEXT_COLOR);
		console = new TextArea();
		console.setEditable(false);
		console.setStyle("-fx-control-inner-background: #515658;");

		input_title = new Text("Command Input:");
		input_title.setFill(References.TEXT_COLOR);
		input = new TextField();
		input.setStyle("-fx-control-inner-background: #515658;");
		input.setOnKeyPressed(event -> handleInput(event));
		Platform.runLater(() -> input.requestFocus());
		
		getChildren().add(console_title);
		getChildren().add(console);
		getChildren().add(input_title);
		getChildren().add(input);
	}
	
	private void handleInput(KeyEvent event) {
		switch (event.getCode()) {
		case ENTER:
			if (possibilities.size() != 0) {
				possibilities.clear();
				break;
			}
			String text = input.getText();
			ClientGame.INSTANCE.handleInput(text);
			input.setText("");
			break;
		case TAB:
			if (input.getText().endsWith(";")) possibilities.clear();
			String txt = (input.getText().endsWith(";")?input.getText() + " ": input.getText());
			String[] cmd = txt.split(";");
			if (possibilities.size() == 0) getAutocomplete(cmd, cmd.length);
			if (possibilities.size() != 0) cmd[cmd.length - 1] = getNextEntry(cmd[cmd.length - 1]);
			input.setText(format(cmd));
			input.selectEnd();
			input.deselect();
			event.consume();
			break;
		default:
			break;
		}
	}

	private String getNextEntry(String origin) {
		if (possibilities.isEmpty()) return origin;
		if (last_key + 1 < possibilities.size()) last_key++;
		else last_key = 0;
		return possibilities.get(last_key).replace(" ", "");
	}

	private String format(String[] cmd) {
		String output = cmd[0];
		for (int i = 1; i < cmd.length; i ++) 
			output += ";" + cmd[i];
		return output;
	}

	private void getAutocomplete(String[] entry, int arg_num) {
		switch (arg_num) {
		case 1:
			if (entry[0].length() == 0) {
				possibilities.add(ProtocolMessages.MAKE_MOVE);
				possibilities.add(ProtocolMessages.REPLACE_TILES);
				possibilities.add(ProtocolMessages.SKIP_TURN);
				possibilities.add(ProtocolMessages.JOIN_ROOM);
				possibilities.add(ProtocolMessages.CREATE_ROOM);
				possibilities.add("/fs");
				possibilities.add("/fx");
			}
			if (entry[0].startsWith(ProtocolMessages.CUSTOM_COMMAND)) {
				if ("/fs".startsWith(entry[0])) possibilities.add("/fs");
				if ("/fx".startsWith(entry[0])) possibilities.add("/fx");
			}
			if (ProtocolMessages.CREATE_ROOM.startsWith(entry[0])) possibilities.add(ProtocolMessages.CREATE_ROOM);
			if (ProtocolMessages.JOIN_ROOM.startsWith(entry[0])) possibilities.add(ProtocolMessages.JOIN_ROOM);
			if (ProtocolMessages.MAKE_MOVE.equals(entry[0]) 
					|| ProtocolMessages.REPLACE_TILES.equals(entry[0])
					|| ProtocolMessages.SKIP_TURN.equals(entry[0])
					|| ProtocolMessages.JOIN_ROOM.equals(entry[0])
					|| ProtocolMessages.CREATE_ROOM.equals(entry[0])
					|| "/fs".equals(entry[0])
					|| "/fx".equals(entry[0]))
				possibilities.add(entry[0] + ";");
			break;
		case 2:
			switch (entry[0]) {
			case ProtocolMessages.MAKE_MOVE:
				if (entry[1].equals(" ")) {
					possibilities.add("horizontal");
					possibilities.add("vertical");
				}
				if ("horizontal".startsWith(entry[1])) possibilities.add("horizontal");
				if ("vertical".startsWith(entry[1])) possibilities.add("vertical");
				if ("horizontal".equals(entry[1])
						|| "vertical".equals(entry[1]))
					possibilities.add(entry[1] + ";");
				break;
			case ProtocolMessages.REPLACE_TILES:
				possibilities.add("<tiles-to-replace>");
				possibilities.add(entry[1]);
				break;
			case ProtocolMessages.JOIN_ROOM:
				possibilities.add("<room-id>");
				possibilities.add(entry[1]);
				break;
			case ProtocolMessages.CREATE_ROOM:
				possibilities.add("<max-amount-player>");
				possibilities.add(entry[1]);
				break;
			default:
				break;
			}
			break;
		case 3:
			switch (entry[0]) {
			case ProtocolMessages.MAKE_MOVE:
				possibilities.add("<coordinate>");
				possibilities.add(entry[2]);
				break;
			default:
				break;
			}
			break;
		case 4:
			switch (entry[0]) {
			case ProtocolMessages.MAKE_MOVE:
				possibilities.add("<word>");
				possibilities.add(entry[2]);
				break;
			default:
				break;
			}
			break;
		default:
			break;
		}
		last_key = -1;
	}

	@Override
	public void updateScale() {
		console_title.setX(getWidth() * 0.12 / 2);
		console_title.setY(getHeight() * 0.06);
		
		console.setMaxSize(getWidth() * 0.9, getHeight()*0.65);
		console.setMinSize(getWidth() * 0.9, getHeight()*0.65);
		console.setLayoutX(getWidth() * 0.1 / 2);
		console.setLayoutY(getHeight() * 0.07);

		input_title.setX(getWidth() * 0.12 / 2);
		input_title.setY(getHeight() * 0.79);
		
		input.setMaxSize(getWidth() * 0.9, getHeight()*0.1);
		input.setMinSize(getWidth() * 0.9, getHeight()*0.1);
		input.setLayoutX(getWidth() * 0.1 / 2);
		input.setLayoutY(getHeight() * 0.8);
		
		font = Font.loadFont(getClass().getResourceAsStream("/PTSerif-Regular.ttf"), getHeight()*0.06);
		sub_font = Font.loadFont(getClass().getResourceAsStream("/PTSerif-Regular.ttf"), getHeight()*0.04);
		console_title.setFont(font);
		input_title.setFont(font);
		input.setFont(sub_font);
		console.setStyle("-fx-control-inner-background: #515658;-fx-font-size: " + (int)(getHeight()*0.035) + "px;");
	}
	
	public TextArea getConsole() {
		return console;
	}
}
