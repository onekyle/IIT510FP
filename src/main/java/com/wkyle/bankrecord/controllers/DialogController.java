package com.wkyle.bankrecord.controllers;

import com.wkyle.bankrecord.models.AccountHelper;
import com.wkyle.bankrecord.models.AccountModel;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import javax.security.auth.callback.Callback;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public class DialogController {

    public static void showErrorDialog(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title != null ? title : "Error");
        alert.setContentText(content);
        alert.show();
    }

    public static void showInfoDialog(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.show();
    }

    public static Dialog<AccountModel> accountInfoInputDialog(String title, AccountModel placeHolder, Function<AccountModel, AccountModel> callback) {
        // Create the custom dialog.
        Dialog<AccountModel> dialog = new Dialog<>();
        dialog.setTitle(title == null ? "Add Account" : title);

        // Set the button types.
        ButtonType addButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        // Create the username and password labels and fields.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField username = new TextField();
        username.setPromptText("Username");
        PasswordField password = new PasswordField();
        password.setPromptText("Password");
        List<String> choices = new ArrayList<>();
        choices.add("CUSTOMER");
        choices.add("ACCOUNT_MANAGER");
        ChoiceBox cb = new ChoiceBox(FXCollections.observableArrayList(choices));

        grid.add(new Label("Username:"), 0, 0);
        grid.add(username, 1, 0);
        grid.add(new Label("Password:"), 0, 1);
        grid.add(password, 1, 1);
        grid.add(new Label("RoleType:"), 0, 2);
        grid.add(cb, 1,2);

        if (placeHolder != null) {
            username.setText(placeHolder.getUname());
            password.setText(placeHolder.getPasswdEncrypted());
            cb.setValue(placeHolder.getRoleTypeString());
        }

        // Enable/Disable login button depending on whether a username was entered.
        Node addButton = dialog.getDialogPane().lookupButton(addButtonType);
        addButton.setDisable(true);

        // Do some validation (using the Java 8 lambda syntax).
        ChangeListener<Object> inputCallback = (observable, oldValue, newValue) -> {
            addButton.setDisable(username.getText().trim().isEmpty() || password.getText().trim().isEmpty() || cb.getValue() == null);
        };

        username.textProperty().addListener(inputCallback);
        password.textProperty().addListener(inputCallback);
        cb.valueProperty().addListener(inputCallback);

        dialog.getDialogPane().setContent(grid);

        // Request focus on the username field by default.
        Platform.runLater(() -> username.requestFocus());

        // Convert the result to a username-password-pair when the login button is clicked.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                AccountModel model = new AccountModel();
                if (placeHolder != null) {
                    model.setCid(placeHolder.getCid());
                }
                model.setUname(username.getText());
                model.setPasswdEncrypted(password.getText());
                model.setRoleType(AccountModel.RoleType.valueOf((String) cb.getValue()));
                return model;
            }
            return null;
        });

        Optional<AccountModel> result = dialog.showAndWait();

        result.ifPresent(resultModel -> {
            if (placeHolder != null) {
                if (Objects.equals(placeHolder.getUname(), resultModel.getUname()) && Objects.equals(placeHolder.getPasswdEncrypted(), resultModel.getPasswdEncrypted()) && placeHolder.getRoleType() == resultModel.getRoleType()) {
                    // edit doesn't change anything
                    return;
                }
            }
            callback.apply(resultModel);
//            String name = resultArr.get(0);
//            String passwd = resultArr.get(1);
//            String roleType = resultArr.get(2);
//            AccountModel account = new AccountModel();
//            account.setUname(name);
//            account.setPasswdEncrypted(passwd);
//            account.setRoleType(AccountModel.RoleType.valueOf(roleType));
//            Boolean flag = AccountHelper.getInstance().createUser(name, passwd, AccountModel.RoleType.valueOf(roleType));
//            if (flag) {
//                Platform.runLater(() -> updateTable());
//            }
        });

        return dialog;
    }
}
