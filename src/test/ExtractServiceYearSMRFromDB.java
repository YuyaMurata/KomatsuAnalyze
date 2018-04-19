/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import db.HiveDB;
import db.field.EQP;
import db.field.Komtrax;
import db.field.Order;
import db.field.Work;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author ZZ17390
 */
public class ExtractServiceYearSMRFromDB {
    public static void main(String[] args) {
        Long start = System.currentTimeMillis();
        
       String syaryoKisyName = "PC138US";
       String syaryoKiban = "2080";
       
       Connection con = HiveDB.getConnection();
       try {
            Statement stmt = con.createStatement();

            //EQP_Syaryo
            String sql = String.format("select %s from eqp_syaryo where kisy='%s' and kiban='%s'",
                EQP.Syaryo.NEW_DELI_DATE,
                syaryoKisyName,
                syaryoKiban
            );
            System.out.println("Running: " + sql);

            ResultSet res = stmt.executeQuery(sql);
            String newDate = "";
            while(res.next()){
                newDate = res.getString(EQP.Syaryo.NEW_DELI_DATE.get());
            }
            
            //EQP_Syaryo
            sql = String.format("select %s, %s from %s where kisy='%s' and kiban='%s'",
                Komtrax.CW_SERVICE_METER.SMR_TIME,
                Komtrax.CW_SERVICE_METER.SMR_VALUE,
                Komtrax.TABLE.CW_SERVICE_METER,
                syaryoKisyName,
                syaryoKiban
            );
            System.out.println("Running: " + sql);

            res = stmt.executeQuery(sql);
            
            TreeMap smrMap = new TreeMap();
            while(res.next()){
                String date = res.getString(Komtrax.CW_SERVICE_METER.SMR_TIME.get()).replace("/", "");
                String smr = res.getString(Komtrax.CW_SERVICE_METER.SMR_VALUE.get());
                smr = String.valueOf(Integer.valueOf(smr) / 60);
                
                smrMap.put(date, smr);
            }
            
            TreeMap yearMap = new TreeMap();
            for(Object date : smrMap.keySet()){
                yearMap.put(date, getAge(newDate, date.toString()));
            }
            
            //EQP_Syaryo
            sql = String.format("select %s, %s, %s, %s, %s, %s, %s from %s where kisy='%s' and kiban='%s'",
                Order._Order.ODDAY,
                Order._Order.SKKG,
                Order._Order.SBN,
                Order._Order.SGYO_KTICD,
                Order._Order.UAGE_KBN_1,
                Order._Order.UAGE_KBN_2,
                Order._Order.UAGE_KBN_3,
                HiveDB.TABLE.ORDER.get(),
                syaryoKisyName,
                syaryoKiban
            );
            System.out.println("Running: " + sql);

            res = stmt.executeQuery(sql);
            
            Map orderMap = new TreeMap();
            while(res.next()){
                String id = res.getString(Order._Order.SBN.get());
                String date = res.getString(Order._Order.ODDAY.get()).replace("/", "");
                String skID = res.getString(Order._Order.SGYO_KTICD.get());
                String kbn1 = res.getString(Order._Order.UAGE_KBN_1.get());
                String kbn2 = res.getString(Order._Order.UAGE_KBN_1.get());
                String kbn3 = res.getString(Order._Order.UAGE_KBN_1.get());
                String price = res.getString(Order._Order.SKKG.get());
                
                List list = new ArrayList();
                list.add(date);list.add(skID);list.add(kbn1);list.add(kbn2);list.add(kbn3);list.add(price);
                orderMap.put(id, list);
            }
            
            //EQP_Syaryo
            sql = String.format("select w.%s, w.%s from %s w join kom_order k on (w.KSYCD=k.KSYCD and w.SBN=k.SBN) where k.kisy='%s' and k.kiban='%s'",
                Work.Info.SBN,
                Work.Info.SGYOCD,
                HiveDB.TABLE.WORK_INFO.get(),
                syaryoKisyName,
                syaryoKiban
            );
            System.out.println("Running: " + sql);

            res = stmt.executeQuery(sql);
            
            while(res.next()){
                String id = res.getString(Work.Info.SBN.get());
                String sgyocd = res.getString(Work.Info.SGYOCD.get());
                
                List list = (List) orderMap.get(id);
                list.add(sgyocd);
                orderMap.put(id, list);
            }
            
       }catch(Exception e){
           e.printStackTrace();
       }
       
       Long stop = System.currentTimeMillis();
       
        System.out.println("time="+(stop-start)+"ms");
    }
    
    public static String getAge(String newdate, String date) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMdd");
            Date birth = sdf.parse(newdate.replace("/", ""));
            Date last = sdf2.parse(date.replace("/", ""));
            Long age = (last.getTime() - birth.getTime()) / (1000 * 60 * 60 * 24);

            //System.out.println(sdf.format(birth) + " - " + sdf.format(last) + " = " + age);
            return age.toString();
        } catch (Exception ex) {
            return "NA";
        }
    }
}
