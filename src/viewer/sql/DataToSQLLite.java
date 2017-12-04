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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author zz17390
 */
public class DataToSQLLite {
    Connection c;
    public DataToSQLLite(){
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:db/csvtemp.db");
        } catch (ClassNotFoundException ex) {
        } catch (SQLException ex) {
        }
    }
    
    public Integer toSQLLite(DataTransaction dt){
        int n = -1;
        try(Statement stmt = c.createStatement()) {
            //Create Table
            System.out.println("Table:"+dt.getCreateTableSQL());
            stmt.executeUpdate(dt.getCreateTableSQL());
            
            //Insert Data
            System.out.println(dt.getDatSQL());
            stmt.executeUpdate(dt.getDatSQL());
            
            //Count Recode
            ResultSet rs = stmt.executeQuery(dt.getRecordCountSQL());
            while(rs.next())
                n = rs.getInt(1);
            
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        
        return n;
    }
}
