package com.wkyle.bankrecord.application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.net.URL;

public class Main extends Application {

	public static Stage stage; // set global stage object!!!

	@Override
	public void start(Stage primaryStage) {
	
		//primaryStage.hide();

		try {
			stage = primaryStage;
			URL loadURL = getClass().getResource("/com/wkyle/bankrecord/LoginView.fxml");
			AnchorPane root = (AnchorPane) FXMLLoader.load(loadURL);
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("/com/wkyle/bankrecord/styles.css").toExternalForm());
			stage.setTitle("Login View");
			stage.setScene(scene);
			stage.show();

		} catch (Exception e) {
			System.out.println("Error occured while inflating view: " + e);
		}
	}

	public static void main(String[] args) {

		launch(args);
	}
}
