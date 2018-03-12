/*
 * To change this license header, choose License Headers in Project Propertie
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package creator.source;

import db.HiveDB;
import db.field.Order;
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
import java.util.List;

/**
 *
 * @author ZZ17390
 */
public class OrderData {
    private static List nonUpdateSyaryoList;
    
    //ORDER DATA
    public Map<String, SyaryoTemplate> addOrder(Connection con, PrintWriter errpw, String machine, Map<String, SyaryoTemplate> syaryoMap) {
        Map<String, SyaryoTemplate> map = new TreeMap();
        
        try {
            Statement stmt = con.createStatement();

            //Syaryo
            String sql = String.format("select %s,%s,%s,%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s from %s where kisy like '%s'",
                    Order._Order.KISY, Order._Order.TYP, Order._Order.SYHK, Order._Order.KIBAN, //Unique ID
                    Order._Order.KSYCD, //会社コード
                    Order._Order.SBN, //作番
                    Order._Order.SGYO_KTICD, //作業形態コード
                    Order._Order.SGKT_NM, //作業形態名
                    Order._Order.ODDAY, //受注日
                    Order._Order.ODR_KBN, //受注区分
                    Order._Order.KKYKCD, //顧客コード
                    Order._Order.KYKNM_1, //顧客名
                    Order._Order.SVC_MTR, //サービスメータ
                    Order._Order.SVC_MTR_UPDAY, //サービスメータ更新日
                    Order._Order.GAIYO_1, //概要1
                    Order._Order.GAIYO_2, //概要2
                    Order._Order.SBN_STS, //作番ステータス
                    Order._Order.SGYO_JISI_YTDAY, //作業実施予定日
                    Order._Order.SBN_HKDAY, //作番発行日
                    Order._Order.JRKOS, //実質累計工数
                    Order._Order.SIJI_RKI_KOS, //指示累計工数
                    Order._Order.SKKG, //請求合計金額
                    Order._Order.KNRO_TRFLG, //作業完了フラグ
                    Order._Order.SGYO_KRDAY, //作業完了日
                    Order._Order.LAST_UPD_DAYT, //最終更新日
                    Order._Order.UAGE_KBN_1, //売上区分1
                    Order._Order.UAGE_KBN_2, //売上区分2
                    Order._Order.UAGE_KBN_3, //売上区分3
                    HiveDB.TABLE.ORDER.get(),
                    machine
            );
            System.out.println("Running: " + sql);

            ResultSet res = stmt.executeQuery(sql);

            int n = 0;
            int m = 0;
            while (res.next()) {
                n++;

                //Name
                String kisy = res.getString(Syaryo._Syaryo.KISY.get());
                String type = res.getString(Syaryo._Syaryo.TYP.get());
                String s_type = res.getString(Syaryo._Syaryo.SYHK.get());
                String kiban = res.getString(Syaryo._Syaryo.KIBAN.get());

                //Order
                String id = res.getString(Order._Order.SBN.get());       //作番
                String odr_kbn = res.getString(Order._Order.ODR_KBN.get());   //受注区分
                String sbn_status = res.getString(Order._Order.SBN_STS.get());   //作番ステータス
                String uag1 = res.getString(Order._Order.UAGE_KBN_1.get());     //売上区分1
                String uag2 = res.getString(Order._Order.UAGE_KBN_2.get());     //売上区分2
                String uag3 = res.getString(Order._Order.UAGE_KBN_3.get());     //売上区分3
                String price = res.getString(Order._Order.SKKG.get());     //請求金額
                
                //Work
                String j_kosu = res.getString(Order._Order.JRKOS.get());     //実質累計工数
                String s_kosu = res.getString(Order._Order.SIJI_RKI_KOS.get());  //指示累計工数
                String sg_fin_flg = res.getString(Order._Order.KNRO_TRFLG.get());   //全作業完了フラグ
                String text = res.getString(Order._Order.GAIYO_1.get()) +"-"+res.getString(Order._Order.GAIYO_2.get());   //概要
                String sgkt_id = res.getString(Order._Order.SGYO_KTICD.get());    //作業形態コード
                String sgkt_name = res.getString(Order._Order.SGKT_NM.get());       //作業形態名
                
                //Date
                String date = res.getString(Order._Order.ODDAY.get()); //受注日
                String sbn_date = res.getString(Order._Order.SBN_HKDAY.get());     //作番発行日
                String smr_date = res.getString(Order._Order.SVC_MTR_UPDAY.get()); //サービスメータ更新日
                String sg_date = res.getString(Order._Order.SGYO_JISI_YTDAY.get());   //作業実施予定日
                String sg_fin_date = res.getString(Order._Order.SGYO_KRDAY.get());    //作業完了日
                if(!sg_fin_flg.equals("1"))
                    sg_fin_date = "?";
                String last_date = res.getString(Order._Order.LAST_UPD_DAYT.get()).replace("-", "").substring(0,8);  //最終更新日
                
                //Customer
                String cid = res.getString(Order._Order.KKYKCD.get()); //顧客コード
                String cname = res.getString(Order._Order.KYKNM_1.get()); //顧客名
                
                //DB
                String db = "order";
                String company = res.getString(Order._Order.KSYCD.get());
                
                //SMR
                String smr = res.getString(Order._Order.SVC_MTR.get()); //サービスメータ
                
                //車両チェック
                String name = SyaryoTemplate.check(kisy, type, s_type, kiban);
                if (name == null || SyaryoTemplate.errorCheck(date)) {
                    errpw.println(n + "," + SyaryoTemplate.getName(kisy, type, s_type, kiban) + "," + last_date + "," + db + "," + company + "," + cid + "," + cname 
                                    + "," + date + "," + id + "," + sgkt_id + "," + sgkt_name + "," + sbn_date + "," + sbn_status + "," + odr_kbn + "," + uag1 + "," + uag2 + "," + uag3
                                    + "," + price + "," + sg_date + "," + sg_fin_flg + "," + sg_fin_date + "," + j_kosu + "," + s_kosu
                                    + "," + smr_date + "," + smr + "," + text);
                    continue;
                }
                
                //車両
                SyaryoTemplate syaryo;
                if(map.get(name) == null)
                    syaryo = syaryoMap.get(name).clone();
                else
                    syaryo = map.get(name);
                
                m++;
                
                //History
                syaryo.addHistory(db, company, date, id);
                syaryo.addLast(db, company, last_date);
                
                //Order
                String uag_kbn = uag1+"-"+uag2+"-"+uag3;
                
                /*"DB, 会社コード, 日付, 作番登録日, 実施予定日, 完了日, 作番, 修・単, 作番ステータス, 顧客ID, 顧客名, 保有顧客ID, 保有顧客名, 工数, 指示工数, 売上区分, 請求, 概要 ";*/
                syaryo.addOrder(db, company, date, sbn_date, sg_date, sg_fin_date, id, sgkt_id, sgkt_name, odr_kbn, sbn_status, "?", "?", cid, cname, j_kosu, s_kosu, uag_kbn, price, text);
                
                //SMR
                syaryo.addSMR(db, company, smr_date, smr);
                
                //AddSyaryo
                map.put(syaryo.getName(), syaryo);

                if (n % 10000 == 0) {
                    System.out.println("Syaryo Processed : " + n);
                }
            }

            System.out.println("Total Processed Syaryo = "+  m + "/" + n);
            System.out.println("Total Created SyaryoTemplate = " + map.size() + "/" + syaryoMap.size());
            
            nonUpdateSyaryoList = new ArrayList();
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
