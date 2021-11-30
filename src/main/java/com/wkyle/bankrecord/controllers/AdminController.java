package com.wkyle.bankrecord.controllers;

import java.net.URL;
import java.sql.*;
import java.util.*;
import java.util.function.Function;

import com.wkyle.bankrecord.Dao.DBConnect;
import com.wkyle.bankrecord.models.*;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.util.Pair;

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
	// Declare DB objects
	DBConnect conn = null;
	Statement stmt = null;

	public void initialize(URL location, ResourceBundle resources) {
		accountID.setCellValueFactory(new PropertyValueFactory<AccountModel, String>("cid"));
		accountName.setCellValueFactory(new PropertyValueFactory<AccountModel, String>("uname"));
		roleType.setCellValueFactory(new PropertyValueFactory<AccountModel, String>("roleTypeString"));
		updateTable();
	}

	public AdminController() {
		conn = new DBConnect();
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

		// INSERT INTO BANK TABLE
		try {
			// Execute a query
			System.out.println("Inserting records into the table...");
			stmt = conn.getConnection().createStatement();
			String sql = null;

			// Include all object data to the database table

			sql = "insert into brs2021_bank(name,address) values ('" + txtName.getText() + "','" + txtAddress.getText()
					+ "')";
			stmt.executeUpdate(sql);
			System.out.println("Bank Record created");

			conn.getConnection().close();
		} catch (SQLException se) {
			se.printStackTrace();
		}
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
		Router.goToLoginView();
	}

	public void addAccount() {
		DialogController.accountInfoInputDialog("Add Account", null, new Function<AccountModel, AccountModel>() {
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
		DialogController.accountInfoInputDialog("Edit Account", model, new Function<AccountModel, AccountModel>() {
			@Override
			public AccountModel apply(AccountModel accountModel) {
				if (!Objects.equals(accountModel.getPasswdEncrypted(), model.getPasswdEncrypted())) {
					// passwd changed, need re encrypt
					accountModel.setPasswdEncrypted(AccountHelper.getInstance().encryptedPassword(accountModel.getPasswdEncrypted()));
				}
				Boolean flag = AccountHelper.getInstance().editAccount(accountModel);
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
			if (result.get() == ButtonType.OK){
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
