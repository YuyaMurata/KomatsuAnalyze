/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package viewer.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author zz17390
 */
public class DataToSQLLite {

    Connection c;

    public DataToSQLLite() {
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:db/csvtemp.db");
        } catch (ClassNotFoundException ex) {
        } catch (SQLException ex) {
        }
    }

    public Integer toSQLLite(DataTransaction dt) {
        int n = -1;
        try (Statement stmt = c.createStatement()) {
            //Prepared
            //stmt.executeUpdate(dt.preparedSQL());

            //Create Table
            //System.out.println("Table:"+dt.getCreateTableSQL());
            stmt.executeUpdate(dt.getCreateTableSQL());

            //Insert Data
            //System.out.println(dt.getDataSQL());
            stmt.executeUpdate(dt.getDataSQL());


        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return n;
    }

    public void toCSV(List<String> table) {
        try (Statement stmt = c.createStatement()) {
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT * FROM ");
            sql.append(String.join(",", table));
            sql.append(" WHERE ");
            for (int i = 1; i < table.size() - 2; i++) {
                sql.append("SID.");
                sql.append(table.get(i));
                sql.append("=");
                sql.append("SID.");
                sql.append(table.get(i + 1));
            }
            ResultSet rs = stmt.executeQuery("SELECT * FROM ");

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public Integer dataCount(Set<String> tables, String syaryo) {
        int n = 0;
        for (String table : tables) {
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT COUNT(*) FROM ");
            sql.append(table);
            sql.append(" WHERE 機種・型・機番=\"");
            sql.append(syaryo);
            sql.append("\";");
            
            System.out.println(sql.toString());
            try (Statement stmt = c.createStatement()) {
                //Count Recode
                ResultSet rs = stmt.executeQuery(sql.toString());
                while (rs.next())
                    n += rs.getInt(1);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        
        return n;
    }

    public void close() {
        try {
            c.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
