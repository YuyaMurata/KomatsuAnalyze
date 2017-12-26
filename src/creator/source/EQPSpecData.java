/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package creator.source;

import db.HiveDB;
import db.field.EQP;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.TreeMap;
import creator.template.SyaryoTemplate;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ZZ17390
 */
public class EQPSpecData {
    private static List nonUpdateSyaryoList = new ArrayList();
    
    //EQP_SPEC DATA
    public Map<String, SyaryoTemplate> addEQPSpec(Connection con, PrintWriter errpw, Map<String, SyaryoTemplate> syaryoMap) {
        Map<String, SyaryoTemplate> map = new TreeMap<>();
        
        try {
            Statement stmt = con.createStatement();

            //EQP_Spec
            String sql = String.format("select s.%s,s.%s,s.%s,s.%s, s.%s, u.%s, u.%s from %s s join %s u on (s.kisy=u.kisy and s.typ=u.typ and s.SALES_UNIT_CD=u.SALES_UNIT_CD)",
                    EQP.Spec.KISY, EQP.Spec.TYP, EQP.Spec.SYHK, EQP.Spec.KIBAN, //Unique ID
                    EQP.Spec.SALES_UNIT_CD,
                    EQP.Hanbai.CTG_M_NM,
                    EQP.Hanbai.CTG_S_NM,
                    HiveDB.TABLE.EQP_SPEC,
                    HiveDB.TABLE.EQP_HANBAI
            );
            System.out.println("Running: " + sql);

            ResultSet res = stmt.executeQuery(sql);
            
            int n = 0;
            int m = 0;
            while (res.next()) {
                n++;
                //Name
                String kisy = res.getString(EQP.Spec.KISY.get());
                String type = res.getString(EQP.Spec.TYP.get());
                String s_type = res.getString(EQP.Spec.SYHK.get());
                String kiban = res.getString(EQP.Spec.KIBAN.get());
                
                //SpecDetail
                String unit = res.getString(EQP.Spec.SALES_UNIT_CD.get());
                String spec_m_name = res.getString(EQP.Hanbai.CTG_M_NM.get());
                String spec_s_name = res.getString(EQP.Hanbai.CTG_S_NM.get());
                
                //DB
                String db = "eqp_spec_unit";
                String company = "??";
                
                //車両チェック
                String name = SyaryoTemplate.check(kisy, type, s_type, kiban);
                if (name == null) {
                    errpw.println(n + "," + SyaryoTemplate.getName(kisy, type, s_type, kiban) + "," + db + "," + company + "," + unit + "," + spec_m_name + "," + spec_s_name);
                    continue;
                }
                
                //車両
                SyaryoTemplate syaryo = syaryoMap.get(name);
                
                m++;

                //Syaryo Spec Detail
                syaryo.addDetail(unit, spec_m_name, spec_s_name);
                
                map.put(syaryo.getName(), syaryo);

                if (n % 10000 == 0) {
                    System.out.println("Syaryo Processed : " + n);
                }
            }

            System.out.println("Total Processed Syaryo = "+  m + "/" + n);
            System.out.println("Total Created SyaryoTemplate = " + map.size() + "/" + syaryoMap.size());
            
            for(String name : syaryoMap.keySet())
                if(map.get(name) == null)
                    nonUpdateSyaryoList.add(name);
            
            return map;
        } catch (SQLException sqlex) {
            sqlex.printStackTrace();
            return null;
        }
    }
    
    public static List dataCheck(){
        return nonUpdateSyaryoList;
    }
}
