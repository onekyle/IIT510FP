package com.wkyle.bankrecord.models;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.wkyle.bankrecord.Dao.DBConnect;

public class LoginModel extends DBConnect {

	private Boolean admin;
	private int id;
 
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Boolean isAdmin() {
		return admin;
	}
	public void setAdmin(Boolean admin) {
		this.admin = admin;
	}

	public void setupSQLTable() {
//		String dropTable = "DROP TABLE brs2021_users ;";

		String createUsersSql = "CREATE TABLE IF NOT EXISTS brs2021_users " +
				"(id INTEGER not NULL AUTO_INCREMENT, " +
				" uname VARCHAR(255), " +
				" passwd VARCHAR(255)," +
				" role int, " +
				" PRIMARY KEY ( id ))";

//		String addPasswd = "ALTER TABLE brs2021_users CHANGE admin admin int;";

		String createTransactionRecordsTable = "CREATE TABLE IF NOT EXISTS brs2021_accounts " +
				"(tid INTEGER not NULL AUTO_INCREMENT, " +
				" cid int, " +
				" balance numeric(8,2), " +
				" PRIMARY KEY ( tid ))";

		String createBankTable = "CREATE TABLE IF NOT EXISTS brs2021_bank " +
				"(id INTEGER not NULL AUTO_INCREMENT, " +
				" name VARCHAR(255), " +
				" address VARCHAR(255), " +
				" PRIMARY KEY ( id ))";
		try {
			Statement stmt = connection.createStatement();
//			stmt.executeUpdate(dropTable);
			stmt.executeUpdate(createUsersSql);
			stmt.executeUpdate(createTransactionRecordsTable);
			stmt.executeUpdate(createBankTable);
		} catch (SQLException se) {
			se.printStackTrace();
		}
	}

	public Boolean getCredentials(String username, String password){
		String query = "SELECT * FROM brs2021_users WHERE uname = ? and passwd = ?;";
		try(PreparedStatement stmt = connection.prepareStatement(query)) {
		   stmt.setString(1, username);
		   stmt.setString(2, password);
		   ResultSet rs = stmt.executeQuery();
			if(rs.next()) {

				setId(rs.getInt("id"));
				setAdmin(rs.getInt("role") == 0);
				return true;
			}
		 }catch (SQLException e) {
			e.printStackTrace();
		 }
		return false;
    }

}//end class