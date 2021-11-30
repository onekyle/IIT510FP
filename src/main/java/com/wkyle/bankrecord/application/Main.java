package com.wkyle.bankrecord.application;

import com.wkyle.bankrecord.controllers.Router;
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
		stage = primaryStage;
		Router.goToLoginView();
		stage.show();

	}

	public static void main(String[] args) {

		launch(args);
	}
}
