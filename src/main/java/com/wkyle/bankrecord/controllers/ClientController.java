package com.wkyle.bankrecord.controllers;

import java.net.URL;
import java.text.NumberFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

import com.wkyle.bankrecord.models.AccountModel;
import com.wkyle.bankrecord.Dao.LoginModel;
import com.wkyle.bankrecord.Dao.RecordHelper;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import com.wkyle.bankrecord.models.ClientModel;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;


public class ClientController implements Initializable {
	
	static int userid;
	@FXML
	private Label userBalance;
	@FXML
	private Label userLbl;
	
	/***** TABLEVIEW intel *********************************************************************/

	@FXML
	private TableView<ClientModel> tblAccounts;
	@FXML
	private TableColumn<ClientModel, String> tid;
	@FXML
	private TableColumn<ClientModel, String> balance;

    public void customResize(TableView<?> view) {

        AtomicLong width = new AtomicLong();
        view.getColumns().forEach(col -> {
            width.addAndGet((long) col.getWidth());
        });
        double tableWidth = view.getWidth();

        if (tableWidth > width.get()) {
            view.getColumns().forEach(col -> {
                col.setPrefWidth(col.getWidth()+((tableWidth-width.get())/view.getColumns().size()));
            });
        }
    }
	/***** End TABLEVIEW intel *********************************************************************/

	public void initialize(URL location, ResourceBundle resources) {
		AccountModel current = LoginModel.getInstance().getAccount();
		userLbl.setText(String.format("Welcome %s, id: %d", current.getUname(), current.getCid()));

		tid.setCellValueFactory(new PropertyValueFactory<ClientModel, String>("tid"));
		balance.setCellValueFactory(new PropertyValueFactory<ClientModel, String>("balanceStr"));

		// auto adjust width of columns depending on their content
		tblAccounts.setColumnResizePolicy((param) -> true);
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				updateUserBalance();
				customResize(tblAccounts);
			}
		});
		tblAccounts.setVisible(false);
	}

	public void viewAccounts() {
		tblAccounts.getItems().setAll(RecordHelper.getInstance().getRecordsForUser(userid)); // load table data from ClientModel List
		tblAccounts.setVisible(true); // set tableview to visible if not
	}

	private void updateUserBalance() {
		double balance = RecordHelper.getInstance().getBalance(userid);

		NumberFormat numberFormat = NumberFormat.getCurrencyInstance(Locale.US);
		userBalance.setText(String.format("Balance:  %s", numberFormat.format(balance)));
	}

	public void logout() {
		LoginModel.getInstance().logout();
		Router.goToLoginView();
	}

	public void createTransaction() {

		TextInputDialog dialog = new TextInputDialog("Enter dollar amount");
		dialog.setTitle("Bank Account Entry Portal");
		dialog.setHeaderText("Enter Transaction");
		dialog.setContentText("Please enter your balance:");

		// Traditional way to get the response value.
		Optional<String> result = dialog.showAndWait();
		if (result.isPresent()) {
			System.out.println("Balance entry: " + result.get());
			RecordHelper.getInstance().updateBalance(userid, Double.parseDouble(result.get()));
		}

		// The Java 8 way to get the response value (with lambda expression).
		result.ifPresent(balance -> System.out.println("Balance entry: " + balance));

	}

	public void onDeposit() {
		DialogController.showInputDialog("Deposit", null, "Please enter the amount you want to deposit:", null, new Function<String, String>() {
			@Override
			public String apply(String s) {
				try {
					double value = Double.parseDouble(s);
					if (value <= 0) {
						DialogController.showErrorDialog("Input invalid", "Please check and re-enter");
					} else {
						RecordHelper.getInstance().updateBalance(userid, value);
						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								updateUserBalance();
								viewAccounts();
							}
						});
					}
				} catch (NumberFormatException e) {
					DialogController.showErrorDialog("Input invalid", "Please check and re-enter");
				}
				return s;
			}
		});
	}

	public void onWithdraw() {
		DialogController.showInputDialog("Withdraw", null, "Please enter the amount you want to withdraw:", null, new Function<String, String>() {
			@Override
			public String apply(String s) {
				try {
					double value = Double.parseDouble(s);
					RecordHelper.getInstance().withdraw(userid, value);
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							updateUserBalance();
							viewAccounts();
						}
					});
				} catch (NumberFormatException e) {
					DialogController.showErrorDialog("Input invalid", "Please check and re-enter");
				}
				return s;
			}
		});
	}

    public void onTransfer() {
        // Create the custom dialog.
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Transfer");

        // Set the button types.
        ButtonType addButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        // Create the username and transfer amount labels and fields.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField username = new TextField();
        username.setPromptText("Cid");
        TextField amount = new TextField();
        amount.setPromptText("Amount");

        grid.add(new Label("Transfer To Cid:"), 0, 0);
        grid.add(username, 1, 0);
        grid.add(new Label("Transfer Amount:"), 0, 1);
        grid.add(amount, 1, 1);

        // Enable/Disable login button depending on whether a username was entered.
        Node addButton = dialog.getDialogPane().lookupButton(addButtonType);
        addButton.setDisable(true);

        // Do some validation (using the Java 8 lambda syntax).
        ChangeListener<Object> inputCallback = (observable, oldValue, newValue) -> {
            addButton.setDisable(username.getText().trim().isEmpty() || amount.getText().trim().isEmpty());
        };

        username.textProperty().addListener(inputCallback);
        amount.textProperty().addListener(inputCallback);

        dialog.getDialogPane().setContent(grid);

        // Request focus on the username field by default.
        Platform.runLater(() -> username.requestFocus());

        // Convert the result to a username-password-pair when the login button is clicked.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                return new Pair(username.getText(), amount.getText());
            }
            return null;
        });

		Optional<Pair<String, String>> result = dialog.showAndWait();

		result.ifPresent(resultPair -> {

			String name = resultPair.getKey();
			String transferAmount = resultPair.getValue();
			if (name == null || name.isEmpty() || transferAmount == null || transferAmount.isEmpty()) {
				DialogController.showErrorDialog("Input invalid", "Please check and re-enter.");
			} else {
				try {
					double doubleAmount = Double.parseDouble(transferAmount);
					int cid = Integer.parseInt(name);
					Boolean flag = RecordHelper.getInstance().withdraw(userid, doubleAmount);
					if (flag) {
						RecordHelper.getInstance().updateBalance(cid, doubleAmount);
						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								updateUserBalance();
								viewAccounts();
							}
						});
					}
				} catch (NumberFormatException e) {
					DialogController.showErrorDialog("Input invalid", e.toString());
				}
			}
		});
	}

    public static void setUserid(int user_id) {
        userid = user_id;
        System.out.println("Welcome id " + userid);
    }
}
