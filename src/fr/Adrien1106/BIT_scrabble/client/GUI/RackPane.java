package fr.Adrien1106.BIT_scrabble.client.GUI;

import java.util.ArrayList;
import java.util.List;

import fr.Adrien1106.BIT_scrabble.client.ClientGame;
import fr.Adrien1106.BIT_scrabble.main.References;
import fr.Adrien1106.BIT_scrabble.util.Tiles;
import fr.Adrien1106.BIT_scrabble.util.render.Scalable;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class RackPane extends Pane implements Scalable {

	public static final RackPane INSTANCE = new RackPane();
	private double scale = 1d;
	private Font font;
	private Font sub_font;
	
	private List<Node> tiles;
	
	private RackPane() {
		// setBackground(new Background(new BackgroundFill(Color.RED, null, null)));
		tiles = new ArrayList<>();
	}

	public synchronized void updateTiles() {
		Platform.runLater( () -> {
			tiles.forEach((node) -> {
				if (node instanceof ImageView) ((ImageView) node).setImage(null);
			});
			getChildren().removeAll(tiles);
			tiles.clear();
		});
		
		List<Tiles> tiles = ClientGame.INSTANCE.getPlayer().getTilesList();
		for(int i = 0; i < tiles.size(); i++) {
			addTile((getWidth() - (References.BASE_SIZE * tiles.size() * scale))/2 + i*References.BASE_SIZE * scale, (getHeight() - References.BASE_SIZE * scale)/2, References.BLANK_TILE);
			if (!tiles.get(i).getLetter().equals("$")) {
				addTileText((getWidth() - (References.BASE_SIZE * tiles.size() * scale))/2 + ( i   *References.BASE_SIZE + 30) * scale, (getHeight() + References.BASE_SIZE * scale)/2 - 32 * scale, font,     References.TILE_COLOR, tiles.get(i).getLetter().toUpperCase());
				addTileText((getWidth() - (References.BASE_SIZE * tiles.size() * scale))/2 + ((i+1)*References.BASE_SIZE - 25) * scale, (getHeight() + References.BASE_SIZE * scale)/2 - 10 * scale, sub_font, References.TILE_COLOR, "" + tiles.get(i).getValue());
			}
		}
	}
	
	private void addTile(double x, double y, Image img) {
		if (img == null) return;
		ImageView view = getView( x, y, img);
		Platform.runLater( () -> {
			getChildren().add(view);
			tiles.add(view);
		});
	}

	private ImageView getView(double x, double y, Image img) {
		ImageView view = new ImageView(img);
		view.setX(x);
		view.setY(y);
		view.setFitHeight(scale*img.getHeight());
		view.setFitWidth(scale*img.getWidth());
		return view;
	}
	
	private void addTileText(double x, double y, Font ft, Color color, String msg) {
		if (ft == null) return;
		Text text = getText(x, y, ft, color, msg);
		Platform.runLater( () -> {
			getChildren().add(text);
			tiles.add(text);
		});
	}
	
	private Text getText(double x, double y, Font ft, Color color, String msg) {
		Text text = new Text(x, y, msg);
        text.setFont(ft);
        text.setFill(color);
        return text;
	}

	@Override
	public void updateScale() {
		Platform.runLater( () -> {
			getChildren().clear();
		});
		double min_dim = getHeight();
		if (getWidth()/7 < min_dim) min_dim = getWidth()/7;

		double normal_size = References.BASE_SIZE * 1.5;
		scale = min_dim / normal_size;
		
		font = Font.loadFont(getClass().getResourceAsStream("/PTSerif-Regular.ttf"), (References.BASE_SIZE-(References.BASE_SIZE/4))*scale);
		sub_font = Font.loadFont(getClass().getResourceAsStream("/PTSerif-Regular.ttf"), (References.BASE_SIZE/5)*scale);
		updateTiles();
	}
}
