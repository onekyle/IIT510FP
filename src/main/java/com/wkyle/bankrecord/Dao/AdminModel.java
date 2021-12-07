package com.wkyle.bankrecord.Dao;

import com.wkyle.bankrecord.controllers.DialogController;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

public class AdminModel {


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
//            sql = "insert into brs2021_bank(name,address,create_time) values ('" + name + "','" + address + "','" + System.currentTimeMillis() + "')";

            PreparedStatement stmt = conn.getConnection().prepareStatement("insert into brs2021_bank(name,address,create_time) VALUES (?,?,?)");
            stmt.setString(1, name);
            stmt.setString(2, address);
            Timestamp date = new Timestamp(System.currentTimeMillis());
            stmt.setTimestamp(3, date);
            int ret = stmt.executeUpdate();
            System.out.println("Bank Record created");

            conn.getConnection().close();
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }
}
