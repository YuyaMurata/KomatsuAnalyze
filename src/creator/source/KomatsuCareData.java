/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package creator.source;

import creator.template.SyaryoTemplate;
import db.HiveDB;
import db.field.Care;
import db.field.EQP;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author ZZ17390
 */
public class KomatsuCareData {

    private static List nonUpdateSyaryoList;

    //ORDER DATA
    public Map<String, SyaryoTemplate> addCare(Connection con, PrintWriter errpw, String machine, Map<String, SyaryoTemplate> syaryoMap) {
        Map<String, SyaryoTemplate> map = new TreeMap();
        try {
            Statement stmt = con.createStatement();

            //EQP_Syaryo
            String sql = String.format("select %s,%s,%s,%s, %s, %s, %s, %s, %s, %s from %s where kisy='%s'",
                Care._Care.KISY, Care._Care.TYP, Care._Care.SYHK, Care._Care.KIBAN, //Unique ID
                Care._Care.TNK_DATE, //転記日
                Care._Care.HSY_KIND, //保証種別
                Care._Care.DAIRI_CD, //代理店CD
                Care._Care.CRM_NO, //クレーム番号
                Care._Care.PRICE, //金額
                Care._Care.SMR,   //SMR
                HiveDB.TABLE.CARE,
                machine
            );
            System.out.println("Running: " + sql);

            ResultSet res = stmt.executeQuery(sql);

            int n = 0;
            int m = 0;
            while (res.next()) {
                n++;

                //Name
                String kisy = res.getString(Care._Care.KISY.get());
                String type = res.getString(Care._Care.TYP.get());
                String s_type = res.getString(Care._Care.SYHK.get());
                String kiban = res.getString(Care._Care.KIBAN.get());

                //Date
                String date = res.getString(Care._Care.TNK_DATE.get());

                //保証種別
                String kind = res.getString(Care._Care.HSY_KIND.get());

                //代理店コード
                String id = res.getString(Care._Care.DAIRI_CD.get());
                
                //クレーム番号
                String c_no = res.getString(Care._Care.CRM_NO.get());

                //Price
                String price = res.getString(Care._Care.PRICE.get());

                //SMR
                String smr = res.getString(Care._Care.SMR.get());

                //DB
                String db = "gcps_care";
                String company = "";
                if (company.length() > 1) {
                    company = company.substring(0, 2);
                } else {
                    company = "??";
                }

                //車両チェック
                String name = SyaryoTemplate.check(kisy, type, s_type, kiban);
                if (name == null || SyaryoTemplate.errorCheck(date)) {
                    errpw.println(n + "," + SyaryoTemplate.getName(kisy, type, s_type, kiban) + "," + date + "," + db + "," + company + "," + kind + "," + id + "," + c_no + "," + price + "," + smr);
                    continue;
                }

                //車両
                SyaryoTemplate syaryo;
                if (map.get(name) == null) {
                    syaryo = syaryoMap.get(name).clone();
                } else {
                    syaryo = map.get(name);
                }

                m++;

                //Add SyaryoTemplate
                syaryo.addCare(db, company, date, id, c_no, kind, price);
                syaryo.addSMR(db, company, date, smr);

                if (n % 10000 == 0) {
                    System.out.println("Syaryo Processed : " + n);
                }

                map.put(syaryo.getName(), syaryo);
            }

            System.out.println("Total Processed Syaryo = " + m + "/" + n);
            System.out.println("Total Created SyaryoTemplate = " + map.size() + "/" + syaryoMap.size());

            for (String name : syaryoMap.keySet()) {
                if (map.get(name) == null) {
                    nonUpdateSyaryoList.add(name);
                }
            }

            return map;

        } catch (SQLException sqle) {
            sqle.printStackTrace();
            return null;
        }
    }

    public static List dataCheck() {
        return nonUpdateSyaryoList;
    }
}
