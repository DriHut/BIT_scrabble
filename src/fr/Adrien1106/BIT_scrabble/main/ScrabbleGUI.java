package fr.Adrien1106.BIT_scrabble.main;

import fr.Adrien1106.BIT_scrabble.client.ClientGame;
import fr.Adrien1106.BIT_scrabble.client.GUI.BoardPane;
import fr.Adrien1106.BIT_scrabble.client.GUI.CommandPane;
import fr.Adrien1106.BIT_scrabble.client.GUI.RackPane;
import fr.Adrien1106.BIT_scrabble.client.GUI.ScorePane;
import fr.Adrien1106.BIT_scrabble.game.Player;
import fr.Adrien1106.BIT_scrabble.util.Align;
import fr.Adrien1106.BIT_scrabble.util.IO.GUIPrinter;
import fr.Adrien1106.BIT_scrabble.util.render.Scalable;
import fr.Adrien1106.BIT_scrabble.util.words.Dictionary;
import fr.Adrien1106.util.exceptions.CantPlaceWordHereException;
import fr.Adrien1106.util.exceptions.WordOutOfBoundsException;
import fr.Adrien1106.util.exceptions.WrongCoordinateException;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class ScrabbleGUI extends Application {

	private Pane root_pane;
	private BoardPane board_pane;
	private RackPane rack_pane;
	private ScorePane score_pane;
	private CommandPane command_pane;
	private Stage stage;
	
	public static void main(String[] args) {
		ClientGame.out = new GUIPrinter();
		ClientGame.HAS_GUI = true;
        launch(args);
    }
	
	public void start(Stage primaryStage) {
		stage = primaryStage;
        root_pane = new Pane();
        ClientGame game = ClientGame.INSTANCE;
        new Thread(game).start();

		/*Dictionary.loadFromRessource();
        try {
			game.getBoard().place("F8", "h$or$n", Align.HORIZONTAL);
			game.getBoard().saveMove();
			game.setPlayer(new Player("a#1"));
			game.addPlayer("a#1");
			game.addPlayer("a#2");
			game.getPlayer().addTiles("abcd$");
		} catch (NumberFormatException | WrongCoordinateException | WordOutOfBoundsException
				| CantPlaceWordHereException e) {e.printStackTrace();}*/
        
        setup();
        open();
    }
	
	private void setup() {
        Rectangle2D screen_bounds = Screen.getPrimary().getBounds();
        Scene scene = new Scene(root_pane, screen_bounds.getWidth()*0.8, screen_bounds.getHeight()*0.8);
        root_pane.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, null, null)));
        scene.setFill(References.BACKGROUND_COLOR);
        
        // board render
		board_pane = BoardPane.INSTANCE;
		setupPropotionalPane(board_pane, 0.5, 0.9, 0.1, 0);
		
		// rack render
		rack_pane = RackPane.INSTANCE;
		setupPropotionalPane(rack_pane, 0.5, 0.1, 0.1, 0.9);

		// scoring render
		score_pane = ScorePane.INSTANCE;
		setupPropotionalPane(score_pane, 0.4, 0.6, 0.6, 0);

		// command input pane
		command_pane = CommandPane.INSTANCE;
		setupPropotionalPane(command_pane, 0.4, 0.4, 0.6, 0.6);
;
        stage.setScene(scene);
	}
	
	private void setupPropotionalPane(Pane pane, double width_prop, double height_prop, double x_offset_prop, double y_offset_prop) {
		root_pane.getChildren().add(pane);
        double width = root_pane.getWidth()*width_prop;
        double height = root_pane.getHeight()*height_prop;
        pane.setMinSize(width, height);
        pane.setMaxSize(width, height);
        pane.setLayoutX(x_offset_prop*root_pane.getWidth());
        pane.setLayoutY(y_offset_prop*root_pane.getHeight());
        if (pane instanceof Scalable) ((Scalable) pane).updateScale();
        
        ChangeListener<Number> stageSizeListener = (observable, oldValue, newValue) -> {
            double v_width = root_pane.getWidth()*width_prop;
            double v_height = root_pane.getHeight()*height_prop;
            pane.setMinSize(v_width, v_height);
            pane.setMaxSize(v_width, v_height);
            pane.setLayoutX(x_offset_prop*root_pane.getWidth());
            pane.setLayoutY(y_offset_prop*root_pane.getHeight());
            if (pane instanceof Scalable) ((Scalable) pane).updateScale();
        };

        stage.widthProperty().addListener(stageSizeListener);
        stage.heightProperty().addListener(stageSizeListener);
	}
	
	private void open() {
		stage.getIcons().add(References.ICON);
        stage.setTitle("Scrabble Lite");
        stage.setAlwaysOnTop(true);
        stage.show();
        stage.setAlwaysOnTop(false);
        stage.show();
	}
	
	@Override
	public void stop(){
		ClientGame.IS_RUNNING = false;
        System.exit(0);
	}

}
