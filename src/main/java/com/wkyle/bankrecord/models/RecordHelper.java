package com.wkyle.bankrecord.models;

import com.wkyle.bankrecord.Dao.DBConnect;
import com.wkyle.bankrecord.controllers.DialogController;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class RecordHelper {

    private static RecordHelper instance = new RecordHelper();
    private RecordHelper(){
        this.connect = new DBConnect();
    }

    public static RecordHelper getInstance(){
        return instance;
    }

    private DBConnect connect = null;

    public double getBalance(int cid) {
        if (AccountHelper.getInstance().getAccount(cid, null) == null) {
            DialogController.showErrorDialog("Update Record Failed", "");
            return 0.0;
        }
        String query = String.format("SELECT SUM(balance) FROM brs2021_accounts WHERE cid=%d;", cid);
        try {
            Statement stmt = connect.getConnection().createStatement();
            ResultSet result =  stmt.executeQuery(query);
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
            RecordHelper.getInstance().updateRecord(cid, -balance);
        }
        return true;
    }

    public Boolean updateRecord(int cid, double balance) {
        if (AccountHelper.getInstance().getAccount(cid, null) == null) {
            DialogController.showErrorDialog("Update Record Failed", "");
            return false;
        }
        String query = String.format("INSERT INTO brs2021_accounts(cid,balance) VALUES (%d,%f)", cid, balance);
        try {
            Statement stmt = connect.getConnection().createStatement();
            int ret =  stmt.executeUpdate(query);
            if (ret == 1) {
                return true;
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
            int ret =  stmt.executeUpdate(query);
            if (ret == 1) {
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
}
