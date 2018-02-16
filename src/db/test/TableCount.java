/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package db.test;

import db.HiveDB;
import db.HiveDB.TABLE;
import static db.HiveDB.getConnection;
import db.field.Komtrax;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author ZZ17390
 */
public class TableCount {

    public static void main(String[] args) {
        Long total = 0L;
        
        /*for(TABLE table : HiveDB.TABLE.values()){
            if(table.get().contains("komtrax"))
                continue;
            total += count(table.get());
        }*/
        
        for(Komtrax.TABLE table : Komtrax.TABLE.values())
            total += count(table.get());
        
        System.out.println("2,Total,"+total);
    }

    public static Integer count(String table) {
        //DB接続
        Connection con = getConnection(); //HiveDB

        try {
            Statement stmt = con.createStatement();
            String sql = String.format("select count(*) from %s", table);
            System.out.println("Running: " + sql);

            ResultSet res = stmt.executeQuery(sql);

            Integer cnt = 0;
            while (res.next()) {
                cnt = res.getInt(1);
            }
            System.out.println("1,"+table + ":" + table + "," + cnt);
            
            return cnt;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
