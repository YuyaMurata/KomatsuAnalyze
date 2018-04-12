/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package creator.source;

import creator.template.SyaryoTemplate;
import db.HiveDB;
import db.field.Allsupport;
import db.field.Care;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author ZZ17390
 */
public class AllSupport {

    private static List nonUpdateSyaryoList;

    //CARE DATA
    public Map<String, SyaryoTemplate> addAllsupport(Connection con, PrintWriter errpw, String machine, Map<String, SyaryoTemplate> syaryoMap) {
        nonUpdateSyaryoList = new ArrayList();
        
        Map<String, SyaryoTemplate> map = new TreeMap();
        try {
            Statement stmt = con.createStatement();

            //EQP_Syaryo
            String sql = String.format("select %s,%s,%s,%s, %s, %s, %s, %s, %s from %s where kisy='%s'",
                Allsupport._Allsupport.KISY, Allsupport._Allsupport.TYP, Allsupport._Allsupport.SYHK, Allsupport._Allsupport.KIBAN, //Unique ID
                Allsupport._Allsupport.DB,
                Allsupport._Allsupport.KSYCD,
                Allsupport._Allsupport.AS_REG_DATE, //契約開始
                Allsupport._Allsupport.M_MNRY_DATE, //契約満了
                Allsupport._Allsupport.PLAN,
                HiveDB.TABLE.ALLSUPPORT.get(),
                machine
            );
            System.out.println("Running: " + sql);

            ResultSet res = stmt.executeQuery(sql);

            int n = 0;
            int m = 0;
            while (res.next()) {
                n++;

                //Name
                String kisy = res.getString(Allsupport._Allsupport.KISY.get());
                String type = res.getString(Allsupport._Allsupport.TYP.get());
                String s_type = res.getString(Allsupport._Allsupport.SYHK.get());
                String kiban = res.getString(Allsupport._Allsupport.KIBAN.get());

                //Date
                String st_date = res.getString(Allsupport._Allsupport.AS_REG_DATE.get());
                String fn_date = res.getString(Allsupport._Allsupport.M_MNRY_DATE.get());

                //保証種別
                String kind = res.getString(Allsupport._Allsupport.PLAN.get());

                //DB
                String db = res.getString(Allsupport._Allsupport.DB.get());
                String company = res.getString(Allsupport._Allsupport.KSYCD.get());

                //車両チェック
                String name = SyaryoTemplate.check(kisy, type, s_type, kiban);
                if (name == null) {
                    errpw.println(n + "," + SyaryoTemplate.getName(kisy, type, s_type, kiban) + "," + st_date + "," + db + "," + company + "," + kind + "," + fn_date +","+ kind);
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
                syaryo.addAllSupport(db, company, kind, st_date, fn_date);

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
    
    //CARE PrePrice DATA
    public Map<String, SyaryoTemplate> addPreprice(Connection con, PrintWriter errpw, String machine, Map<String, SyaryoTemplate> syaryoMap) {
        nonUpdateSyaryoList = new ArrayList();
        
        Map<String, SyaryoTemplate> map = new TreeMap();
        try {
            Statement stmt = con.createStatement();
            
            System.out.println(HiveDB.TABLE.CARE_PRICE.get());
            //EQP_Syaryo
            String sql = String.format("select %s,%s,%s,%s, %s from %s where kisy='%s'",
                Care.PrePrice.KISY, Care.PrePrice.TYP, Care.PrePrice.SYHK, Care.PrePrice.KIBAN, //Unique ID
                Care.PrePrice.PRICE, //金額
                HiveDB.TABLE.CARE_PRICE.get(),
                machine
            );
            System.out.println("Running: " + sql);

            ResultSet res = stmt.executeQuery(sql);

            int n = 0;
            int m = 0;
            while (res.next()) {
                n++;

                //Name
                String kisy = res.getString(Care.PrePrice.KISY.get());
                String type = res.getString(Care.PrePrice.TYP.get());
                String s_type = res.getString(Care.PrePrice.SYHK.get());
                String kiban = res.getString(Care.PrePrice.KIBAN.get());

                //Price
                String price = res.getString(Care.PrePrice.PRICE.get());

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
                if (name == null) {
                    errpw.println(n + "," + SyaryoTemplate.getName(kisy, type, s_type, kiban) + "," + db + "," + company + "," + price);
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
                syaryo.addCarePrePrice(db, company, price);

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
