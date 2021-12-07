package com.wkyle.bankrecord.Dao;

import com.wkyle.bankrecord.controllers.DialogController;
import com.wkyle.bankrecord.models.FundsRecordModel;
import com.wkyle.bankrecord.utils.HashSHAUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RecordHelper {

    private static RecordHelper instance = new RecordHelper();

    private RecordHelper() {
        this.connect = new DBConnect();
    }

    public static RecordHelper getInstance() {
        return instance;
    }

    private DBConnect connect = null;

    /*
     * cid: -1 for all users records.
     */
    public List<FundsRecordModel> getRecords(int cid) {
        String query = "SELECT tid,cid,balance FROM brs2021_accounts";
        if (cid != -1) {
            query += (" WHERE cid = " + cid + ";");
        }
        List<FundsRecordModel> accounts = new ArrayList<>();
        try {
            Statement stmt = connect.getConnection().createStatement();
            ResultSet resultSet = stmt.executeQuery(query);
            while (resultSet.next()) {
                FundsRecordModel account = new FundsRecordModel();
                // grab record data by table field name into FundsRecordModel account object
                account.setCid(resultSet.getInt("cid"));
                account.setTid(resultSet.getInt("tid"));
                account.setBalance(resultSet.getDouble("balance"));
                accounts.add(account); // add account data to arraylist
            }
        } catch (SQLException e) {
            System.out.println("Error fetching Accounts: " + e);
        }
        return accounts; // return arraylist
    }

    public double getBalance(int cid) {
        if (AccountHelper.getInstance().getAccount(cid, null) == null) {
            DialogController.showErrorDialog("Update Record Failed", "");
            return 0.0;
        }
        String query = String.format("SELECT SUM(balance) FROM brs2021_accounts WHERE cid=%d;", cid);
        try {
            Statement stmt = connect.getConnection().createStatement();
            ResultSet result = stmt.executeQuery(query);
            if (result.next()) {
                return result.getDouble(1);
            }
        } catch (SQLException se) {
            se.printStackTrace();
            DialogController.showErrorDialog("Update Record Failed", se.toString());
        }
        return 0.0;
    }

    public Boolean withdraw(int cid, double balance) {
        if (balance <= 0) {
            DialogController.showErrorDialog("Input invalid", "Please check and re-enter");
            return false;
        }
        double remainBalance = RecordHelper.getInstance().getBalance(cid);
        if (remainBalance < balance) {
            DialogController.showErrorDialog("Lack of balance", String.format("You have %.2f in your account, please retry", remainBalance));
            return false;
        } else {
            RecordHelper.getInstance().updateBalance(cid, -balance);
        }
        return true;
    }

    public Boolean updateBalance(int cid, double amount) {
        if (AccountHelper.getInstance().getAccount(cid, null) == null) {
            DialogController.showErrorDialog("Update Record Failed", "User doesn't exits.");
            return false;
        } else {
            try {
                PreparedStatement stmt = connect.getConnection().prepareStatement("INSERT INTO brs2021_accounts(cid,balance,create_time) VALUES (?,?,?)");
                stmt.setInt(1, cid);
                stmt.setDouble(2, amount);
                Timestamp date = new Timestamp(System.currentTimeMillis());
                stmt.setTimestamp(3, date);
                int ret = stmt.executeUpdate();
                if (ret == 1) {
                    return true;
                }
            } catch (SQLException se) {
                se.printStackTrace();
                DialogController.showErrorDialog("Update Record Failed", se.toString());
            }
        }
        return false;
    }

    public Boolean updateRecord(int tid, double amount) {
        String query = String.format("UPDATE brs2021_accounts SET balance=%f WHERE tid=%d", amount, tid);
        try {
            Statement stmt = connect.getConnection().createStatement();
            int ret = stmt.executeUpdate(query);
            if (ret == 1) {
                DialogController.showInfoDialog("", "Update Record Success", "");
                return true;
            } else {
                DialogController.showErrorDialog("Update Record Failed", "Record doesn't exist.");
            }
        } catch (SQLException se) {
            se.printStackTrace();
            DialogController.showErrorDialog("Update Record Failed", se.toString());
        }
        return false;
    }

    public Boolean deleteRecord(int tid) {
        String query = String.format("DELETE FROM brs2021_accounts WHERE tid=%d", tid);
        try {
            Statement stmt = connect.getConnection().createStatement();
            int ret = stmt.executeUpdate(query);
            if (ret == 1) {
                DialogController.showInfoDialog("", "Delete Record Success", "");
                return true;
            } else {
                DialogController.showErrorDialog("Delete Record Failed", "Record doesn't exist.");
            }
        } catch (SQLException se) {
            se.printStackTrace();
            DialogController.showErrorDialog("Delete Record Failed", se.toString());
        }
        return false;
    }

    public void setupSQLTable() {
        String createUsersTable = "CREATE TABLE IF NOT EXISTS brs2021_users " +
                "(id INTEGER not NULL AUTO_INCREMENT, " +
                " uname VARCHAR(255), " +
                " passwd VARCHAR(255)," +
                " role int, " +
                "create_time datetime," +
                " PRIMARY KEY ( id )," +
                "UNIQUE (uname))";


        String createTransactionRecordsTable = "CREATE TABLE IF NOT EXISTS brs2021_accounts " +
                "(tid INTEGER not NULL AUTO_INCREMENT, " +
                " cid int, " +
                " balance numeric(38,2), " +
                "create_time datetime," +
                " PRIMARY KEY ( tid ))";

        String createBankTable = "CREATE TABLE IF NOT EXISTS brs2021_bank " +
                "(id INTEGER not NULL AUTO_INCREMENT, " +
                " name VARCHAR(255), " +
                " address VARCHAR(255), " +
                "create_time datetime," +
                " PRIMARY KEY ( id ))";
        try {
            Statement stmt = connect.getConnection().createStatement();
            stmt.executeUpdate(createUsersTable);
            stmt.executeUpdate(createTransactionRecordsTable);
            stmt.executeUpdate(createBankTable);
        } catch (Exception se) {
            se.printStackTrace();
        }
    }

    public void setupSQL() {
        String createUserSql = "replace into brs2021_users( uname, passwd, role, create_time) values(?,?,?,?)";
        try {
            PreparedStatement stmt = connect.getConnection().prepareStatement(createUserSql);
            stmt.setString(1, "admin");
            stmt.setString(2, HashSHAUtils.toMD5("123456"));
            stmt.setInt(3, 0);
            stmt.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
            stmt.executeUpdate();
            stmt.setString(1, "manage");
            stmt.setString(2, HashSHAUtils.toMD5("123456"));
            stmt.setInt(3, 1);
            stmt.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
            stmt.executeUpdate();

        } catch (Exception se) {
            se.printStackTrace();
        }
    }
}
