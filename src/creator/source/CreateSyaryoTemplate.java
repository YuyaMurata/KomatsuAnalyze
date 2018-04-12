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
import creator.template.SyaryoTemplate;
import db.field.Syaryo;

/**
 *
 * @author ZZ17390
 */
public class CreateSyaryoTemplate {
    //EQP_SYARYO DATA
    public Map<String, SyaryoTemplate> createTemplate(Connection con, String machine, String reject, Boolean filter) {
        TreeMap<String, SyaryoTemplate> syaryoMap = new TreeMap();
        
        try {
            Statement stmt = con.createStatement();

            //EQP_Syaryo
            String sql = "";
            if(filter)
                sql = String.format("select e.%s, e.%s, e.%s, e.%s from %s e join %s s on e.%s=s.%s and e.%s=s.%s where e.kisy='%s'",
                    EQP.Syaryo.KISY, EQP.Syaryo.TYP, EQP.Syaryo.SYHK, EQP.Syaryo.KIBAN, //Unique ID
                    HiveDB.TABLE.EQP_SYARYO,
                    HiveDB.TABLE.SYARYO,
                    EQP.Syaryo.KISY,
                    Syaryo._Syaryo.KISY,
                    EQP.Syaryo.KIBAN,
                    Syaryo._Syaryo.KIBAN,
                    machine
                );
            else
                sql = String.format("select %s, %s, %s, %s from %s where kisy='%s'",
                    EQP.Syaryo.KISY, EQP.Syaryo.TYP, EQP.Syaryo.SYHK, EQP.Syaryo.KIBAN, //Unique ID
                    HiveDB.TABLE.EQP_SYARYO,
                    machine
                );
            System.out.println("Running: " + sql);

            ResultSet res = stmt.executeQuery(sql);

            int n = 0;
            while (res.next()) {
                n++;
                //Name
                String kisy = res.getString(EQP.Syaryo.KISY.get());
                String type = res.getString(EQP.Syaryo.TYP.get());
                String s_type = res.getString(EQP.Syaryo.SYHK.get());
                String kiban = res.getString(EQP.Syaryo.KIBAN.get());

                //Syaryo Template
                SyaryoTemplate syaryo = new SyaryoTemplate(kisy, type, s_type, kiban);
                
                //Duplicate
                if(syaryoMap.get(syaryo.getName()) != null)
                    System.out.println(syaryo.getName());
                
                syaryoMap.put(syaryo.getName(), syaryo);

                if (n % 10000 == 0) {
                    //System.out.println("Syaryo Processed : " + n);
                }
            }

            System.out.println("Total Created SyaryoTemplate = " + n);

            return syaryoMap;
        } catch (SQLException sqlex) {
            sqlex.printStackTrace();
            return null;
        }
    }
}
