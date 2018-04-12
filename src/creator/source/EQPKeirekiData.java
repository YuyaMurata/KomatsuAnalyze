/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package creator.source;

import db.HiveDB;
import db.field.EQP;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.TreeMap;
import creator.template.SyaryoTemplate;
import db.field.Customer;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ZZ17390
 */
public class EQPKeirekiData {
    private static List nonUpdateSyaryoList = new ArrayList();
    
    //EQP_KEIREKI DATA
    public Map<String, SyaryoTemplate> addSyaryoKeireki(Connection con, PrintWriter errpw, String machine, Map<String, SyaryoTemplate> syaryoMap) throws IOException {
        Map<String, SyaryoTemplate> map = new TreeMap<>();
        
        try {
            Statement stmt = con.createStatement();

            //EQP_Syaryo
            String sql = String.format("select e.%s,e.%s,e.%s,e.%s, e.%s, e.%s, e.%s, e.%s, e.%s, e.%s, e.%s, c.%s, c.%s, c2.%s from %s e join %s c on (e.%s=c.%s) join %s c2 on (e.%s=c2.%s) where e.kisy='%s'",
                    EQP.Keireki.KISY, EQP.Keireki.TYP, EQP.Keireki.SYHK, EQP.Keireki.KIBAN, //Unique ID
                    EQP.Keireki.HIS_DATE, //経歴日
                    EQP.Keireki.HIS_INFO_CD, //経歴コード
                    EQP.Keireki.HIS_SMR, //SMR
                    EQP.Keireki.CNTRY, //国コード
                    EQP.Keireki.CUST_CD, //顧客コード
                    EQP.Keireki.CUST_NM, //顧客名
                    EQP.Keireki.DB, //代理店
                    Customer.Common.GYSD_BNRCD,
                    Customer.Common.GYSCD,
                    Customer._Customer.KKYK_KBN,
                    HiveDB.TABLE.EQP_KEIREKI,
                    HiveDB.TABLE.CUSTOMER_COMMON,
                    EQP.Keireki.CUST_CD,
                    Customer.Common.KKYKCD,
                    HiveDB.TABLE.CUSTOMER,
                    EQP.Keireki.CUST_CD,
                    Customer._Customer.KKYKCD,
                    machine
            );
            System.out.println("Running: " + sql);

            ResultSet res = stmt.executeQuery(sql);

            int n = 0;
            int m = 0;
            while (res.next()) {
                n++;

                //Name
                String kisy = res.getString(EQP.Keireki.KISY.get());
                String type = res.getString(EQP.Keireki.TYP.get());
                String s_type = res.getString(EQP.Keireki.SYHK.get());
                String kiban = res.getString(EQP.Keireki.KIBAN.get());
                
                //Date
                String date = res.getString(EQP.Keireki.HIS_DATE.get());

                //Kireki
                String id = res.getString(EQP.Keireki.HIS_INFO_CD.get());

                //Customer
                String ckbn = res.getString(Customer._Customer.KKYK_KBN.get());
                String cid = res.getString(EQP.Keireki.CUST_CD.get());
                String cname = res.getString(EQP.Keireki.CUST_NM.get());
                String gyosyu = res.getString(Customer.Common.GYSD_BNRCD.get())
                        + "-" + res.getString(Customer.Common.GYSCD.get());   //納入先業種

                //Country
                String country = res.getString(EQP.Keireki.CNTRY.get());

                //SMR
                String smr = res.getString(EQP.Keireki.HIS_SMR.get());

                //DB
                String db = "eqp_keireki";
                String company = res.getString(EQP.Keireki.DB.get());
                if (company.length() > 1) {
                    company = company.substring(0, 2);
                } else {
                    company = "??";
                }

                //車両チェック
                String name = SyaryoTemplate.check(kisy, type, s_type, kiban);
                if (name == null || SyaryoTemplate.errorCheck(date)) {
                    errpw.println(n + "," + SyaryoTemplate.getName(kisy, type, s_type, kiban) + "," + date + "," + db + "," + company + "," + ckbn + "," + gyosyu + "," + cid + "," + cname + "," + country + "," + id + "," + smr);
                    continue;
                }
                
                //車両
                SyaryoTemplate syaryo;
                if(map.get(name) == null)
                    syaryo = syaryoMap.get(name).clone();
                else
                    syaryo = map.get(name);
                
                m++;

                //Add SyaryoTemplate
                syaryo.addHistory(db, company, date, id);
                syaryo.addOwner(db, company, date, ckbn, gyosyu, cid, cname);
                syaryo.addSMR(db, company, date, smr);
                syaryo.addCountry(db, company, date, country);

                if (n % 10000 == 0) {
                    System.out.println("Syaryo Processed : " + n);
                }

                map.put(syaryo.getName(), syaryo);
            }

            System.out.println("Total Processed Syaryo = "+  m + "/" + n);
            System.out.println("Total Created SyaryoTemplate = " + map.size() + "/" + syaryoMap.size());
            
            for(String name : syaryoMap.keySet())
                if(map.get(name) == null)
                    nonUpdateSyaryoList.add(name);
            
            return map;

        } catch (SQLException sqle) {
            sqle.printStackTrace();
            return null;
        }
    }
    
    public static List dataCheck(){
        return nonUpdateSyaryoList;
    }
}
