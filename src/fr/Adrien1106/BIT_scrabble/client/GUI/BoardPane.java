package fr.Adrien1106.BIT_scrabble.client.GUI;

import java.util.ArrayList;
import java.util.List;

import fr.Adrien1106.BIT_scrabble.client.ClientGame;
import fr.Adrien1106.BIT_scrabble.main.References;
import fr.Adrien1106.BIT_scrabble.util.Board;
import fr.Adrien1106.BIT_scrabble.util.Modifier;
import fr.Adrien1106.BIT_scrabble.util.Tile;
import fr.Adrien1106.BIT_scrabble.util.Tiles;
import fr.Adrien1106.BIT_scrabble.util.render.Scalable;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class BoardPane extends Pane implements Scalable {
	
	public static final BoardPane INSTANCE = new BoardPane();
	private double scale = 1d;
	private Font font;
	private Font sub_font;
	private int offset_x = 0;
	private int offset_y = 0;
	
	private List<Node> modifiers;
	private List<Node> tiles;
	
	private BoardPane() {
		// setBackground(new Background(new BackgroundFill(Color.BLUE, null, null)));
		modifiers = new ArrayList<>();
		tiles = new ArrayList<>();
	}

	/**
	 * Render the board background
	 */
	public void renderBoard() {
		Platform.runLater( () -> {
			modifiers.forEach((node) -> {
				if (node instanceof ImageView) ((ImageView) node).setImage(null);
			});
			getChildren().removeAll(modifiers);
			modifiers.clear();
		});
		
		Modifier[][] modifiers = ClientGame.INSTANCE.getBoard().getModifiers();
		for(int x = 0; x < Board.SIZE; x++)
			for(int y = 0; y < Board.SIZE; y++)
				if (modifiers[x][y].equals(Modifier.NONE)) addModifier((x+1) * References.BASE_SIZE * scale, (y+1) * References.BASE_SIZE * scale, References.NULL_MODIFIER);
		
		for(int x = 0; x < Board.SIZE; x++)
			for(int y = 0; y < Board.SIZE; y++)
				if (!modifiers[x][y].equals(Modifier.NONE)) {
					Image img = null;
					switch (modifiers[x][y]) {
					case TRIPLE_WORD:
						img = References.TW_MODIFIER;
						break;
					case DOUBLE_WORD:
						img = References.DW_MODIFIER;
						break;
					case TRIPLE_LETTER:
						img = References.TL_MODIFIER;
						break;
					case DOUBLE_LETTER:
						img = References.DL_MODIFIER;
						break;
					default:
					}
					double coord_x = ((x+1) * References.BASE_SIZE) - (References.BASE_SIZE/2);
					double coord_y = ((y+1) * References.BASE_SIZE) - (References.BASE_SIZE/2);
					if (x == 0) {
						img = crop((int) (References.BASE_SIZE/2), 0, 0, 0, img);
						coord_x += References.BASE_SIZE/2;
					}
					if (x == Board.SIZE-1) {
						img = crop(0, 0, (int) (References.BASE_SIZE/2), 0, img);
					}
					if (y == 0) {
						img = crop(0, 0, 0, (int) (References.BASE_SIZE/2), img);
						coord_y += References.BASE_SIZE/2;
					}
					if (y == Board.SIZE-1) {
						img = crop(0, (int) (References.BASE_SIZE/2), 0, 0, img);
					}
					addModifier( coord_x * scale, coord_y * scale, img);
				}
		
		String Alpahbet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		for(int i = 0; i < Board.SIZE; i++) {
			addText((i+1.5) * References.BASE_SIZE * scale, References.BASE_SIZE * scale * 0.6, font, References.TEXT_COLOR, Alpahbet.substring(i,i+1));
			addText(References.BASE_SIZE * scale * 0.54, (i+1.5) * References.BASE_SIZE* scale, font, References.TEXT_COLOR, "" + (i+1));
		}
	}
	
	/**
	 * Update the board tiles render
	 */
	public synchronized void updateTiles() {
		Platform.runLater( () -> {
			tiles.forEach((node) -> {
				if (node instanceof ImageView) ((ImageView) node).setImage(null);
			});
			getChildren().removeAll(tiles);
			tiles.clear();
		});
		
		Tile[][] tiles = ClientGame.INSTANCE.getBoard().getTiles();
		for(int x = 0; x < Board.SIZE; x++)
			for(int y = 0; y < Board.SIZE; y++)
				if (!tiles[x][y].equals(Tiles.EMPTY)) {
					addTile((x+1) * References.BASE_SIZE * scale, (y+1) * References.BASE_SIZE * scale, References.BLANK_TILE);
					addTileText( (x+1.5) * References.BASE_SIZE * scale, (y+1.5) * References.BASE_SIZE * scale, font, References.TILE_COLOR, tiles[x][y].getSubLetter().toUpperCase());
					addTileText( (x+1.85) * References.BASE_SIZE * scale, (y+1.85) * References.BASE_SIZE * scale, sub_font, References.TILE_COLOR, "" + tiles[x][y].getValue());
				}
	}
	
	/**
	 * Crop a given image (used for modifiers on the side)
	 * @param left - left offset
	 * @param up - up offset
	 * @param right - right offset
	 * @param down - down offset
	 * @param img - the image to be cropped
	 * @return
	 */
	private Image crop(int left, int up, int right, int down, Image img) {
		PixelReader reader = img.getPixelReader();
		WritableImage new_image = new WritableImage(reader, left, down, (int)img.getWidth() - left - right, (int)img.getHeight() - up - down);
		return new_image;
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
		view.setX(offset_x + x);
		view.setY(offset_y + y);
		view.setFitHeight(scale*img.getHeight());
		view.setFitWidth(scale*img.getWidth());
		return view;
	}

	private void addModifier(double x, double y, Image img) {
		if (img == null) return;
		ImageView view = getView(x, y, img);
		Platform.runLater( () -> {
			getChildren().add(view);
			modifiers.add(view);
		});
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
		Text text = new Text(msg);
        text.setFont(ft);
        text.setFill(color);
        text.setLayoutX(offset_x + x - text.getLayoutBounds().getWidth()/2);
        text.setLayoutY(offset_y + y + text.getLayoutBounds().getHeight()/4);
        return text;
	}
	
	private void addText(double x, double y, Font ft, Color color, String msg) {
		if (ft == null) return;
		Text text = getText(x, y, ft, color, msg);
		Platform.runLater( () -> {
			getChildren().add(text);
			modifiers.add(text);
		});
	}

	@Override
	public void updateScale() {
		Platform.runLater( () -> {
			getChildren().clear();
		});
		double min_dim = getHeight();
		if (getWidth() < min_dim) min_dim = getWidth();

		double normal_size = References.BASE_SIZE * (Board.SIZE + 2);
		scale = min_dim / normal_size;
		offset_x = (int) (getWidth() - normal_size*scale)/2;
		offset_y = (int) (getHeight() - normal_size*scale)/2;
		
		font = Font.loadFont(getClass().getResourceAsStream("/PTSerif-Regular.ttf"), (References.BASE_SIZE-(References.BASE_SIZE/4))*scale);
		sub_font = Font.loadFont(getClass().getResourceAsStream("/PTSerif-Regular.ttf"), (References.BASE_SIZE/5)*scale);
		renderBoard();
		updateTiles();
	}
}
