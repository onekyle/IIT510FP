package com.wkyle.bankrecord.controllers;

import com.wkyle.bankrecord.application.Main;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;

public class Router {
    public static void goToLoginView() {
        try {
            AnchorPane root = (AnchorPane) FXMLLoader.load(Router.class.getResource("/com/wkyle/bankrecord/LoginView.fxml"));
            Scene scene = new Scene(root);
            scene.getStylesheets().add(Router.class.getResource("/com/wkyle/bankrecord/styles.css").toExternalForm());
            Main.stage.setScene(scene);
            Main.stage.setTitle("Login View");
        } catch (Exception e) {
            System.out.println("Error occured while inflating view: " + e);
        }
    }

    public static void goToAdminView() {
        try {
            AnchorPane root;
            root = (AnchorPane) FXMLLoader.load(Router.class.getResource("/com/wkyle/bankrecord/AdminView.fxml"));
            Main.stage.setTitle("Admin View");
            Scene scene = new Scene(root);
            Main.stage.setScene(scene);
        } catch (Exception e) {
            System.out.println("Error occured while inflating view: " + e);
        }
    }

    public static void goToAccountManagerView() {
        try {
            AnchorPane root;
            root = (AnchorPane) FXMLLoader.load(Router.class.getResource("/com/wkyle/bankrecord/AccountManager.fxml"));
            Main.stage.setTitle("AccountManger View");
            Scene scene = new Scene(root);
            Main.stage.setScene(scene);
        } catch (Exception e) {
            System.out.println("Error occured while inflating view: " + e);
        }
    }

    public static void goToClientView(int cid) {
        try {
            AnchorPane root;
            root = (AnchorPane) FXMLLoader.load(Router.class.getResource("/com/wkyle/bankrecord/ClientView.fxml"));
            // ***Set user ID acquired from db****
            int user_id = cid;
            ClientController.setUserid(user_id);
            Main.stage.setTitle("Client View");

            Scene scene = new Scene(root);
            Main.stage.setScene(scene);
        } catch (Exception e) {
            System.out.println("Error occured while inflating view: " + e);
        }

    }
}

