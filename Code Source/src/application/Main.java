package application;
	
import java.io.IOException;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;


public class Main extends Application {
	
	public static double xOffset = 0;
	public static double yOffset = 0;
    
	public static EventHandler<MouseEvent> handlerPressed;
	public static EventHandler<MouseEvent> handlerDragged;
	
	public static Stage stage = new Stage();
	
	public static boolean ad = false;
	
	@Override
	public void start(Stage s) throws IOException {
			stage = s;
			Scene loginScene;
			Parent root = FXMLLoader.load(getClass().getResource("GUI/Login.fxml"));
			root.setOnMousePressed(handlerPressed = new EventHandler<MouseEvent>() {
	            @Override
	            public void handle(MouseEvent event) {
	                xOffset = event.getSceneX();
	                yOffset = event.getSceneY();
	            }
	        });
	        root.setOnMouseDragged(handlerDragged = new EventHandler<MouseEvent>() {
	            @Override
	            public void handle(MouseEvent event) {
	            	stage.setX(event.getScreenX() - xOffset);
	            	stage.setY(event.getScreenY() - yOffset);
	            }
	        });

			loginScene = new Scene(root);
			stage.setScene(loginScene);
			stage.initStyle(StageStyle.UNDECORATED);
			stage.show();
			stage.centerOnScreen();
	}
	
	
	public static void main(String[] args) {
		launch(args);
	}
}
