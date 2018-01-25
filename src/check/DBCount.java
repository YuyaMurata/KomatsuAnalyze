/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package check;

import db.HiveDB;
import static db.HiveDB.getConnection;
import db.field.Komtrax;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ZZ17390
 */
public class DBCount {

    public static void main(String[] args) {
        //DB接続
        Connection con = getConnection(); //HiveDB

        try {
            Statement stmt = con.createStatement();
            List countlist = new ArrayList();

            for (String table : tableList()) {
                System.out.println(table);

                String sql = "select count(*) from " + table + "";
                
                System.out.println("Running:"+sql);
                
                //exec SQL
                ResultSet rs = stmt.executeQuery(sql);

                //Header Info
                ResultSetMetaData rsmd = rs.getMetaData();

                List list = new ArrayList();
                while (rs.next()) {
                    list.add(String.valueOf(rs.getInt(rsmd.getColumnName(1))));
                }
                
                String cnt = String.join(",", list);
                System.out.println(cnt);
                countlist.add(cnt);
            }

            countlist.stream().forEach(System.out::println);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private static List<String> tableList() {
        List list = new ArrayList();

        for (HiveDB.TABLE table : HiveDB.TABLE.values()) {
            if (table.get().equals("komtrax")) {
                for (Komtrax.TABLE kmtable : Komtrax.TABLE.values()) {
                    list.add(kmtable.get());
                }
            } else {
                list.add(table.get());
            }
        }

        return list;
    }
}
