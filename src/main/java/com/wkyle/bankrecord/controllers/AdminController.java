package com.wkyle.bankrecord.controllers;

import java.net.URL;
import java.util.*;
import java.util.function.Function;

import com.wkyle.bankrecord.Dao.AccountHelper;
import com.wkyle.bankrecord.Dao.AdminModel;
import com.wkyle.bankrecord.Dao.LoginModel;
import com.wkyle.bankrecord.models.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;

public class AdminController implements Initializable {

    @FXML
    private Pane pane1;
    @FXML
    private Pane pane2;
    @FXML
    private Pane pane3;
    @FXML
    private Pane paneAccounts;
    @FXML
    private TextField txtName;
    @FXML
    private TextField txtAddress;

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

    AdminModel adminModel = null;

    public void initialize(URL location, ResourceBundle resources) {
        accountID.setCellValueFactory(new PropertyValueFactory<AccountModel, String>("cid"));
        accountName.setCellValueFactory(new PropertyValueFactory<AccountModel, String>("uname"));
        roleType.setCellValueFactory(new PropertyValueFactory<AccountModel, String>("roleTypeString"));
        updateTable();
        viewAccounts();
    }

    public AdminController() {
        adminModel = new AdminModel();
    }

    public void viewAccounts() {
        paneAccounts.setVisible(true);
        pane3.setVisible(false);
        pane2.setVisible(false);
        pane1.setVisible(false);
    }

    public void updateRec() {
        paneAccounts.setVisible(false);
        pane3.setVisible(false);
        pane2.setVisible(false);
        pane1.setVisible(true);
    }

    public void deleteRec() {
        paneAccounts.setVisible(false);
        pane1.setVisible(false);
        pane2.setVisible(true);
        pane3.setVisible(false);
    }

    public void addBankRec() {
        paneAccounts.setVisible(false);
        pane1.setVisible(false);
        pane2.setVisible(false);
        pane3.setVisible(true);
    }

    public void submitBank() {
        adminModel.createBank(txtName.getText(), txtAddress.getText());
    }

    public void submitUpdate() {
        System.out.println("Update Submit button pressed");
        String recordID = recordIDTF.getText();
        String recordBalance = recordBalanceTF.getText();
        if (recordID == null || recordID.isEmpty() || recordBalance == null || recordBalance.isEmpty()) {
            DialogController.showErrorDialog("Input invalid", "Please check and re-enter.");
        } else {
            try {
                adminModel.updateBank(recordID, recordBalance);
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
                adminModel.deleteBank(tid);
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
                if (!Objects.equals(accountModel.getPasswdEncrypted(), model.getPasswdEncrypted())) {
                    // passwd changed, need re encrypt
                    accountModel.setPasswdEncrypted(AccountHelper.getInstance().encryptedPassword(accountModel.getPasswdEncrypted()));
                }
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
        tblAccounts.getItems().setAll(AccountHelper.getInstance().getAccounts(AccountModel.RoleType.ADMIN));
    }
}
