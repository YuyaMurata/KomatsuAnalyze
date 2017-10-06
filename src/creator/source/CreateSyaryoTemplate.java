/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package creator.source;

import db.HiveDB;
import db.field.EQP;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.TreeMap;
import obj.SyaryoTemplate;

/**
 *
 * @author ZZ17390
 */
public class CreateSyaryoTemplate {
    //EQP_SYARYO DATA
    public Map<String, SyaryoTemplate> createTemplate(Connection con) {
        TreeMap<String, SyaryoTemplate> syaryoMap = new TreeMap();
        
        try {
            Statement stmt = con.createStatement();

            //EQP_Syaryo
            String sql = String.format("select %s, %s, %s from %s",
                    EQP.Syaryo.KISY, EQP.Syaryo.TYP, EQP.Syaryo.KIBAN, //Unique ID
                    HiveDB.TABLE.EQP_SYARYO
            );
            System.out.println("Running: " + sql);

            ResultSet res = stmt.executeQuery(sql);

            int n = 0;
            while (res.next()) {
                n++;
                //Name
                String kisy = res.getString(EQP.Syaryo.KISY.get());
                String type = res.getString(EQP.Syaryo.TYP.get());
                String kiban = res.getString(EQP.Syaryo.KIBAN.get());

                //Syaryo Template
                SyaryoTemplate syaryo = new SyaryoTemplate(kisy, type, kiban);
                
                syaryoMap.put(syaryo.getName(), syaryo);

                if (n % 10000 == 0) {
                    System.out.println("Syaryo Processed : " + n);
                }
            }

            System.out.println("Total Created SyaryoTemplate = " + n);

            return syaryoMap;
        } catch (SQLException sqlex) {
            sqlex.printStackTrace();
            return null;
        }
    }
    
    //KOMPAS車両に絞込み
    public Map<String, SyaryoTemplate> mergeTemplate(Map<String, SyaryoTemplate> eqp_syaryo, Map<String, SyaryoTemplate> kom_syaryo) {
        TreeMap<String, SyaryoTemplate> syaryoMap = new TreeMap();
        
        for(String name : eqp_syaryo.keySet()){
            if(kom_syaryo.get(name) != null)
                syaryoMap.put(name, new SyaryoTemplate(name));
        }
        
        return syaryoMap;
    }
}
