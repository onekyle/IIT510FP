package com.wkyle.bankrecord.controllers;

import java.net.URL;
import java.util.*;
import java.util.function.Function;

import com.wkyle.bankrecord.Dao.AccountHelper;
import com.wkyle.bankrecord.Dao.AdminModel;
import com.wkyle.bankrecord.Dao.LoginModel;
import com.wkyle.bankrecord.Dao.RecordHelper;
import com.wkyle.bankrecord.models.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;

public class AdminController implements Initializable {

    @FXML
    private Pane paneRecords;
    @FXML
    private Pane paneAddBank;
    @FXML
    private Pane paneAccounts;
    @FXML
    private TextField txtName;
    @FXML
    private TextField txtAddress;


    @FXML
    private TableView<AccountModel> tblAccounts;
    @FXML
    private TableColumn<AccountModel, String> accountID;
    @FXML
    private TableColumn<AccountModel, String> accountName;
    @FXML
    private TableColumn<AccountModel, String> roleType;

    @FXML
    private TableView<ClientModel> tblRecords;
    @FXML
    private TableColumn<ClientModel, String> recordTid;
    @FXML
    private TableColumn<ClientModel, String> recordCid;
    @FXML
    private TableColumn<ClientModel, String> recordAmount;

    AdminModel adminModel = null;

    public void initialize(URL location, ResourceBundle resources) {
        accountID.setCellValueFactory(new PropertyValueFactory<AccountModel, String>("cid"));
        accountName.setCellValueFactory(new PropertyValueFactory<AccountModel, String>("uname"));
        roleType.setCellValueFactory(new PropertyValueFactory<AccountModel, String>("roleTypeString"));
        viewAccounts();

        recordTid.setCellValueFactory(new PropertyValueFactory<ClientModel, String>("tid"));
        recordCid.setCellValueFactory(new PropertyValueFactory<ClientModel, String>("cid"));
        recordAmount.setCellValueFactory(new PropertyValueFactory<ClientModel, String>("balanceStr"));
    }

    public AdminController() {
        adminModel = new AdminModel();
    }

    public void viewAccounts() {
        paneAccounts.setVisible(true);
        paneRecords.setVisible(false);
        paneAddBank.setVisible(false);
        updateAccountTableData();
    }

    public void viewRecords() {
        paneAccounts.setVisible(false);
        paneRecords.setVisible(true);
        paneAddBank.setVisible(false);
        updateRecordTableData();
    }

    public void addBankRec() {
        paneAccounts.setVisible(false);
        paneRecords.setVisible(false);
        paneAddBank.setVisible(true);
    }

    public void submitBank() {
        adminModel.createBank(txtName.getText(), txtAddress.getText());
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
                    Platform.runLater(() -> updateAccountTableData());
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
                    Platform.runLater(() -> updateAccountTableData());
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

    public void addRecord() {
        DialogController.recordInfoInputDialog("Add Record", null, new Function<ClientModel, ClientModel>() {
            @Override
            public ClientModel apply(ClientModel clientModel) {
                Boolean flag = RecordHelper.getInstance().updateBalance(clientModel.getCid(), clientModel.getBalance());
                if (flag) {
                    Platform.runLater(() -> updateRecordTableData());
                }
                return clientModel;
            }
        });
    }

    public void editRecord() {
        ClientModel model = tblRecords.getSelectionModel().getSelectedItem();
        DialogController.recordInfoInputDialog("Add Record", model, new Function<ClientModel, ClientModel>() {
            @Override
            public ClientModel apply(ClientModel clientModel) {
                Boolean flag = RecordHelper.getInstance().updateRecord(model.getTid(), clientModel.getBalance());
                if (flag) {
                    Platform.runLater(() -> updateRecordTableData());
                }
                return clientModel;
            }
        });
    }

    public void deleteRecord() {
        ClientModel model = tblRecords.getSelectionModel().getSelectedItem();
        Boolean flag = RecordHelper.getInstance().deleteRecord(model.getTid());
        if (flag) {
            Platform.runLater(() -> updateRecordTableData());
        }
    }

    private void updateAccountTableData() {
        tblAccounts.getItems().setAll(AccountHelper.getInstance().getAccounts(AccountModel.RoleType.ADMIN));
    }

    private void updateRecordTableData() {
        tblRecords.getItems().setAll(RecordHelper.getInstance().getRecords());
    }
}
