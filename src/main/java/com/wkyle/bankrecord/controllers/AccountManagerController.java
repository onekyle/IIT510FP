package com.wkyle.bankrecord.controllers;

import com.wkyle.bankrecord.Dao.DBConnect;
import com.wkyle.bankrecord.models.AccountHelper;
import com.wkyle.bankrecord.models.AccountModel;
import com.wkyle.bankrecord.models.LoginModel;
import com.wkyle.bankrecord.models.RecordHelper;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Function;

public class AccountManagerController implements Initializable {

    @FXML
    private Pane pane1;
    @FXML
    private Pane pane2;
    @FXML
    private Pane paneAccounts;

    @FXML
    private Label userLbl;
    @FXML
    private TextField recordIDTF;
    @FXML
    private TextField recordBalanceTF;
    @FXML
    private TextField deleteTidTF;
    @FXML
    private TableView<AccountModel> tblAccounts;
    @FXML
    private TableColumn<AccountModel, String> accountID;
    @FXML
    private TableColumn<AccountModel, String> accountName;
    @FXML
    private TableColumn<AccountModel, String> roleType;
    // Declare DB objects
    DBConnect conn = null;
    Statement stmt = null;

    public void initialize(URL location, ResourceBundle resources) {
        AccountModel current = LoginModel.getInstance().getAccount();
        userLbl.setText(String.format("Welcome %s, id: %d", current.getUname(), current.getCid()));
        accountID.setCellValueFactory(new PropertyValueFactory<AccountModel, String>("cid"));
        accountName.setCellValueFactory(new PropertyValueFactory<AccountModel, String>("uname"));
        roleType.setCellValueFactory(new PropertyValueFactory<AccountModel, String>("roleTypeString"));
        updateTable();
        viewAccounts();
    }

    public AccountManagerController() {
        conn = new DBConnect();
    }

    public void viewAccounts() {
        paneAccounts.setVisible(true);
        pane2.setVisible(false);
        pane1.setVisible(false);
    }

    public void updateRec() {
        paneAccounts.setVisible(false);
        pane2.setVisible(false);
        pane1.setVisible(true);
    }

    public void deleteRec() {
        paneAccounts.setVisible(false);
        pane1.setVisible(false);
        pane2.setVisible(true);
    }


    public void submitUpdate() {
        System.out.println("Update Submit button pressed");
        String recordID = recordIDTF.getText();
        String recordBalance = recordBalanceTF.getText();
        if (recordID == null || recordID.isEmpty() || recordBalance == null || recordBalance.isEmpty()) {
            DialogController.showErrorDialog("Input invalid", "Please check and re-enter.");
        } else {
            try {
                RecordHelper.getInstance().updateRecord(Integer.parseInt(recordID), Double.parseDouble(recordBalance));
            } catch (NumberFormatException e) {
                DialogController.showErrorDialog("Input invalid", e.toString());
            }
        }
    }

    public void submitDelete() {
        System.out.println("Delete Submit button pressed");
        String tid = deleteTidTF.getText();
        if (tid == null || tid.isEmpty()) {
            DialogController.showErrorDialog("Input invalid", "Please check and re-enter.");
        } else {
            try {
                RecordHelper.getInstance().deleteRecord(Integer.parseInt(tid));
            } catch (NumberFormatException e) {
                DialogController.showErrorDialog("Input invalid", e.toString());
            }
        }
    }

    public void logout() {
        LoginModel.getInstance().logout();
        Router.goToLoginView();
    }

    public void addAccount() {
        DialogController.accountInfoInputDialog("Add Account", LoginModel.getInstance().getAccount(), null, new Function<AccountModel, AccountModel>() {
            @Override
            public AccountModel apply(AccountModel accountModel) {
                Boolean flag = AccountHelper.getInstance().createUser(accountModel.getUname(), accountModel.getPasswdEncrypted(), accountModel.getRoleType());
                if (flag) {
                    Platform.runLater(() -> updateTable());
                }
                return accountModel;
            }
        });
    }

    public void editAccount() {
        AccountModel model = tblAccounts.getSelectionModel().getSelectedItem();
        if (model == null) {
            return;
        }
        DialogController.accountInfoInputDialog("Edit Account", LoginModel.getInstance().getAccount(), model, new Function<AccountModel, AccountModel>() {
            @Override
            public AccountModel apply(AccountModel accountModel) {
                Boolean flag = AccountHelper.getInstance().editAccount(accountModel, model);
                if (flag) {
                    Platform.runLater(() -> updateTable());
                }
                return accountModel;
            }
        });
    }

    public void deleteAccount() {
        AccountModel model = tblAccounts.getSelectionModel().getSelectedItem();
        if (model == null) {
            // do nothing..
        } else {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation Dialog");
            alert.setHeaderText("Are you sure you want delete this account?");
            alert.setContentText(String.format("Delete Account: %s, role type is: %s", model.getUname(), model.getRoleTypeString()));

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                // ... user chose OK
                Boolean flag = AccountHelper.getInstance().deleteAccount(model.getCid());
                if (flag) {
                    tblAccounts.getItems().remove(model);
                }
            } else {
                // ... user chose CANCEL or closed the dialog
            }
        }
    }

    private void updateTable() {
        tblAccounts.getItems().setAll(AccountHelper.getInstance().getAccounts(AccountModel.RoleType.ACCOUNT_MANAGER));
    }
}
