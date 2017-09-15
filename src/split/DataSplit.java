/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package split;

import db.HiveDB;
import static db.HiveDB.getConnection;
import field.EQP;
import field.Syaryo;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import json.JsonToSyaryoObj;
import json.SyaryoObjToJson;
import obj.SyaryoObject;

/**
 *
 * @author ZZ17390
 */
public class DataSplit {
    private static String FILENAME = "syaryo_history_ex.json";
    
    public static void main(String[] args) {
        Connection con = getConnection(); //HiveDB
        
        JsonToSyaryoObj obj = new JsonToSyaryoObj();
        Map<String, SyaryoObject> syaryoMap = obj.reader(FILENAME);
        
        Map<String, Map<String, SyaryoObject>> syaryoHistoryMap = new HashMap();
        
         try {
            Statement stmt = con.createStatement();

            //Syaryo
            String sql = String.format("select %s,%s,%s from %s where MAKR_KBN like %s",
                    Syaryo._Syaryo.KISY, Syaryo._Syaryo.TYP, Syaryo._Syaryo.KIBAN, //Unique ID
                    HiveDB.TABLE.SYARYO,
                    "'A%'"
            );
            System.out.println("Running: " + sql);

            ResultSet res = stmt.executeQuery(sql);
            
            //NotExist
            Map<String, SyaryoObject> eMap = new HashMap<>();

            int n = 0;
            while (res.next()) {
                n++;
                //Name
                String kisy     = res.getString(Syaryo._Syaryo.KISY.get());
                String type     = res.getString(Syaryo._Syaryo.TYP.get());
                String kiban    = res.getString(Syaryo._Syaryo.KIBAN.get());
                
                //車両チェック
                SyaryoObject syaryo = syaryoMap.get(kisy+"-"+type+"-"+kiban);

                if(syaryo == null){
                    continue;
                }
                
                syaryoMap.remove(syaryo.getName());
                eMap.put(syaryo.getName(), syaryo);
                
                if (n % 10000 == 0) {
                    System.out.println("Syaryo Processed : " + n);
                }
            }

            System.out.println("Total Processed Syaryo = " + n);
            System.out.println("Total Exists Syaryo = " + eMap.size());
            System.out.println("Total Not Exists Syaryo = " + syaryoMap.size());
            
            //SetMap
            syaryoHistoryMap.put("exist", eMap);
            syaryoHistoryMap.put("notexist", syaryoMap);
            
            SyaryoObjToJson json = new SyaryoObjToJson();
            //保存
            json.write(FILENAME.replace("ex", "ex_komatsu"), syaryoHistoryMap.get("exist"));
            json.write(FILENAME.replace("ex", "ex_tasya"), syaryoHistoryMap.get("notexist"));
            
         }catch(SQLException sqe){
             
         }
    }
}
