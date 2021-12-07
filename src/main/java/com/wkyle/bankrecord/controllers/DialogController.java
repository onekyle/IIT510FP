package com.wkyle.bankrecord.controllers;

import com.wkyle.bankrecord.models.AccountModel;
import com.wkyle.bankrecord.models.FundsRecordModel;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

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


    public static TextInputDialog showInputDialog(String title, String header, String content, String placeholder, Function<String, String> callBack) {
        TextInputDialog dialog = new TextInputDialog(placeholder);
        dialog.setTitle(title);
        dialog.setHeaderText(header);
        dialog.setContentText(content);

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(str -> {
            callBack.apply(str);
        });
        return dialog;
    }

    public static Dialog<AccountModel> accountInfoInputDialog(String title, AccountModel creater, AccountModel placeHolder, Function<AccountModel, AccountModel> callback) {
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
        if (creater.getRoleType() == AccountModel.RoleType.ADMIN) {
            // only admin can add account manager.
            choices.add("ACCOUNT_MANAGER");
        }
        ChoiceBox cb = new ChoiceBox(FXCollections.observableArrayList(choices));

        grid.add(new Label("Username:"), 0, 0);
        grid.add(username, 1, 0);
        grid.add(new Label("Password:"), 0, 1);
        grid.add(password, 1, 1);
        grid.add(new Label("RoleType:"), 0, 2);
        grid.add(cb, 1, 2);

        if (placeHolder != null) {
            username.setText(placeHolder.getUname());
            password.setText(placeHolder.getPasswdEncrypted());
            cb.setValue(placeHolder.getRoleTypeString());
        } else {
            cb.setValue(AccountModel.RoleType.CUSTOMER.name());
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
        });

        return dialog;
    }

    public static Dialog<FundsRecordModel> recordInfoInputDialog(String title, FundsRecordModel placeHolder, Function<FundsRecordModel, FundsRecordModel> callback) {
        // Create the custom dialog.
        Dialog<FundsRecordModel> dialog = new Dialog<>();
        dialog.setTitle(title == null ? "Add Record" : title);

        // Set the button types.
        ButtonType addButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        // Create the username and password labels and fields.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField cidTf = new TextField();
        cidTf.setPromptText("Cid");
        TextField amountTf = new TextField();
        amountTf.setPromptText("Amount");
        List<String> choices = new ArrayList<>();
        grid.add(new Label("Cid:"), 0, 0);
        grid.add(cidTf, 1, 0);
        grid.add(new Label("Amount:"), 0, 1);
        grid.add(amountTf, 1, 1);

        if (placeHolder != null) {
            grid.add(new Label("Tid:"), 0, 2);
            grid.add(new Label(String.valueOf(placeHolder.getTid())), 1, 2);
            cidTf.setText(String.valueOf(placeHolder.getCid()));
            cidTf.setDisable(true);
            amountTf.setText(String.valueOf(placeHolder.getBalance()));
        }
        // Enable/Disable login button depending on whether a username was entered.
        Node addButton = dialog.getDialogPane().lookupButton(addButtonType);
        addButton.setDisable(true);

        // Do some validation (using the Java 8 lambda syntax).
        ChangeListener<Object> inputCallback = (observable, oldValue, newValue) -> {
            addButton.setDisable(cidTf.getText().trim().isEmpty() || amountTf.getText().trim().isEmpty());
        };

        cidTf.textProperty().addListener(inputCallback);
        amountTf.textProperty().addListener(inputCallback);

        dialog.getDialogPane().setContent(grid);

        // Request focus on the username field by default.
        Platform.runLater(() -> cidTf.requestFocus());

        // Convert the result to a username-password-pair when the login button is clicked.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                FundsRecordModel model = new FundsRecordModel();
                if (placeHolder != null) {
                    model.setTid(placeHolder.getTid());
                }
                model.setCid(Integer.parseInt(cidTf.getText()));
                model.setBalance(Double.parseDouble(amountTf.getText()));
                return model;
            }
            return null;
        });

        Optional<FundsRecordModel> result = dialog.showAndWait();

        result.ifPresent(resultModel -> {
            if (placeHolder != null) {
                if (Objects.equals(placeHolder.getBalance(), resultModel.getBalance())) {
                    // edit doesn't change anything
                    return;
                }
            }
            callback.apply(resultModel);
        });

        return dialog;
    }
}
