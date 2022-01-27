package fr.Adrien1106.BIT_scrabble.main;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class SccrableGUI extends Application {

	public static void main(String[] args) {
        launch(args);
    }
	
	public void started(Stage primaryStage) {

        Image image = new Image(this.getClass().getResourceAsStream("/Screenshot 2022-01-27 021149.jpg"));
        ImageView imageView = new ImageView(image);
        imageView.setX(200); 
        imageView.setY(200); 
        
        Image image2 = new Image(this.getClass().getResourceAsStream("/Sans titre.png"));
        ImageView imageView2 = new ImageView(image2);
        imageView2.setX(200); 
        imageView2.setY(200); 

        Pane pane = new Pane();
        pane.getChildren().add(imageView);
        pane.getChildren().add(imageView2);
        
        Scene scene = new Scene(pane, 1000, 1000);

        primaryStage.setScene(scene);

        primaryStage.show();
    }

    public void start(Stage primaryStage) {

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
