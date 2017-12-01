/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package creator.source;

import db.HiveDB;
import db.field.Sell;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.TreeMap;
import creator.template.SyaryoTemplate;

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
                    Sell._Sell.CO_URI_ACT_KBN + "=2",
                    Sell._Sell.MKM_ACT_KBN + "=2",
                    Sell._Sell.SP_MTO_SK_KBN + "=1",
                    Sell._Sell.CO_URI_LA_FLG + "=1"
            );
            System.out.println("Running: " + sql);

            ResultSet res = stmt.executeQuery(sql);

            int n = 0;
            int m = 0;
            while (res.next()) {
                n++;

                //Name
                String kisy = res.getString(Sell._Sell.KISY.get());
                String type = res.getString(Sell._Sell.TYP.get());
                String kiban = res.getString(Sell._Sell.KIBAN.get());

                //車両
                SyaryoTemplate syaryo = syaryoMap.get(kisy + "-" + type + "-" + kiban);
                if (syaryo != null) {
                    syaryo = new SyaryoTemplate(syaryo.getName());
                    if (map.get(syaryo.name) != null) {
                        syaryo = (SyaryoTemplate) map.get(syaryo.name);
                    }
                } else if ((noneType.get(kisy + "-" + kiban) != null)) {
                    syaryo = new SyaryoTemplate(syaryoMap.get(noneType.get(kisy + "-" + kiban)).getName());
                    if (map.get(syaryo.name) != null) {
                        syaryo = (SyaryoTemplate) map.get(syaryo.name);
                    }
                }

                //Sell
                String nu_kbn = res.getString(Sell._Sell.NU_KBN.get());  //NUE
                String price1 = res.getString(Sell._Sell.HM_URI_KN.get());   //表面価
                String price2 = res.getString(Sell._Sell.RL_URI_KN.get());   //実質価
                String price3 = res.getString(Sell._Sell.STD_SY_KKU.get());  //標準価

                //Customer
                String cid = res.getString(Sell._Sell.NNSCD.get());   //納入先コード
                String cname = res.getString(Sell._Sell.NNSK_NM_1.get());   //納入先名
                String gyosyu = "?-" + res.getString(Sell._Sell.NOU_GYSCD.get());   //納入先業種

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

                m++;

                //Last
                syaryo.addLast(db, company, last_date);

                //Sell
                if (nu_kbn.equals("N")) {
                    syaryo.addNew(db, company, date, price3, price1, price2);
                    syaryo.addOwner(db, company, date, gyosyu, cid, cname);
                } else if (nu_kbn.equals("U")) {
                    syaryo.addUsed(db, company, date, price3, price1, price2);
                    syaryo.addOwner(db, company, date, gyosyu, cid, cname);
                } else {
                    continue;
                }

                //AddSyaryo
                map.put(syaryo.getName(), syaryo);

                if (n % 10000 == 0) {
                    System.out.println("Syaryo Processed : " + n);
                }
            }

            System.out.println("Total Processed Syaryo = " + m + "/" + n);
            System.out.println("Total Update Syaryo = " + map.size());

            return map;
        } catch (SQLException sqlex) {
            sqlex.printStackTrace();
            return null;
        }
    }

    //SELL DATA
    public Map<String, SyaryoTemplate> addOld(Connection con, PrintWriter errpw, Map<String, SyaryoTemplate> syaryoMap, Map<String, SyaryoTemplate> noneType) {
        Map map = new TreeMap();

        try {
            Statement stmt = con.createStatement();

            //Syaryo
            String sql = String.format("select %s,%s,%s, %s, %s, %s from %s",
                    Sell.Old.KISY, Sell.Old.TYP, Sell.Old.KIBAN, //Unique ID
                    Sell.Old.KSYCD, //会社コード
                    Sell.Old.URI_DAY, //売上年月
                    Sell.Old.URI_KNGK, //実質価
                    HiveDB.TABLE.SELL_OLD
            );
            System.out.println("Running: " + sql);

            ResultSet res = stmt.executeQuery(sql);

            int n = 0;
            int m = 0;
            while (res.next()) {
                n++;

                //Name
                String kisy = res.getString(Sell.Old.KISY.get());
                String type = res.getString(Sell.Old.TYP.get());
                String kiban = res.getString(Sell.Old.KIBAN.get());

                //車両
                SyaryoTemplate syaryo = syaryoMap.get(kisy + "-" + type + "-" + kiban);
                if (syaryo != null) {
                    syaryo = new SyaryoTemplate(syaryo.getName());
                    if (map.get(syaryo.name) != null) {
                        syaryo = (SyaryoTemplate) map.get(syaryo.name);
                    }
                } else if ((noneType.get(kisy + "-" + kiban) != null)) {
                    syaryo = new SyaryoTemplate(syaryoMap.get(noneType.get(kisy + "-" + kiban)).getName());
                    if (map.get(syaryo.name) != null) {
                        syaryo = (SyaryoTemplate) map.get(syaryo.name);
                    }
                }

                //Sell
                String price2 = res.getString(Sell.Old.URI_KNGK.get());   //実質価

                //Date
                String date = res.getString(Sell.Old.URI_DAY.get()); //売上年月

                //DB
                String db = "sell_old";
                String company = res.getString(Sell.Old.KSYCD.get());   //会社コード
                if(company != null)
                    company = company.substring(0,2);
                else
                    company = "?";
                
                //車両チェック
                String name = kisy + "-" + type + "-" + kiban;
                if (syaryo == null) {
                    errpw.println(n + "," + name + "," + date + "," + db + "," + company + ","
                            + "," + date + "," + price2);
                    continue;
                }

                m++;

                //Last
                syaryo.addLast(db, company, date);

                //Sell
                syaryo.addNew(db, company, date, "-1", "-1", price2);

                //AddSyaryo
                map.put(syaryo.getName(), syaryo);

                if (n % 10000 == 0) {
                    System.out.println("Syaryo Processed : " + n);
                }
            }

            System.out.println("Total Processed Syaryo = " + m + "/" + n);
            System.out.println("Total Update Syaryo = " + map.size());

            return map;
        } catch (SQLException sqlex) {
            sqlex.printStackTrace();
            return null;
        }
    }

    //SELL_USED DATA
    public Map<String, SyaryoTemplate> addUsed(Connection con, PrintWriter errpw, Map<String, SyaryoTemplate> syaryoMap, Map<String, SyaryoTemplate> noneType) {
        Map map = new TreeMap();

        try {
            Statement stmt = con.createStatement();

            //Syaryo
            String sql = String.format("select %s,%s,%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s from %s",
                    Sell.Used.KISY, Sell.Used.TYPE, Sell.Used.KIBAN, //Unique ID
                    Sell.Used.URI_DAY, //売上日
                    Sell.Used.CO_CODE, //受注コード
                    Sell.Used.CO_CUST, //受注先名
                    Sell.Used.NO_CUST, //納入先名
                    Sell.Used.ST_KKU, //査定価格
                    Sell.Used.CO_KKU, //受注価格
                    Sell.Used.HAN_CH_SUM, //販直費
                    Sell.Used.ST_ANS_DAY, //査定回答日
                    Sell.Used.DEN_HAK_DAY, //伝票発行日
                    Sell.Used.PO_HR_MTR, //SMR
                    Sell.Used.PO_TO, //仕入先
                    Sell.Used.PO_SUM_DAY, //仕入計上日
                    Sell.Used.NO_CTRY, //納品先国名
                    HiveDB.TABLE.SELL_USED
            );
            System.out.println("Running: " + sql);

            ResultSet res = stmt.executeQuery(sql);

            int n = 0;
            int m = 0;
            while (res.next()) {
                n++;

                //Name
                String kisy = res.getString(Sell.Used.KISY.get());
                String type = res.getString(Sell.Used.TYPE.get());
                String kiban = res.getString(Sell.Used.KIBAN.get());

                //車両
                SyaryoTemplate syaryo = syaryoMap.get(kisy + "-" + type + "-" + kiban);
                if (syaryo != null) {
                    syaryo = new SyaryoTemplate(syaryo.getName());
                    if (map.get(syaryo.name) != null) {
                        syaryo = (SyaryoTemplate) map.get(syaryo.name);
                    }
                } else if ((noneType.get(kisy + "-" + kiban) != null)) {
                    syaryo = new SyaryoTemplate(syaryoMap.get(noneType.get(kisy + "-" + kiban)).getName());
                    if (map.get(syaryo.name) != null) {
                        syaryo = (SyaryoTemplate) map.get(syaryo.name);
                    }
                }

                //Sell
                String satei_price = res.getString(Sell.Used.ST_KKU.get()); //査定価格
                String price = res.getString(Sell.Used.CO_KKU.get()); //受注価格
                String cost = res.getString(Sell.Used.HAN_CH_SUM.get()); //販直費
                if (satei_price.contains("_")) {
                    satei_price = satei_price.replace("_", "");
                }
                if(satei_price.equals("")) satei_price = "-1";
                if (price.contains("_")) {
                    price = price.replace("_", "");
                }
                if (cost.contains("_")) {
                    cost = cost.replace("_", "");
                }

                //Customer
                String jid = res.getString(Sell.Used.CO_CODE.get()); //受注コード
                String jname = res.getString(Sell.Used.CO_CUST.get()); //受注先名
                String nctry_name = res.getString(Sell.Used.NO_CTRY.get()); //納品先国名
                String nname = res.getString(Sell.Used.NO_CUST.get()); //納入先名
                String sname = res.getString(Sell.Used.PO_TO.get()); //仕入先名

                //Date
                String reg_date = res.getString(Sell.Used.DEN_HAK_DAY.get()).replace("/", ""); //伝票発行日
                String date = res.getString(Sell.Used.URI_DAY.get()).replace("/", ""); //売上日
                String satei_date = res.getString(Sell.Used.ST_ANS_DAY.get()).replace("/", ""); //査定回答日
                String shire_date = res.getString(Sell.Used.PO_SUM_DAY.get()); //仕入計上日
                try { //そのうち修正
                    shire_date = shire_date.replace("/", "");
                } catch (Exception e) {
                    shire_date = "-1";
                }

                //SMR
                String smr = res.getString(Sell.Used.PO_HR_MTR.get()); //SMR

                //DB
                String db = "sell_used";
                String company = "?";   //会社コード

                //車両チェック
                String name = kisy + "-" + type + "-" + kiban;
                if (syaryo == null) {
                    errpw.println(n + "," + name + "," + reg_date + "," + db + "," + company + "," + "?-?-" + nctry_name + "," + "-1" + "," + nname
                            + "," + jid + "," + jname + "," + shire_date + "," + sname + "," + satei_date + "," + satei_price + "," + date + "," + price + "," + cost
                            + "," + smr);
                    continue;
                }

                m++;

                //Used
                syaryo.addUsed(db, company, date, price, satei_price, String.valueOf(Integer.valueOf(price) - Integer.valueOf(cost)));

                //Customer
                syaryo.addOwner(db, company, date, "?-?" + nctry_name, "-1", nname);
                syaryo.addOwner(db, company, shire_date, "?-?", "-1", sname);

                //SMR
                syaryo.addSMR(db, company, shire_date, smr);

                //Last
                syaryo.addLast(db, company, reg_date);

                //AddSyaryo
                map.put(syaryo.getName(), syaryo);

                if (n % 10000 == 0) {
                    System.out.println("Syaryo Processed : " + n);
                }
            }

            System.out.println("Total Processed Syaryo = " + m + "/" + n);
            System.out.println("Total Update Syaryo = " + map.size());

            return map;
        } catch (SQLException sqlex) {
            sqlex.printStackTrace();
            return null;
        }
    }
}
