/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package creator.source;

import db.HiveDB;
import db.field.Order;
import db.field.Work;
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
public class WorkData {
    private static List nonUpdateSyaryoList = new ArrayList();
    
    //WORK_INFO DATA
    public Map<String, SyaryoTemplate> addWork(Connection con, PrintWriter errpw, String machine, Map<String, SyaryoTemplate> syaryoMap) {
        Map<String, SyaryoTemplate> map = new TreeMap();

        try {
            Statement stmt = con.createStatement();

            //Syaryo
            String sql = String.format("select o.%s,o.%s,o.%s,o.%s, w.%s, w.%s, w.%s, o.%s, w.%s, w.%s, w.%s, w.%s, w.%s, w.%s, w.%s, w.%s, w.%s, w.%s, w.%s, o.%s, o.%s, w.%s from %s w join %s o on (w.%s=o.%s and w.%s=o.%s) where o.kisy like '%s'",
                    Order._Order.KISY, Order._Order.TYP, Order._Order.SYHK, Order._Order.KIBAN, //Unique ID
                    Work.Info.KSYCD, //会社コード
                    Work.Info.SBN, //作番
                    Work.Info.SGYO_MSINO, //作業明細番号
                    Order._Order.ODDAY, //受注日
                    Work.Info.SGYOCD, //作業コード
                    Work.Info.SGYO_NM, //作業名
                    Work.Info.SGYO_KRFLG, //作業管理フラグ
                    Work.Info.HJUN_KOS, //標準工数
                    Work.Info.INV_KOS, //請求工数
                    Work.Info.SIJI_KOS, //指示工数
                    Work.Info.JRKOS, //実績累計工数
                    Work.Info.ODR_SU, //受注数量
                    Work.Info.GTWT_UMFLG, //外注有無
                    Work.Info.JKT_GKGK, //純工賃合計原価
                    Work.Info.GT_GKGK, //外注合計原価
                    Order._Order.SGYO_KTICD, //作業形態コード
                    Order._Order.SGKT_NM, //作業形態名
                    Work.Info.LAST_UPD_DAYT,
                    HiveDB.TABLE.WORK_INFO,
                    HiveDB.TABLE.ORDER.get(),
                    Work.Info.KSYCD, Order._Order.KSYCD,
                    Work.Info.SBN, Order._Order.SBN,
                    machine
            );
            System.out.println("Running: " + sql);

            ResultSet res = stmt.executeQuery(sql);

            int n = 0;
            int m = 0;
            while (res.next()) {
                n++;

                //Name
                String kisy = res.getString(Order._Order.KISY.get());
                String type = res.getString(Order._Order.TYP.get());
                String s_type = res.getString(Order._Order.SYHK.get());
                String kiban = res.getString(Order._Order.KIBAN.get());

                //Work
                String sbn = res.getString(Work.Info.SBN.get()); //作番
                String id = res.getString(Work.Info.SGYO_MSINO.get());   //作業明細番号
                String sgkt_id = res.getString(Order._Order.SGYO_KTICD.get());    //作業形態コード
                String sgkt_name = res.getString(Order._Order.SGKT_NM.get());       //作業形態名
                String sg_id = res.getString(Work.Info.SGYOCD.get()); //作業コード
                String sg_name = res.getString(Work.Info.SGYO_NM.get()); //作業名
                String sg_finishflg = res.getString(Work.Info.SGYO_KRFLG.get()); //作業完了フラグ
                String suryo = res.getString(Work.Info.ODR_SU.get());   //受注数量
                String price = res.getString(Work.Info.JKT_GKGK.get());     //純工賃合計原価
                
                //工数
                String hyouzyun= res.getString(Work.Info.HJUN_KOS.get()); //標準工数
                String seikyu = res.getString(Work.Info.INV_KOS.get());  //請求工数
                String siji = res.getString(Work.Info.SIJI_KOS.get()); //指示工数
                String jruikei = res.getString(Work.Info.JRKOS.get());    //実績累計工数
                
                //外注
                String gaichu = res.getString(Work.Info.GTWT_UMFLG.get());   //外注有無
                String gaichu_price = res.getString(Work.Info.GT_GKGK.get());      //外注合計原価
                
                //Date
                String date = res.getString(Order._Order.ODDAY.get());   //受注日
                String last_date = res.getString(Work.Info.LAST_UPD_DAYT.get());

                //DB
                String db = "work_info";
                String company = res.getString(Work.Info.KSYCD.get()); //会社コード

                //車両チェック
                String name = SyaryoTemplate.check(kisy, type, s_type, kiban);
                if (name == null || SyaryoTemplate.errorCheck(date)) {
                    errpw.println(n + "," + SyaryoTemplate.getName(kisy, type, s_type, kiban) + "," + last_date + "," + db + "," + company + "," + date + "," + sbn + "," + id 
                                    + "," + sgkt_id + "," + sgkt_name + "," + sg_id + "," + sg_name + "," + sg_finishflg + "," + price 
                                    + "," + suryo + "," + hyouzyun + "," + seikyu + "," + siji + "," + jruikei + "," + gaichu + "," + gaichu_price);
                    continue;
                }
                
                //車両
                SyaryoTemplate syaryo;
                if(map.get(name) == null)
                    syaryo = syaryoMap.get(name).clone();
                else
                    syaryo = map.get(name);
                
                m++;
                
                //Last
                syaryo.addLast(db, company, last_date);
                
                //Work
                syaryo.addWork(db, company, date, sbn, id, sgkt_id, sgkt_name, sg_id, sg_name, sg_finishflg, suryo, price, gaichu, gaichu_price, hyouzyun, seikyu, siji, jruikei);
                
                //AddSyaryo
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
