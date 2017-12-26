/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package creator.source;

import db.HiveDB;
import db.field.Order;
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
public class PartsData {
    private static List nonUpdateSyaryoList = new ArrayList();
    
    //PARTS DATA
    public Map<String, SyaryoTemplate> addParts(Connection con, PrintWriter errpw, Map<String, SyaryoTemplate> syaryoMap) {
        Map map = new TreeMap();

        try {
            Statement stmt = con.createStatement();

            //Syaryo
            String sql = String.format("select o.%s,o.%s,o.%s,o.%s, p.%s, p.%s, p.%s, p.%s, p.%s, p.%s, p.%s, p.%s, p.%s, p.%s, p.%s from %s p join %s o on (p.%s=o.%s and p.%s=o.%s)",
                    Order._Order.KISY, Order._Order.TYP, Order._Order.SYHK, Order._Order.KIBAN, //Unique ID
                    Order.Parts.KSYCD, //会社コード
                    Order.Parts.SBN, //作番
                    Order.Parts.BHN_MSINO, //部品明細番号
                    Order.Parts.MIDAY, //明細登録日
                    Order.Parts.BHN_MAKR_KBN, //部品メーカ
                    Order.Parts.HNBN, //品番
                    Order.Parts.BHN_NM, //品名
                    Order.Parts.ODR_SU, //受注数量
                    Order.Parts.CANSL_SU, //キャンセル数量
                    Order.Parts.HJUN_KKU, //標準価格
                    Order.Parts.LAST_UPD_DAYT,
                    HiveDB.TABLE.PARTS,
                    HiveDB.TABLE.ORDER.get(),
                    Order.Parts.KSYCD, Order._Order.KSYCD,
                    Order.Parts.SBN, Order._Order.SBN
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

                //Parts
                String sbn = res.getString(Order.Parts.SBN.get()); //作番
                String mid = res.getString(Order.Parts.BHN_MSINO.get()); //部品明細番号
                String maker = res.getString(Order.Parts.BHN_MAKR_KBN.get()); //部品メーカー
                String parts_id = res.getString(Order.Parts.HNBN.get()); //品番
                String parts_name = res.getString(Order.Parts.BHN_NM.get()); //品名
                String suryo = res.getString(Order.Parts.ODR_SU.get()); //受注数量
                String cancel = res.getString(Order.Parts.CANSL_SU.get()); //キャンセル数量
                String price = res.getString(Order.Parts.HJUN_KKU.get()); //標準価格

                //Date
                String date = res.getString(Order.Parts.MIDAY.get());  //明細登録日
                String last_date = res.getString(Order.Parts.LAST_UPD_DAYT.get());

                //DB
                String db = "parts";
                String company = res.getString(Order.Parts.KSYCD.get()); //会社コード

                //車両チェック
                String name = SyaryoTemplate.check(kisy, type, s_type, kiban);
                if (name == null || SyaryoTemplate.errorCheck(date)) {
                    errpw.println(n + "," + SyaryoTemplate.getName(kisy, type, s_type, kiban) + "," + last_date + "," + db + "," + company + "," + date + "," + sbn + "," + mid 
                                    + "," + maker + "," + parts_id + "," + parts_name + "," + suryo + "," + cancel + "," + price);
                    continue;
                }
                
                //車両
                SyaryoTemplate syaryo = syaryoMap.get(name);
                
                m++;

                //Last
                syaryo.addLast(db, company, last_date);

                //Parts
                syaryo.addParts(db, company, date, sbn, mid, maker, parts_id, parts_name, suryo, cancel, price);
                
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
