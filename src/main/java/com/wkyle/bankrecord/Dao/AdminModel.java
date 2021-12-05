package com.wkyle.bankrecord.Dao;

import com.wkyle.bankrecord.Dao.DBConnect;
import com.wkyle.bankrecord.Dao.RecordHelper;
import com.wkyle.bankrecord.controllers.DialogController;

import java.sql.SQLException;
import java.sql.Statement;

public class AdminModel extends DBConnect {


    // Declare DB objects
    DBConnect conn = null;
    Statement stmt = null;

    public AdminModel() {

        conn = new DBConnect();
    }


    public void createBank(String name, String address) {

        // INSERT INTO BANK TABLE
        try {
            // Execute a query
            System.out.println("Inserting records into the table...");
            stmt = conn.getConnection().createStatement();
            String sql = null;

            // Include all object data to the database table

            sql = "insert into brs2021_bank(name,address,create_time) values ('" + name + "','" + address + "','" + System.currentTimeMillis() + "')";
            stmt.executeUpdate(sql);
            System.out.println("Bank Record created");

            conn.getConnection().close();
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

	public void updateBank(String recordId, String recordBalance) {
		System.out.println("Update Submit button pressed");
		if (recordId == null || recordId.isEmpty() || recordBalance == null || recordBalance.isEmpty()) {
			DialogController.showErrorDialog("Input invalid", "Please check and re-enter.");
		} else {
			try {
				RecordHelper.getInstance().updateRecord(Integer.parseInt(recordId), Double.parseDouble(recordBalance));
			} catch (NumberFormatException e) {
				DialogController.showErrorDialog("Input invalid", e.toString());
			}
		}
	}

	public void deleteBank(String tid) {
		System.out.println("Delete Submit button pressed");
//		String tid = deleteTidTF.getText();
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
}
