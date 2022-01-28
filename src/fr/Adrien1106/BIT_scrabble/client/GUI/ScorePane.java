package fr.Adrien1106.BIT_scrabble.client.GUI;

import java.util.ArrayList;
import java.util.List;

import fr.Adrien1106.BIT_scrabble.client.ClientGame;
import fr.Adrien1106.BIT_scrabble.game.Player;
import fr.Adrien1106.BIT_scrabble.main.References;
import fr.Adrien1106.util.interfaces.IPlayer;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class ScorePane extends Pane {
	private double scale = 1d;
	private Font font;
	private Font sub_font;
	
	private List<Node> texts;
	
	public ScorePane() {
		setBackground(new Background(new BackgroundFill(Color.GREEN, null, null)));
		texts = new ArrayList<>();
	}

	public void updateScores() {
		this.texts.forEach((node) -> {
			if (node instanceof ImageView) ((ImageView) node).setImage(null);
		});
		getChildren().removeAll(texts);
		texts.clear();
		
		List<IPlayer> players = ClientGame.INSTANCE.getPlayers();
		String name = "";
		for (int i = 0; i < players.size(); i++) {
			name = ((Player) players.get(i)).getName();
			if (((Player) players.get(i)).getIdentifier().equals(ClientGame.INSTANCE.getPlayer().getIdentifier())) name = "YOU";
			addText(getWidth()*0.1,                              (getHeight() - References.BASE_SIZE * (players.size()-i) * 4 * scale)/2, font, References.TEXT_COLOR, name.toUpperCase());
			addText(getWidth()*0.1 + References.BASE_SIZE*scale, (getHeight() - (References.BASE_SIZE * (players.size()-i) * 4 - References.BASE_SIZE * 2) * scale)/2 - References.BASE_SIZE/2 * scale, sub_font, References.TEXT_COLOR, "score: " + ((Player) players.get(i)).getScore());
		}
	}
	
	private void addText(double x, double y, Font ft, Color color, String msg) {
		if (ft == null) return;
		Text text = new Text(x, y, msg);
        text.setFont(ft);
        text.setFill(color);
        getChildren().add(text);
		texts.add(text);
	}

	public void updateScale() {
		double min_dim = getHeight()/(ClientGame.INSTANCE.getPlayers().size() + 1);
		if (getWidth()/20 < min_dim) min_dim = getWidth()/20;

		double normal_size = References.BASE_SIZE * 1.5;
		scale = min_dim / normal_size;
		
		font = Font.loadFont(getClass().getResourceAsStream("/PTSerif-Regular.ttf"), (References.BASE_SIZE)*scale);
		sub_font = Font.loadFont(getClass().getResourceAsStream("/PTSerif-Regular.ttf"), (References.BASE_SIZE/2)*scale);
		updateScores();
	}
}
