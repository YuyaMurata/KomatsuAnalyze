/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package creator.source;

import db.HiveDB;
import db.field.EQP;
import db.field.Syaryo;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.TreeMap;
import creator.template.SyaryoTemplate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author ZZ17390
 */
public class EQPSyaryoData {
    private static List nonUpdateSyaryoList = new ArrayList();
    
    //EQP_SYARYO DATA
    public Map<String, SyaryoTemplate> addEQPSyaryo(Connection con, PrintWriter errpw, Map<String, SyaryoTemplate> syaryoMap) {
        Map<String, SyaryoTemplate> map = new TreeMap<>();
        
        try {
            Statement stmt = con.createStatement();

            //EQP_Syaryo
            String sql = String.format("select e.%s,e.%s,e.%s, e.%s, e.%s, e.%s, e.%s, e.%s, e.%s, e.%s, e.%s, e.%s, e.%s, e.%s, e.%s, e.%s, e.%s, e.%s, s.%s from %s e join %s s on (e.kisy=s.kisy and e.kiban=s.kiban)",
                    EQP.Syaryo.KISY, EQP.Syaryo.TYP, EQP.Syaryo.SYHK, EQP.Syaryo.KIBAN, //Unique ID
                    EQP.Syaryo.MNF_DATE, //生産日
                    EQP.Syaryo.PLANT, //生産工場(catalog)
                    EQP.Syaryo.SHIP_DATE, //出荷日
                    EQP.Syaryo.NEW_DELI_DATE, //新車納入日
                    EQP.Syaryo.DELI_CTG, //中古納入区分
                    EQP.Syaryo.LTST_DELI_DATE, //最新納入日
                    EQP.Syaryo.SCRAP_DATE, //廃車日
                    EQP.Syaryo.CUST_CD, //顧客コード
                    EQP.Syaryo.CUST_NM, //顧客名
                    EQP.Syaryo.DB, //代理店
                    EQP.Syaryo.LTST_SMR_DATE, //最新SMR日付
                    EQP.Syaryo.LTST_SMR, //SMR
                    EQP.Syaryo.KMTRX_APP_CTG, //Komtrax
                    EQP.Syaryo.VHMS_APP_CTG, //Komtrax plus
                    Syaryo._Syaryo.SEHN_BNR_CD_B, //製品分類コード B
                    HiveDB.TABLE.EQP_SYARYO,
                    HiveDB.TABLE.SYARYO
            );
            System.out.println("Running: " + sql);

            ResultSet res = stmt.executeQuery(sql);
            
            int n = 0;
            int m = 0;
            while (res.next()) {
                n++;
                //Name
                String kisy = res.getString(EQP.Syaryo.KISY.get());
                String type = res.getString(EQP.Syaryo.TYP.get());
                String s_type = res.getString(EQP.Syaryo.SYHK.get());
                String kiban = res.getString(EQP.Syaryo.KIBAN.get());

                //Date
                String mnf_date = res.getString(EQP.Syaryo.MNF_DATE.get());
                String ship_date = res.getString(EQP.Syaryo.SHIP_DATE.get());
                String new_date = res.getString(EQP.Syaryo.NEW_DELI_DATE.get());
                String used_date = res.getString(EQP.Syaryo.LTST_DELI_DATE.get());
                String scrap_date = res.getString(EQP.Syaryo.SCRAP_DATE.get());
                String smr_date = res.getString(EQP.Syaryo.LTST_SMR_DATE.get());
                if(!smr_date.equals("")) smr_date = smr_date.substring(0, 8);

                //Plant
                String plant = res.getString(EQP.Syaryo.PLANT.get());
                
                //Spec
                String komtrax = res.getString(EQP.Syaryo.KMTRX_APP_CTG.get());
                String komtrax_plus = res.getString(EQP.Syaryo.VHMS_APP_CTG.get());
                String category = res.getString(Syaryo._Syaryo.SEHN_BNR_CD_B.get());
                if(komtrax.equals("Y") || komtrax_plus.equals("Y"))
                    komtrax = "1";
                else
                    komtrax = "0";
                
                //Used
                String used_flg = res.getString(EQP.Syaryo.DELI_CTG.get());

                //Customer
                String cid = res.getString(EQP.Syaryo.CUST_CD.get());
                String cname = res.getString(EQP.Syaryo.CUST_NM.get());

                //DB
                String db = "eqp_syaryo";
                String company = res.getString(EQP.Syaryo.DB.get());
                if (company.length() > 1) {
                    company = company.substring(0, 2);
                } else {
                    company = "??";
                }

                //SMR
                String smr = res.getString(EQP.Syaryo.LTST_SMR.get());
                
                //車両チェック
                String name = SyaryoTemplate.check(kisy, type, s_type, kiban);
                if (name == null) {
                    errpw.println(n + "," + SyaryoTemplate.getName(kisy, type, s_type, kiban) + "," + smr_date + "," + db + "," + company + "," + cid + "," + cname + "," + plant
                                    + "," + mnf_date + "," + ship_date + "," + scrap_date + "," + used_flg + "," + used_date + "," + smr);
                    continue;
                }
                
                //車両
                SyaryoTemplate syaryo = syaryoMap.get(name);
                
                m++;

                //Syaryo Template
                syaryo.addBorn(mnf_date, plant);
                syaryo.addDeploy(ship_date);
                syaryo.addDead(db, company, scrap_date);
                
                //納入
                //新車
                syaryo.addNew(db, company, new_date, "-1", "-1", "-1");
                //中古
                if (used_flg.equals("1")) {
                    syaryo.addUsed(db, company, used_date, "-1", "-1", "-1");
                    syaryo.addOwner(db, company, used_date, "?-?", cid, cname);
                    syaryo.addOwner(db, company, new_date, "-1", "-1", "?");
                }else{
                    syaryo.addOwner(db, company, new_date, "?-?", cid, cname);
                }
                
                //Spec
                syaryo.addSpec(komtrax, category);
                
                //SMR
                syaryo.addSMR(db, company, smr_date, smr);
                syaryo.addLast(db, company, smr_date);
                
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
