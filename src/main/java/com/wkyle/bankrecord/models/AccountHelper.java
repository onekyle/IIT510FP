package com.wkyle.bankrecord.models;

import com.wkyle.bankrecord.Dao.DBConnect;
import com.wkyle.bankrecord.controllers.DialogController;
import com.wkyle.bankrecord.utils.HashSHAUtils;
import javafx.scene.control.Dialog;

import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AccountHelper {

    private static AccountHelper instance = new AccountHelper();
    private AccountHelper(){
        this.connect = new DBConnect();
    }

    public static AccountHelper getInstance(){
        return instance;
    }

    private DBConnect connect = null;

    public String encryptedPassword(String passwd) {

        String encryptedPassword="";
        try {
            encryptedPassword= HashSHAUtils.toMD5(passwd);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return String.valueOf(encryptedPassword);
    }

    public AccountModel getAccount(int cid, String name) {
        String condition = null;
        if (name != null && !name.isEmpty()) {
            condition = " WHERE uname = \"" + name + "\"";
        } else {
            condition = " WHERE id = " + cid;
        }
        String query = "SELECT * FROM brs2021_users" + condition;
        try {
            Statement stmt = connect.getConnection().createStatement();
            ResultSet rs = stmt.executeQuery(query);
            if(rs.next()) {
                AccountModel account = new AccountModel();
                account.setCid(rs.getInt("id"));
                account.setPasswdEncrypted(rs.getString("passwd"));
                account.setRoleType(AccountModel.RoleType.values()[rs.getInt("role")]);
                account.setUname(rs.getString("uname"));
                return account;
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Boolean createUser(String username, String password, AccountModel.RoleType role) {
        if (username != null && !username.isEmpty()) {
            AccountModel existAccount = getAccount(0,username);
            if (existAccount != null) {
                DialogController.showErrorDialog("User name exist", "Please try another user name");
                return false;
            }
        }
//        String query = String.format("INSERT INTO brs2021_users(uname,passwd,role) VALUES (\"%s\",\"%s\",%d)", username, encryptedPassword(password), role.ordinal());
        try {
            PreparedStatement stmt = connect.getConnection().prepareStatement("INSERT INTO brs2021_users(uname,passwd,role,create_time) VALUES (?,?,?,?)");
            stmt.setString(1, username);
            stmt.setString(2, encryptedPassword(password));
            stmt.setInt(3, role.ordinal());
            Timestamp date = new Timestamp(System.currentTimeMillis());
            stmt.setTimestamp(4, date);
            int ret =  stmt.executeUpdate();
            if (ret == 1) {
                return true;
            }
        } catch (SQLException se) {
            se.printStackTrace();
            DialogController.showErrorDialog(null, se.toString());
        }
        return false;
    }

    public Boolean editAccount(AccountModel newAccount, AccountModel old) {
        if (!Objects.equals(newAccount.getUname(), old.getUname())) { // user name changed
            // check whether the name is valid
            AccountModel existAccount = getAccount(0,newAccount.getUname());
            if (existAccount != null) {
                DialogController.showErrorDialog("User name exist", "Please try another user name");
                return false;
            }
        }
        String query = String.format("UPDATE brs2021_users SET uname=\"%s\", passwd=\"%s\", role=%d WHERE id=%d;", newAccount.getUname(), newAccount.getPasswdEncrypted(), newAccount.getRoleType().ordinal(), newAccount.getCid());
        try {
            Statement statement = connect.getConnection().createStatement();
            int result = statement.executeUpdate(query);
            if (result == 1) {
                return true;
            }
        } catch (SQLException se) {
            System.out.println("Error edit Account: " + se);
            DialogController.showErrorDialog(null, se.toString());
        }
        return false;
    }

    public Boolean deleteAccount(int cid) {
        String query = String.format("DELETE FROM brs2021_users WHERE id=%d", cid);
        try {
            Statement statement = connect.getConnection().createStatement();
            int result = statement.executeUpdate(query);
            if (result == 1) {
                return true;
            }
        } catch (SQLException se) {
            System.out.println("Error delete Account: " + se);
            DialogController.showErrorDialog(null, se.toString());
        }
        return false;
    }

    public List<AccountModel> getAccounts(AccountModel.RoleType ownerType) {
        String condition = "";
        switch (ownerType) {
            case CUSTOMER:
                return null;
            case ADMIN:
                condition = " WHERE role > 0;";
                break;
            case ACCOUNT_MANAGER:
                condition = " WHERE role > 1;";
                break;
        }
        List<AccountModel> accounts = new ArrayList<>();
        String query = "SELECT * FROM brs2021_users" + condition;
        try {
            Statement statement = connect.getConnection().createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                AccountModel account = new AccountModel();
                // grab record data by table field name into ClientModel account object
                account.setCid(resultSet.getInt("id"));
                account.setUname(resultSet.getString("uname"));
                account.setPasswdEncrypted(resultSet.getString("passwd"));

                account.setRoleType(AccountModel.RoleType.values()[resultSet.getInt("role")]);
                accounts.add(account); // add account data to arraylist
            }
        } catch (SQLException se) {
            System.out.println("Error fetching Accounts: " + se);
            DialogController.showErrorDialog(null, se.toString());
        }
        return accounts; // return arraylist
    }

    public void logAllUsers() {
        String query = "SELECT * FROM brs2021_users;";
        try {
            Statement stmt = connect.getConnection().createStatement();
            ResultSet rs = stmt.executeQuery(query);
            if (rs.next()) {
                ResultSetPrinter.printResultSet(rs);
            }
        } catch (Exception se) {
            se.printStackTrace();
            DialogController.showErrorDialog(null, se.toString());
        }
    }
}
