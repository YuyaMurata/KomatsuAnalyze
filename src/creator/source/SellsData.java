/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package creator.source;

import db.HiveDB;
import db.field.Order;
import db.field.Sell;
import db.field.Syaryo;
import db.field.Work;
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
public class SellsData {

    //SELL DATA
    public Map<String, SyaryoTemplate> addSell(Connection con, PrintWriter errpw, Map<String, SyaryoTemplate> syaryoMap, Map<String, SyaryoTemplate> noneType) {
        Map map = new TreeMap();

        try {
            Statement stmt = con.createStatement();

            //Syaryo
            String sql = String.format("select %s,%s,%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s from %s where %s and %s and %s and %s",
                    Sell._Sell.KISY, Sell._Sell.TYP, Sell._Sell.KIBAN, //Unique ID
                    Sell._Sell.KSYCD, //会社コード
                    Sell._Sell.NOU_YTI_DAY, //納入年月
                    Sell._Sell.NU_KBN, //NU
                    Sell._Sell.NNSCD, //納入先コード
                    Sell._Sell.NNSK_NM_1, //納入先名
                    Sell._Sell.NOU_GYSCD, //納入先業種
                    Sell._Sell.HM_URI_KN, //表面価
                    Sell._Sell.RL_URI_KN, //実質価
                    Sell._Sell.STD_SY_KKU, //標準価
                    Sell._Sell.LAST_UPD_DAYT,
                    HiveDB.TABLE.SELL,
                    Sell._Sell.CO_URI_SEQ + "=2",
                    Sell._Sell.MKM_ACT_KBN + "=2",
                    Sell._Sell.SP_MTO_SK_KBN + "=1",
                    Sell._Sell.CO_URI_LA_FLG + "=1"
            );
            System.out.println("Running: " + sql);

            ResultSet res = stmt.executeQuery(sql);

            int n = 0;
            while (res.next()) {
                n++;

                //Name
                String kisy = res.getString(Sell._Sell.KISY.get());
                String type = res.getString(Sell._Sell.TYP.get());
                String kiban = res.getString(Sell._Sell.KIBAN.get());

                //車両
                SyaryoTemplate syaryo = null;
                if (type.equals("")) {
                    syaryo = syaryoMap.get(noneType.get(kisy + "-" + kiban));
                } else {
                    syaryo = syaryoMap.get(kisy + "-" + type + "-" + kiban);
                }

                //Sell
                String nu_kbn = res.getString(Sell._Sell.NU_KBN.get());  //NUE
                String price1 = res.getString(Sell._Sell.HM_URI_KN.get());   //表面価
                String price2 = res.getString(Sell._Sell.RL_URI_KN.get());   //実質価
                String price3 = res.getString(Sell._Sell.STD_SY_KKU.get());  //標準価
                
                //Customer
                String cid = res.getString(Sell._Sell.NNSCD.get());   //納入先コード
                String cname = res.getString(Sell._Sell.NNSK_NM_1.get());   //納入先名
                String gyosyu = "?-"+res.getString(Sell._Sell.NOU_GYSCD.get());   //納入先業種
               
                //Date
                String date = res.getString(Sell._Sell.NOU_YTI_DAY.get()); //納入年月
                String last_date = res.getString(Sell._Sell.LAST_UPD_DAYT.get());

                //DB
                String db = "sell";
                String company = res.getString(Sell._Sell.KSYCD.get());   //会社コード
                
                //車両チェック
                String name = kisy + "-" + type + "-" + kiban;
                if (syaryo == null) {
                    errpw.println(n + "," + name + "," + last_date + "," + db + "," + company + "," + gyosyu + "," + cid + "," + cname 
                                    + "," + date + "," + nu_kbn + "," + price1 + "," + price2 + "," + price3);
                    continue;
                }

                //Last
                syaryo.addLast(db, company, last_date);

                //Sell
                if(nu_kbn.equals("N")){
                    syaryo.addNew(db, company, date, price3, price1, price2);
                    syaryo.addOwner(db, company, date, gyosyu, cid, cname);
                }else if(nu_kbn.equals("U")){
                    syaryo.addUsed(db, company, date, price3, price1, price2);
                    syaryo.addOwner(db, company, date, gyosyu, cid, cname);
                }else
                    continue;
                
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
