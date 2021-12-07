package com.wkyle.bankrecord.models;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

public class ResultSetPrinter {
    public static void printResultSet(ResultSet rs) throws SQLException {
        ResultSetMetaData resultSetMetaData = rs.getMetaData();
        // Get the number of columns
        int ColumnCount = resultSetMetaData.getColumnCount();
        // Save the array with the maximum length of the current column
        int[] columnMaxLengths = new int[ColumnCount];
        // Caching the result set, the result set may be ordered, so saving with ArrayList becomes disordered.
        ArrayList<String[]> results = new ArrayList<>();
        // Traverse by line
        while (rs.next()) {
            // Save all columns in the current row
            String[] columnStr = new String[ColumnCount];
            // 获取属性值.
            for (int i = 0; i < ColumnCount; i++) {
                // Get the attribute value.
                columnStr[i] = rs.getString(i + 1);
                // Calculate the maximum length of the current column
                columnMaxLengths[i] = Math.max(columnMaxLengths[i], (columnStr[i] == null) ? 0 : columnStr[i].length());
            }
            // Cache this line.
            results.add(columnStr);
        }
        printSeparator(columnMaxLengths);
        printColumnName(resultSetMetaData, columnMaxLengths);
        printSeparator(columnMaxLengths);
        // Traverse the collection output result
        Iterator<String[]> iterator = results.iterator();
        String[] columnStr;
        while (iterator.hasNext()) {
            columnStr = iterator.next();
            for (int i = 0; i < ColumnCount; i++) {
                // System.out.printf("|%" + (columnMaxLengths[i] + 1) + "s", columnStr[i]);
                System.out.printf("|%" + columnMaxLengths[i] + "s", columnStr[i]);
            }
            System.out.println("|");
        }
        printSeparator(columnMaxLengths);
    }

    /**
     * Output column names.
     *
     * @param resultSetMetaData The metadata object of the result set.
     * @param columnMaxLengths  The length of the maximum length string in each column.
     * @throws SQLException
     */
    private static void printColumnName(ResultSetMetaData resultSetMetaData, int[] columnMaxLengths) throws SQLException {
        int columnCount = resultSetMetaData.getColumnCount();
        for (int i = 0; i < columnCount; i++) {
            // System.out.printf("|%" + (columnMaxLengths[i] + 1) + "s", resultSetMetaData.getColumnName(i + 1));
            System.out.printf("|%" + columnMaxLengths[i] + "s", resultSetMetaData.getColumnName(i + 1));
        }
        System.out.println("|");
    }

    /**
     * Output separator.
     *
     * @param columnMaxLengths Save the length of the longest string in each column of the result set.
     */
    private static void printSeparator(int[] columnMaxLengths) {
        for (int i = 0; i < columnMaxLengths.length; i++) {
            System.out.print("+");
            // for (int j = 0; j < columnMaxLengths[i] + 1; j++) {
            for (int j = 0; j < columnMaxLengths[i]; j++) {
                System.out.print("-");
            }
        }
        System.out.println("+");
    }
}
