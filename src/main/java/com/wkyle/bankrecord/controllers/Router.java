package com.wkyle.bankrecord.controllers;

import com.wkyle.bankrecord.application.Main;
import com.wkyle.bankrecord.models.AccountModel;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;

public class Router {
    public static void goToLoginView() {
        try {
            AnchorPane root = (AnchorPane) FXMLLoader.load(Router.class.getResource("/com/wkyle/bankrecord/views/LoginView.fxml"));
            Scene scene = new Scene(root);
            addCssForScene(scene);
            Main.stage.setScene(scene);
            Main.stage.setTitle("Login View");
        } catch (Exception e) {
            System.out.println("Error occured while inflating view: " + e);
        }
    }

    public static void goToAdminView() {
        try {
            AnchorPane root;
            root = (AnchorPane) FXMLLoader.load(Router.class.getResource("/com/wkyle/bankrecord/views/AdminView.fxml"));
            Main.stage.setTitle("Admin View");
            Scene scene = new Scene(root);
            addCssForScene(scene);
            Main.stage.setScene(scene);
        } catch (Exception e) {
            System.out.println("Error occured while inflating view: " + e);
        }
    }

    public static void goToManagerView() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Router.class.getResource("/com/wkyle/bankrecord/views/AdminView.fxml"));
            AnchorPane root = fxmlLoader.load();
            AdminController controller = (AdminController) fxmlLoader.getController();
            controller.setManagerType(AccountModel.RoleType.ACCOUNT_MANAGER);
            Main.stage.setTitle("Admin View");
            Scene scene = new Scene(root);
            addCssForScene(scene);
            Main.stage.setScene(scene);
        } catch (Exception e) {
            System.out.println("Error occured while inflating view: " + e);
        }
    }

    public static void goToFundsOperateView(int cid) {
        try {
            AnchorPane root;
            root = (AnchorPane) FXMLLoader.load(Router.class.getResource("/com/wkyle/bankrecord/views/FundsOperateView.fxml"));
            Main.stage.setTitle("Client View");
            Scene scene = new Scene(root);
            addCssForScene(scene);
            Main.stage.setScene(scene);
        } catch (Exception e) {
            System.out.println("Error occured while inflating view: " + e);
        }

    }

    private static void addCssForScene(Scene se) {
        se.getStylesheets().add(Router.class.getResource("/com/wkyle/bankrecord/views/styles.css").toExternalForm());
    }

}

