package fr.Adrien1106.BIT_scrabble.client.GUI;

import java.util.ArrayList;
import java.util.List;

import fr.Adrien1106.BIT_scrabble.client.ClientGame;
import fr.Adrien1106.BIT_scrabble.game.Player;
import fr.Adrien1106.BIT_scrabble.main.References;
import fr.Adrien1106.BIT_scrabble.util.render.Scalable;
import fr.Adrien1106.util.interfaces.IPlayer;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class ScorePane extends Pane implements Scalable {

	public static final ScorePane INSTANCE = new ScorePane();
	private double scale = 1d;
	private Font font;
	private Font sub_font;
	
	private List<Node> texts;
	
	private ScorePane() {
		// setBackground(new Background(new BackgroundFill(Color.GREEN, null, null)));
		texts = new ArrayList<>();
	}

	/**
	 * Updates the score panel
	 */
	public synchronized void updateScores() {
		Platform.runLater( () -> {
			getChildren().removeAll(texts);
			texts.clear();
		});
		
		List<IPlayer> players = ClientGame.INSTANCE.getPlayers();
		Player current_player = ClientGame.INSTANCE.getCurrentPlayer();
		String name = "";
		for (int i = 0; i < players.size(); i++) {
			name = ((Player) players.get(i)).getName();
			if (players.get(i).equals(ClientGame.INSTANCE.getPlayer())) name = "You";
			if (players.get(i).equals(current_player)) name = "> " + name;
			double y = getHeight()/2 + (-players.size() + i*2 + 1) * References.BASE_SIZE * scale;
			addText(getWidth()*0.1,                              y, font, References.TEXT_COLOR, name);
			addText(getWidth()*0.1 + References.BASE_SIZE*scale, y + References.BASE_SIZE/2 * scale, sub_font, References.TEXT_COLOR, "score: " + ((Player) players.get(i)).getScore());
		}
	}
	
	/**
	 * add a text component
	 * @param x - x position
	 * @param y - y position
	 * @param ft - font
	 * @param color - colour of the text
	 * @param msg - the text to put on the label
	 */
	private void addText(double x, double y, Font ft, Color color, String msg) {
		if (ft == null) return;
		Text text = new Text(x, y, msg);
        text.setFont(ft);
        text.setFill(color);
        Platform.runLater( () -> {
        	getChildren().add(text);
    		texts.add(text);
        });
	}

	@Override
	public void updateScale() {
		Platform.runLater( () -> {
			getChildren().clear();
		});
		double min_dim = getHeight()/((ClientGame.INSTANCE.getPlayers().size())*2 + 1);
		if (getWidth()/10 < min_dim) min_dim = getWidth()/10;

		double normal_size = References.BASE_SIZE;
		scale = min_dim / normal_size;
		
		font = Font.loadFont(getClass().getResourceAsStream("/PTSerif-Regular.ttf"), (References.BASE_SIZE)*scale);
		sub_font = Font.loadFont(getClass().getResourceAsStream("/PTSerif-Regular.ttf"), (References.BASE_SIZE/2)*scale);
		updateScores();
	}
}
