package com.wkyle.bankrecord.Dao;

import java.sql.*;

import com.wkyle.bankrecord.models.AccountModel;
import com.wkyle.bankrecord.utils.HashSHAUtils;

public class LoginModel extends DBConnect {

    private static LoginModel instance = new LoginModel();

    private LoginModel() {
    }

    public static LoginModel getInstance() {
        return instance;
    }

    private AccountModel account = new AccountModel();

    public AccountModel getAccount() {
        return this.account;
    }

    public int getId() {
        return account.getCid();
    }

    public Boolean isAdmin() {
        return account.getRoleType() == AccountModel.RoleType.ADMIN;
    }

    public Boolean getCredentials(String username, String password) {
        String query = "SELECT * FROM brs2021_users WHERE uname = ? and passwd = ?;";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, AccountHelper.getInstance().encryptedPassword(password));
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                account.setCid(rs.getInt("id"));
                account.setPasswdEncrypted(rs.getString("passwd"));
                account.setRoleType(AccountModel.RoleType.values()[rs.getInt("role")]);
                account.setUname(rs.getString("uname"));
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void logout() {
        this.account = new AccountModel();
    }

}//end class
