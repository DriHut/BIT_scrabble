package fr.Adrien1106.BIT_scrabble.main;

import fr.Adrien1106.BIT_scrabble.client.ClientGame;
import fr.Adrien1106.BIT_scrabble.client.GUI.BoardPane;
import fr.Adrien1106.BIT_scrabble.client.GUI.CommandPane;
import fr.Adrien1106.BIT_scrabble.client.GUI.RackPane;
import fr.Adrien1106.BIT_scrabble.client.GUI.ScorePane;
import fr.Adrien1106.BIT_scrabble.game.Player;
import fr.Adrien1106.BIT_scrabble.util.Align;
import fr.Adrien1106.BIT_scrabble.util.words.Dictionary;
import fr.Adrien1106.util.exceptions.CantPlaceWordHereException;
import fr.Adrien1106.util.exceptions.WordOutOfBoundsException;
import fr.Adrien1106.util.exceptions.WrongCoordinateException;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class SccrableGUI extends Application {

	private Pane root_pane;
	private BoardPane board_pane;
	private RackPane rack_pane;
	private ScorePane score_pane;
	private CommandPane command_pane;
	private Stage stage;
	private ClientGame game;
	
	public static void main(String[] args) {
        launch(args);
    }
	
	public void start(Stage primaryStage) {
		stage = primaryStage;
        root_pane = new Pane();
        game = ClientGame.INSTANCE;
        //new Thread(game).start();

		Dictionary.loadFromRessource();
        try {
			game.getBoard().place("F8", "h$or$n", Align.HORIZONTAL);
			game.getBoard().saveMove();
			game.setPlayer(new Player("a#1"));
			game.addPlayer("a#1");
			game.addPlayer("a#2");
			game.addPlayer("a#3");
			game.getPlayer().addTiles("abcd$");
		} catch (NumberFormatException | WrongCoordinateException | WordOutOfBoundsException
				| CantPlaceWordHereException e) {e.printStackTrace();}
        
        setup();
        open();
    }
	
	private void setup() {
        Rectangle2D screen_bounds = Screen.getPrimary().getBounds();
        Scene scene = new Scene(root_pane, screen_bounds.getWidth()*0.8, screen_bounds.getHeight()*0.8);
        scene.setFill(References.BACKGROUND_COLOR);
        
        boardSetup();
        rackSetup();
        scoringSetup();
        cmdSetup();

        stage.setScene(scene);
	}

	private void boardSetup() {
		board_pane = new BoardPane();
        root_pane.getChildren().add(board_pane);
        double width = root_pane.getWidth()*0.5;
        double height = root_pane.getHeight()*0.9;
        board_pane.setMinSize(width, height);
        board_pane.setMaxSize(width, height);
        board_pane.setLayoutX(0.1*root_pane.getWidth());
        board_pane.updateScale();
        
        ChangeListener<Number> stageSizeListener = (observable, oldValue, newValue) -> {
            double v_width = root_pane.getWidth()*0.5;
            double v_height = root_pane.getHeight()*0.9;
            board_pane.setMinSize(v_width, v_height);
            board_pane.setMaxSize(v_width, v_height);
            board_pane.setLayoutX(0.1*root_pane.getWidth());
        	board_pane.updateScale();
        };

        stage.widthProperty().addListener(stageSizeListener);
        stage.heightProperty().addListener(stageSizeListener);
	}
	
	private void rackSetup() {
		rack_pane = new RackPane();
        root_pane.getChildren().add(rack_pane);
        double width = root_pane.getWidth()*0.5;
        double height = root_pane.getHeight()*0.1;
        rack_pane.setMinSize(width, height);
        rack_pane.setMaxSize(width, height);
        rack_pane.setLayoutY(0.9*root_pane.getHeight());
        rack_pane.setLayoutX(0.1*root_pane.getWidth());
        rack_pane.updateScale();
        
        ChangeListener<Number> stageSizeListener = (observable, oldValue, newValue) -> {
            double v_width = root_pane.getWidth()*0.5;
            double v_height = root_pane.getHeight()*0.1;
            rack_pane.setMinSize(v_width, v_height);
            rack_pane.setMaxSize(v_width, v_height);
            rack_pane.setLayoutY(0.9*root_pane.getHeight());
            rack_pane.setLayoutX(0.1*root_pane.getWidth());
            rack_pane.updateScale();
        };

        stage.widthProperty().addListener(stageSizeListener);
        stage.heightProperty().addListener(stageSizeListener);
	}
	
	private void scoringSetup() {
		score_pane = new ScorePane();
        root_pane.getChildren().add(score_pane);
        double width = root_pane.getWidth()*0.4;
        double height = root_pane.getHeight()*0.6;
        score_pane.setMinSize(width, height);
        score_pane.setMaxSize(width, height);
        score_pane.setLayoutX(0.6*root_pane.getWidth());
        score_pane.updateScale();
        
        ChangeListener<Number> stageSizeListener = (observable, oldValue, newValue) -> {
            double v_width = root_pane.getWidth()*0.4;
            double v_height = root_pane.getHeight()*0.6;
            score_pane.setMinSize(v_width, v_height);
            score_pane.setMaxSize(v_width, v_height);
            score_pane.setLayoutX(0.6*root_pane.getWidth());
            score_pane.updateScale();
        };

        stage.widthProperty().addListener(stageSizeListener);
        stage.heightProperty().addListener(stageSizeListener);
	}
	
	private void cmdSetup() {
		command_pane = new CommandPane();
        root_pane.getChildren().add(command_pane);
        double width = root_pane.getWidth()*0.4;
        double height = root_pane.getHeight()*0.4;
        command_pane.setMinSize(width, height);
        command_pane.setMaxSize(width, height);
        command_pane.setLayoutX(0.6*root_pane.getWidth());
        command_pane.setLayoutY(0.6*root_pane.getHeight());
        command_pane.updateScale();
        
        ChangeListener<Number> stageSizeListener = (observable, oldValue, newValue) -> {
            double v_width = root_pane.getWidth()*0.4;
            double v_height = root_pane.getHeight()*0.4;
            command_pane.setMinSize(v_width, v_height);
            command_pane.setMaxSize(v_width, v_height);
            command_pane.setLayoutX(0.6*root_pane.getWidth());
            command_pane.setLayoutY(0.6*root_pane.getHeight());
            command_pane.updateScale();
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
	
	public void starting(Stage primaryStage) {

        Image image = new Image(this.getClass().getResourceAsStream("/sprites/background_tile.png"));
        ImageView imageView = new ImageView(image);
        imageView.setX(100); 
        imageView.setY(100);
        
        Image image2 = new Image(this.getClass().getResourceAsStream("/sprites/background_tile.png"));
        ImageView imageView2 = new ImageView(image2);
        imageView2.setX(100 + 128); 
        imageView2.setY(100); 

        Pane pane = new Pane();
        pane.getChildren().add(imageView);
        pane.getChildren().add(imageView2);
        pane.setScaleX(0.5);
        pane.setScaleY(0.5);
        pane.setBackground(new Background(new BackgroundFill(Color.valueOf("#005955"), null, null)));
        
        Scene scene = new Scene(pane, 1000, 1000);
        
        System.out.println(pane.getHeight());
        
        primaryStage.setScene(scene);

        primaryStage.show();
    }

    public void started(Stage primaryStage) {

        Circle circle = new Circle(300, 300, 125);
        // circle.setStroke(Color.valueOf("#ff00ff"));
        // circle.setStrokeWidth(5);
        circle.setFill(Color.AZURE);

        Rectangle rectangle = new Rectangle(200, 200, 300, 400);
        rectangle.setStroke(Color.TRANSPARENT);
        rectangle.setFill(Color.valueOf("#005955"));

        Pane pane = new Pane();
        pane.getChildren().add(rectangle);
        pane.getChildren().add(circle);

        Scene scene = new Scene(pane, 1024, 800, true);
        primaryStage.setScene(scene);
        primaryStage.setTitle("2D Example");

        primaryStage.show();
    }

}
