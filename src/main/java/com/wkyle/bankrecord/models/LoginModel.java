package com.wkyle.bankrecord.models;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.wkyle.bankrecord.Dao.DBConnect;

public class LoginModel extends DBConnect {

	private AccountModel account = new AccountModel();
	public AccountModel getAccount() { return this.account; }

	public int getId() {
		return account.getCid();
	}
	public Boolean isAdmin() {
		return account.getRoleType() == AccountModel.RoleType.ADMIN;
	}

	public void setupSQLTable() {
//		String dropTable = "DROP TABLE brs2021_users ;";

		String createUsersSql = "CREATE TABLE IF NOT EXISTS brs2021_users " +
				"(id INTEGER not NULL AUTO_INCREMENT, " +
				" uname VARCHAR(255), " +
				" passwd VARCHAR(255)," +
				" role int, " +
				"create_time datetime,"+
				" PRIMARY KEY ( id )," +
				"UNIQUE (uname))";

//		String alter = "ALTER TABLE Persons\n" +
//				"ADD UNIQUE (uname);";

		String createTransactionRecordsTable = "CREATE TABLE IF NOT EXISTS brs2021_accounts " +
				"(tid INTEGER not NULL AUTO_INCREMENT, " +
				" cid int, " +
				" balance numeric(8,2), " +
				"create_time datetime,"+
				" PRIMARY KEY ( tid ))";

		String createBankTable = "CREATE TABLE IF NOT EXISTS brs2021_bank " +
				"(id INTEGER not NULL AUTO_INCREMENT, " +
				" name VARCHAR(255), " +
				" address VARCHAR(255), " +
				"create_time datetime,"+
				" PRIMARY KEY ( id ))";
		try {
			Statement stmt = connection.createStatement();
//			stmt.executeUpdate(dropTable);
			stmt.executeUpdate(createUsersSql);
//			stmt.executeUpdate(alter);
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
		   stmt.setString(2, AccountHelper.getInstance().encryptedPassword(password));
		   ResultSet rs = stmt.executeQuery();
			if(rs.next()) {
				account.setCid(rs.getInt("id"));
				account.setPasswdEncrypted(rs.getString("passwd"));
				account.setRoleType(AccountModel.RoleType.values()[rs.getInt("role")]);
				account.setUname(rs.getString("uname"));
				return true;
			}
		 }catch (SQLException e) {
			e.printStackTrace();
		 }
		return false;
    }

}//end class