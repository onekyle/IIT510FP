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
		String sql = "CREATE TABLE IF NOT EXISTS brs2021_users " +
				"(pid INTEGER not NULL AUTO_INCREMENT, " +
				" id VARCHAR(10), " +
				" income numeric(8,2), " + " pep VARCHAR(3), " +
				" PRIMARY KEY ( pid ))";
	}

	public void logAllUsers() {
		String query = "SELECT * FROM brs2021_users;";
		try {
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			ResultSetPrinter.printResultSet(rs);
		} catch (SQLException se) {
			se.printStackTrace();
		}
	}

	public Boolean createUser(String username, String password) {
		String query = String.format("INSERT INTO brs2021_users(uname,passwd,admin) VALUES (\"%s\",\"%s\",\"%s\")", username, password, "0");
		try {
			Statement stmt = connection.createStatement();
			int ret =  stmt.executeUpdate(query);
			if (ret == 1) {
				return true;
			}
		} catch (SQLException se) {
			se.printStackTrace();
		}
		return false;
	}
		
	public Boolean getCredentials(String username, String password){
		String query = "SELECT * FROM brs2021_users WHERE uname = ? and passwd = ?;";
		try(PreparedStatement stmt = connection.prepareStatement(query)) {
		   stmt.setString(1, username);
		   stmt.setString(2, password);
		   ResultSet rs = stmt.executeQuery();
			if(rs.next()) {

				setId(rs.getInt("id"));
				setAdmin(rs.getBoolean("admin"));
				return true;
			}
		 }catch (SQLException e) {
			e.printStackTrace();
		 }
		return false;
    }

}//end class