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
import obj.SyaryoTemplate;

/**
 *
 * @author ZZ17390
 */
public class EQPKeirekiData {
    //EQP_KEIREKI DATA
    public Map<String, SyaryoTemplate> addSyaryoKeireki(Connection con, PrintWriter errpw, Map<String, SyaryoTemplate> syaryoMap, Map<String, SyaryoTemplate> noneType) throws IOException {
        try {
            Statement stmt = con.createStatement();

            //EQP_Syaryo
            String sql = String.format("select %s,%s,%s, %s, %s, %s, %s, %s, %s, %s from %s",
                    EQP.Keireki.KISY, EQP.Keireki.TYP, EQP.Keireki.KIBAN, //Unique ID
                    EQP.Keireki.HIS_DATE, //経歴日
                    EQP.Keireki.HIS_INFO_CD, //経歴コード
                    EQP.Keireki.HIS_SMR, //SMR
                    EQP.Keireki.CNTRY, //国コード
                    EQP.Keireki.CUST_CD, //顧客コード
                    EQP.Keireki.CUST_NM, //顧客名
                    EQP.Keireki.DB, //代理店
                    HiveDB.TABLE.EQP_KEIREKI
            );
            System.out.println("Running: " + sql);

            ResultSet res = stmt.executeQuery(sql);

            Map<String, SyaryoTemplate> map = new TreeMap<>();

            int n = 0;
            while (res.next()) {
                n++;

                //Name
                String kisy = res.getString(EQP.Keireki.KISY.get());
                String type = res.getString(EQP.Keireki.TYP.get());
                String kiban = res.getString(EQP.Keireki.KIBAN.get());
                
                //車両
                SyaryoTemplate syaryo = null;
                syaryo = syaryoMap.get(kisy + "-" + type + "-" + kiban);
                if((syaryo == null) && (noneType.get(kisy + "-" + kiban) != null))
                    syaryo = syaryoMap.get(noneType.get(kisy + "-" + kiban));
                
                //Date
                String date = res.getString(EQP.Keireki.HIS_DATE.get());

                //Kireki
                String id = res.getString(EQP.Keireki.HIS_INFO_CD.get());

                //Customer
                String cid = res.getString(EQP.Keireki.CUST_CD.get());
                String cname = res.getString(EQP.Keireki.CUST_NM.get());

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
                String name = kisy + "-" + type + "-" + kiban;
                if (syaryo == null) {
                    errpw.println(n + "," + name + "," + date + "," + db + "," + company + "," + cid + "," + cname + "," + country + "," + id + "," + smr);
                    continue;
                }

                //Add SyaryoTemplate
                syaryo.addHistory(date, db, company, id);
                syaryo.addOwner(db, company, date, "-1", cid, cname);
                syaryo.addSMR(db, company, date, smr);
                syaryo.addCountry(db, company, date, country);

                if (n % 10000 == 0) {
                    System.out.println("Syaryo Processed : " + n);
                }

                map.put(syaryo.getName(), syaryo);
            }

            System.out.println("Total Processed Syaryo = " + n);
            System.out.println("Total Update Syaryo = " + map.size());

            return map;

        } catch (SQLException sqle) {
            sqle.printStackTrace();
            return null;
        }
    }
}
