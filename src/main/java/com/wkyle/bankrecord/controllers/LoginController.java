package com.wkyle.bankrecord.controllers;

import com.wkyle.bankrecord.application.Main;
import com.wkyle.bankrecord.models.AccountHelper;
import com.wkyle.bankrecord.models.AccountModel;
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

    private LoginModel model;

    public LoginController() {
        model = LoginModel.getInstance();
        model.setupSQLTable();
    }

    public void login() {
        String username = this.txtUsername.getText();
        String password = this.txtPassword.getText();
        if (checkInputUserNameAndPassword(username, password)) {
            // authentication check
            checkCredentials(username, password);
        }
    }

    public void signup() {
        String username = this.txtUsername.getText();
        String password = this.txtPassword.getText();
        if (checkInputUserNameAndPassword(username, password)) {
            if (AccountHelper.getInstance().createUser(username, password, AccountModel.RoleType.CUSTOMER)) {
                // authentication check
                checkCredentials(username, password);
            }
        }
    }

    public boolean checkInputUserNameAndPassword(String username, String password) {
        // Validations
        if (username == null || username.trim().equals("")) {
            DialogController.showErrorDialog("Error", "Username Cannot be empty or spaces");
            return false;
        }
        if (password == null || password.trim().equals("")) {
            DialogController.showErrorDialog("Error", "Password Cannot be empty or spaces");
            return false;
        }
        if (username == null || username.trim().equals("") && (password == null || password.trim().equals(""))) {
            DialogController.showErrorDialog("Error", "User name / Password Cannot be empty or spaces");
            return false;
        }
        return true;
    }

    public void checkCredentials(String username, String password) {
        Boolean isValid = model.getCredentials(username, password);
        if (!isValid) {
            DialogController.showErrorDialog("Error", "User account password is incorrect or User does not exist!");
            return;
        }
        if (model.isAdmin()) {
            // If user is admin, inflate admin view
            Router.goToAdminView();
        } else if (model.getAccount().getRoleType() == AccountModel.RoleType.ACCOUNT_MANAGER) {
            Router.goToAccountManagerView();
        } else {
            // If user is customer, inflate customer view
            Router.goToClientView(model.getId());
        }
    }
}