/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package db.test;

import static db.HiveDB.getConnection;
import file.MapToJSON;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author ZZ17807
 */
public class TestMetaSetGenerator {

    public static void main(String[] args) {
        tableToMetaSet("SERVICE");
    }

    public static void tableToMetaSet(String table) {
        //DB接続
        Connection con = getConnection(); //HiveDB

        try {
            Statement stmt = con.createStatement();

            String sql = "select * from " + table;

            System.out.println("Running:" + sql);
            //exec SQL
            ResultSet rs = stmt.executeQuery(sql);

            //Header Info
            ResultSetMetaData rsmd = rs.getMetaData();
            
            TestGenMeta meta = new TestGenMeta(table);
            
            while (rs.next()) {
                for(int i=1; i < rsmd.getColumnCount()+1; i++){
                    String field = rsmd.getColumnName(i);
                    String data = rs.getString(field);
                    meta.setData(field, data);
                }
                meta.total++;
                
                if((meta.total % 100000) == 0)
                    System.out.println(meta.total +" 件 読み込み");
            }
            
            meta.mapping();
            
            new MapToJSON().toJSON("test_"+table+".json", meta.metaSet);
            
        } catch (SQLException ex) {
            //System.out.println("Not kisy and type !");
            ex.printStackTrace();
        }
    }
}
