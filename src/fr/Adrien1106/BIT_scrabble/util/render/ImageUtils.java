package fr.Adrien1106.BIT_scrabble.util.render;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ImageUtils {

	public static Image loadImage(String url) {
		return new Image(ImageUtils.class.getResourceAsStream(url));
	}
	
	public static void scale(ImageView img, double scale) {
		img.setScaleX(scale);
		img.setScaleY(scale);
	}

}
