package com.wkyle.bankrecord.controllers;

import com.wkyle.bankrecord.application.Main;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import com.wkyle.bankrecord.models.LoginModel;

public class LoginController {

	@FXML
	private TextField txtUsername;

	@FXML
	private PasswordField txtPassword;

	@FXML
	private Label lblError;

	private LoginModel model;

	public LoginController() {
		model = new LoginModel();
	}

	public void login() {

//		model.logAllUsers();
		String username = this.txtUsername.getText();
		String password = this.txtPassword.getText();
		if (checkInputUserNameAndPassword(username, password)) {
			// authentication check
			checkCredentials(username, password);
		}
	}

	public void signup() {
		model.logAllUsers();
		String username = this.txtUsername.getText();
		String password = this.txtPassword.getText();
		if (checkInputUserNameAndPassword(username, password)) {
			if (model.createUser(username,password)) {
				// authentication check
				checkCredentials(username, password);
			}
		}
	}

	public boolean checkInputUserNameAndPassword(String username, String password) {
		lblError.setText("");

		// Validations
		if (username == null || username.trim().equals("")) {
			lblError.setText("Username Cannot be empty or spaces");
			return false;
		}
		if (password == null || password.trim().equals("")) {
			lblError.setText("Password Cannot be empty or spaces");
			return false;
		}
		if (username == null || username.trim().equals("") && (password == null || password.trim().equals(""))) {
			lblError.setText("User name / Password Cannot be empty or spaces");
			return false;
		}
		return true;
	}

	public void checkCredentials(String username, String password) {
		Boolean isValid = model.getCredentials(username, password);
		if (!isValid) {
			lblError.setText("User does not exist!");
			return;
		}
		try {
			AnchorPane root;
			if (model.isAdmin() && isValid) {
				// If user is admin, inflate admin view

				root = (AnchorPane) FXMLLoader.load(getClass().getResource("/com/wkyle/bankrecord/AdminView.fxml"));
				Main.stage.setTitle("Admin View");

			} else {
				// If user is customer, inflate customer view

				root = (AnchorPane) FXMLLoader.load(getClass().getResource("/com/wkyle/bankrecord/ClientView.fxml"));
				// ***Set user ID acquired from db****
				int user_id = model.getId();  
				ClientController.setUserid(user_id);
				Main.stage.setTitle("Client View");
			}

			Scene scene = new Scene(root);
			Main.stage.setScene(scene);

		} catch (Exception e) {
			System.out.println("Error occured while inflating view: " + e);
		}

	}
}