/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package creator;

import creator.source.CreateSyaryoTemplate;
import creator.template.SyaryoTemplate;
import db.HiveDB;
import db.field.Komtrax;
import db.field.Syaryo;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import json.JsonToSyaryoTemplate;
import json.SyaryoTemplateToJson;

/**
 *
 * @author ZZ17390
 */
public class GeneralizeAggregateCreator extends HiveDB {
    private static final String KISY="";
    private static final String INDEX = "..\\KomatsuData\\テスト\\syaryo_index.json";
    private static final String FILENAME = "..\\KomatsuData\\テスト\\syaryo_template.json";

    private static Map map = new TreeMap<String, SyaryoTemplate>();

    public static void main(String[] args) throws SQLException {
        //DB接続
        Connection con = getConnection(); //HiveDB

        //Create Key Index
        //createIndex(con);
        //Aggregate
        execute(con);
    }

    private static void createIndex(Connection con) {
        //テンプレート作成
        Map<String, SyaryoTemplate> syaryoTemplate = new CreateSyaryoTemplate().createTemplate(con, KISY, KISY.split("%")[0]+"0", true);
        new SyaryoTemplateToJson().write(INDEX, syaryoTemplate);
    }

    private static void execute(Connection con) throws SQLException {
        Map index = new JsonToSyaryoTemplate().reader(INDEX);

        Statement stmt = con.createStatement();

        //TABLE List
        for (String table : tableList()) {
            System.out.println(table);

            //exec SQL
            ResultSet res = stmt.executeQuery(sql(table));

            //Header Info
            ResultSetMetaData rsmd = res.getMetaData();
            
            //create
            add(index, table, res, rsmd);
        }
        
        new SyaryoTemplateToJson().write(FILENAME, map);
    }

    private static List<String> tableList() {
        List list = new ArrayList();

        for (TABLE table : TABLE.values()) {
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

    private static String sql(Object table) {
        String s = "select * from " + table + " limit 1";
        System.out.println(s);
        return s;
    }

    private static String header(ResultSetMetaData rsmd) throws SQLException {
        List list = new ArrayList();

        for (int i = 1; i <= rsmd.getColumnCount(); i++) {
            list.add(String.valueOf(rsmd.getColumnName(i)));
        }

        return String.join(",", list);
    }
    
    private static String[] elementKey(ResultSet rs) throws SQLException{
        List<String> element = new ArrayList<>();
        element.add(/*kisy   */ rs.getString(Syaryo._Syaryo.KISY.get()));
        element.add(/*type   */ rs.getString(Syaryo._Syaryo.TYP.get()));
        element.add(/*s_type */ rs.getString(Syaryo._Syaryo.SYHK.get()));
        element.add(/*kiban  */ rs.getString(Syaryo._Syaryo.KIBAN.get()));
        
        return element.toArray(new String[element.size()]);
    }

    private static void add(Map index, String table, ResultSet rs, ResultSetMetaData rsmd){
        try{
        String header = header(rsmd);
        while (rs.next()) {
            //centralize elemnt
            String[] keyElement = elementKey(rs);
            
            //key
            //String key = keyElement[0]+keyElement[1]+keyElement[2]+keyElement[3];
            //System.out.println(key);
            String key = SyaryoTemplate.check(keyElement[0], keyElement[1], keyElement[2], keyElement[3]);
            
            if (index.get(key) == null) {
                //error process
            } else {
                //create template
                SyaryoTemplate syaryo;
                if (map.get(key) == null) {
                    syaryo = new SyaryoTemplate(keyElement[0], keyElement[1], keyElement[2], keyElement[3]);
                } else {
                    syaryo = (SyaryoTemplate) map.get(key);
                }

                syaryo.add(table, header, record(rs, rsmd));
                map.put(key, syaryo);
            }
        }
        }catch(SQLException sql){
            sql.printStackTrace();
        }
    }
    
    private static String record(ResultSet rs, ResultSetMetaData rsmd) throws SQLException{
        List list = new ArrayList();
        
        for (int i = 1; i <= rsmd.getColumnCount(); i++)
            list.add(String.valueOf(rs.getString(rsmd.getColumnName(i))));
        
        return String.join(",", list);
    }
}
