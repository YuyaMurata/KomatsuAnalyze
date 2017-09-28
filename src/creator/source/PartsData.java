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
import obj.SyaryoTemplate;

/**
 *
 * @author ZZ17390
 */
public class PartsData {

    //PARTS DATA
    public Map<String, SyaryoTemplate> addParts(Connection con, PrintWriter errpw, Map<String, SyaryoTemplate> syaryoMap, Map<String, SyaryoTemplate> noneType) {
        Map map = new TreeMap();

        try {
            Statement stmt = con.createStatement();

            //Syaryo
            String sql = String.format("select o.%s,o.%s,o.%s, p.%s, p.%s, p.%s, p.%s, p.%s, p.%s, p.%s, p.%s, p.%s, p.%s from %s p join %s o on (p.%s=o.%s and p.%s=o.%s)",
                    Order._Order.KISY, Order._Order.TYP, Order._Order.KIBAN, //Unique ID
                    Order.Parts.KSYCD, //会社コード
                    Order.Parts.SBN, //作番
                    Order.Parts.BHN_MSINO, //部品明細番号
                    Order.Parts.MIDAY, //明細登録日
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
            while (res.next()) {
                n++;

                //Name
                String kisy = res.getString(Order._Order.KISY.get());
                String type = res.getString(Order._Order.TYP.get());
                String kiban = res.getString(Order._Order.KIBAN.get());

                //車両
                SyaryoTemplate syaryo = null;
                syaryo = syaryoMap.get(kisy + "-" + type + "-" + kiban);
                if((syaryo == null) && (noneType.get(kisy + "-" + kiban) != null))
                    syaryo = syaryoMap.get(noneType.get(kisy + "-" + kiban));

                //Parts
                String sbn = res.getString(Order.Parts.SBN.get()); //作番
                String mid = res.getString(Order.Parts.BHN_MSINO.get()); //部品明細番号
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
                String name = kisy + "-" + type + "-" + kiban;
                if (syaryo == null) {
                    errpw.println(n + "," + name + "," + last_date + "," + db + "," + company + "," + date + "," + sbn + "," + mid 
                                    + "," + parts_id + "," + parts_name + "," + suryo + "," + cancel + "," + price);
                    continue;
                }

                //Last
                syaryo.addLast(db, company, last_date);

                //Parts
                syaryo.addParts(db, company, date, sbn, mid, parts_id, parts_name, suryo, cancel, price);
                
                //AddSyaryo
                map.put(syaryo.getName(), syaryo);

                if (n % 10000 == 0) {
                    System.out.println("Syaryo Processed : " + n);
                }
            }

            System.out.println("Total Processed Syaryo = " + n);
            System.out.println("Total Update Syaryo = " + map.size());

            return map;
        } catch (SQLException sqlex) {
            sqlex.printStackTrace();
            return null;
        }
    }
}
