package com.wkyle.bankrecord.Dao;

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
}
