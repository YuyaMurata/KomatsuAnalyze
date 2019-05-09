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
import file.CSVFileReadWrite;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author ZZ17390
 */
public class TableCount {

    private static PrintWriter pw;

    public static void main(String[] args) {
        pw = CSVFileReadWrite.writerSJIS("DBCount.csv");
        print("TABLE,KISY,CNT");
        
        //count();
        KISYCount("PC200");
        
        pw.close();
    }
    
    private static void count(){
         Long total = 0L;
         
        for (TABLE table : HiveDB.TABLE.values()) {
            if(table.get().contains("komtrax")){
                continue;
            }
            total += count(table.get(), "");
        }
        
        for(Komtrax.TABLE table : Komtrax.TABLE.values())
            total += count(table.get(), "");
        
        System.out.println("2,Total," + total);
    }

    private static void KISYCount(String kisy) {
        Long total = 0L;
        System.out.println("\n" + kisy);

        for (TABLE table : HiveDB.TABLE.values()) {
            if(table.get().contains("komtrax")){
                continue;
            }
            if(table.get().equals(table.CUSTOMER.get()) || table.get().equals(table.CUSTOMER_COMMON.get()) || table.get().equals(table.PARTS.get()))
                total += joincount(table.get(), kisy);
            //else
            //    total += count(table.get(), kisy);
        }
        
        //for(Komtrax.TABLE table : Komtrax.TABLE.values())
        //    total += count(table.get(), kisy);
        
        System.out.println("2,Total," + total);
    }

    public static Integer count(String table, String kisy) {
        //DB接続
        Connection con = getConnection(); //HiveDB

        try {
            Statement stmt = con.createStatement();
            String sql = "";
            if (kisy.equals("")) {
                sql = String.format("select count(*) from %s", table);
            } else {
                sql = String.format("select count(*) from %s where kisy=\'" + kisy + "\'", table);
            }
            System.out.println("Running: " + sql);

            ResultSet res = stmt.executeQuery(sql);

            Integer cnt = 0;

            while (res.next()) {
                cnt = res.getInt(1);
            }

            print(table + "," + kisy + "," + cnt);

            return cnt;
        } catch (SQLException ex) {
            System.err.println(table+", "+kisy+" NULL");
            return 0;
        }
    }
    
    public static Integer joincount(String table, String kisy) {
        //DB接続
        Connection con = getConnection(); //HiveDB

        try {
            Statement stmt = con.createStatement();
            String sql = "";
            if(table.contains("customer"))
                sql = String.format("select count(*) from %s c join SYARYO s on (c.KKYKCD = s.HY_KKYKCD) where s.kisy=\'" + kisy + "\'", table);
            else
                sql = String.format("select count(*) from %s p join kom_order o on (p.KSYCD = o.KSYCD and p.SBN = o.SBN) where o.kisy=\'" + kisy + "\'", table);
            
            System.out.println("Running: " + sql);

            ResultSet res = stmt.executeQuery(sql);

            Integer cnt = 0;

            while (res.next()) {
                cnt = res.getInt(1);
            }

            print(table + "," + kisy + "," + cnt);

            return cnt;
        } catch (SQLException ex) {
            System.err.println(table+", "+kisy+" NULL");
            return 0;
        }
    }

    private static void print(String str) {
        pw.println(str);
        pw.flush();
    }
}
