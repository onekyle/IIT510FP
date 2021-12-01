package com.wkyle.bankrecord.models;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
 
import com.wkyle.bankrecord.Dao.DBConnect;

public class ClientModel extends DBConnect {

	private int cid;
	private int tid;
	private double balance;

	// Declare DB objects
	DBConnect conn = null;
	Statement stmt = null;

	public ClientModel() {

		conn = new DBConnect();
	}

	/* getters & setters */
	
	public int getCid() {
		return cid;
	}

	public void setCid(int cid) {
		this.cid = cid;
	}

	public void setTid(int tid) {
		this.tid = tid;
	}

	public int getTid() {
		return tid;
	}

	public Double getBalance() {
		return balance;
	}

	public void setBalance(Double balance) {
		this.balance = balance;
	}

	// INSERT INTO METHOD
	public void insertRecord(int cid, double bal) {

		try {
			setCid(cid);
			// Execute a query
			System.out.println("Inserting record into the table...");
			stmt = conn.getConnection().createStatement();
			String sql = null;

			// Include data to the database table

//			sql = " insert into brs2021_accounts(cid, balance,create_time) values('" + cid + "', '" + bal + "','" + System.currentTimeMillis() + "')";

			PreparedStatement stmt = conn.getConnection().prepareStatement("insert into brs2021_accounts(cid, balance,create_time) values(?,?,?)");
			stmt.setInt(1, cid);
			stmt.setDouble(2, bal);
			Timestamp date = new Timestamp(System.currentTimeMillis());
			stmt.setTimestamp(3, date);

			stmt.executeUpdate(sql);
			conn.getConnection().close();

			System.out.println("Balance inserted $" + bal + " for ClientID " + cid);

		} catch (SQLException se) {
			se.printStackTrace();
		}
	}

	public List<ClientModel> getAccounts(int cid) {
		List<ClientModel> accounts = new ArrayList<>();
		String query = "SELECT tid,balance FROM brs2021_accounts WHERE cid = ?;";
		try (PreparedStatement statement = connection.prepareStatement(query)) {
			statement.setInt(1, cid);
			ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()) {
				ClientModel account = new ClientModel();
				// grab record data by table field name into ClientModel account object
				account.setTid(resultSet.getInt("tid"));
				account.setBalance(resultSet.getDouble("balance"));
				accounts.add(account); // add account data to arraylist
			}
		} catch (SQLException e) {
			System.out.println("Error fetching Accounts: " + e);
		}
		return accounts; // return arraylist
	}

}