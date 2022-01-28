package fr.Adrien1106.BIT_scrabble.main;

import fr.Adrien1106.BIT_scrabble.util.render.ImageUtils;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class References {
	
	public static final String PROG_ID = "BIT_Scrabble";
	
	public static final int MAX_NAME_LENGTH = 10;
	public static final int MAX_PLAYERS = 4;
	public static final int MIN_PLAYERS = 1;
	
	public static final Color BACKGROUND_COLOR = Color.valueOf("#005955");
	public static final Image ICON = ImageUtils.loadImage("/sprites/icon.png");
	
	public static final Color TEXT_COLOR = Color.valueOf("#ffffff");
	public static final Color TILE_COLOR = Color.valueOf("#000000");
	public static final Image BLANK_TILE = ImageUtils.loadImage("/sprites/blank_tile.png");
	public static final Image NULL_MODIFIER = ImageUtils.loadImage("/sprites/background_tile.png");
	public static final Image TW_MODIFIER = ImageUtils.loadImage("/sprites/triple_word_modifier.png");
	public static final Image DW_MODIFIER = ImageUtils.loadImage("/sprites/double_word_modifier.png");
	public static final Image TL_MODIFIER = ImageUtils.loadImage("/sprites/triple_letter_modifier.png");
	public static final Image DL_MODIFIER = ImageUtils.loadImage("/sprites/double_letter_modifier.png");
	public static final double BASE_SIZE = 128d;

}
