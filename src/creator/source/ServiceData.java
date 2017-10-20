/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package creator.source;

import db.HiveDB;
import db.field.Service;
import db.field.Syaryo;
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
public class ServiceData {

    //SERVICE DATA
    public Map<String, SyaryoTemplate> addService(Connection con, PrintWriter errpw, Map<String, SyaryoTemplate> syaryoMap, Map<String, SyaryoTemplate> noneType, String sp1, String sp2) {
        Map map = new TreeMap();

        try {
            Statement stmt = con.createStatement();

            //Syaryo
            String sql = String.format("select %s,%s,%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s from %s where %s and %s",
                    Service._Service.KISY, Service._Service.TYP, Service._Service.KIBAN, //Unique ID
                    Service._Service.KSYCD, //会社コード
                    Service._Service.JSDAY, //実施日
                    Service._Service.SVCKR_KNRNO, //作番
                    Service._Service.ODR_KBN, //受注区分
                    Service._Service.HY_KKYKCD, //顧客コード
                    Service._Service.KYKNM, //顧客名
                    Service._Service.SGYO_MSINO, //作業明細番号
                    Service._Service.UPD_RRK_MNO, //追番
                    Service._Service.UAGE_KBN_3, //売上区分3
                    Service._Service.SGYO_KTICD, //作業形態コード
                    Service._Service.SGYOCD, //作業コード
                    Service._Service.SGYO_NM, //作業名
                    Service._Service.GAIYO, //概要
                    Service._Service.KISY_CMT, //機種コメント
                    Service._Service.JKOS, //工数
                    Service._Service.JISI_SU, //数量
                    Service._Service.HNBN, //品番
                    Service._Service.BHN_NM, //部品名称
                    Service._Service.SKKG, //請求金額
                    Service._Service.SVC_MTR, //サービスメータ
                    Service._Service.LAST_UPD_DAYT,
                    HiveDB.TABLE.SERVICE,
                    Service._Service.HASSEI_KBN + sp1,
                    Service._Service.ODR_KBN + sp2
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
                String kiban = res.getString(Syaryo._Syaryo.KIBAN.get());

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

                //Service
                String id = res.getString(Service._Service.SVCKR_KNRNO.get());   //作番
                String text = res.getString(Service._Service.GAIYO.get());     //概要

                //Order
                String odr_kbn = res.getString(Service._Service.ODR_KBN.get());       //受注区分
                String uag_kbn = res.getString(Service._Service.UAGE_KBN_3.get());    //売上区分3
                String price = res.getString(Service._Service.SKKG.get());
                String price1 = "0", price2 = "0", price3 = "0";
                if (uag_kbn.equals("1") || uag_kbn.equals("3")) {
                    price1 = price;      //請求金額
                } else if (uag_kbn.equals("2")) {
                    price2 = price;
                } else if (!uag_kbn.equals("")) {
                    price3 = price;
                } else {
                    price1 = "*" + price;
                }

                //Work
                String sg_mid = res.getString(Service._Service.SGYO_MSINO.get());    //作業明細番号
                String sg_add_id = res.getString(Service._Service.UPD_RRK_MNO.get());    //追番
                String sg_keitai_id = res.getString(Service._Service.SGYO_KTICD.get());    //作業形態コード
                String sg_id = res.getString(Service._Service.SGYOCD.get());        //作業コード
                String sg_name = res.getString(Service._Service.SGYO_NM.get());       //作業名
                String kosu = res.getString(Service._Service.JKOS.get());      //工数
                String suryo = res.getString(Service._Service.JISI_SU.get());   //数量

                //Parts
                String parts_id = res.getString(Service._Service.HNBN.get());      //品番
                String parts_name = res.getString(Service._Service.BHN_NM.get());    //部品名称

                //Date
                String date = res.getString(Service._Service.JSDAY.get());     //実施日
                String last_date = res.getString(Service._Service.LAST_UPD_DAYT.get());

                //Customer
                String cid = res.getString(Service._Service.HY_KKYKCD.get());     //顧客コード
                String cname = res.getString(Service._Service.KYKNM.get());
                String gyosyu = "?-?";

                //SMR
                String smr = res.getString(Service._Service.SVC_MTR.get()); //サービスメータ

                //DB
                String db = "service";
                String company = res.getString(Service._Service.KSYCD.get());   //会社コード

                //車両チェック
                String name = kisy + "-" + type + "-" + kiban;
                String comment = res.getString(Service._Service.KISY_CMT.get());
                if (syaryo == null) {
                    errpw.println(n + "," + name + "," + last_date + "," + db + "," + company + "," + cid + "," + gyosyu + "," + cname
                            + "," + date + "," + id + "," + odr_kbn + "," + uag_kbn + "," + "," + price + "," + sg_mid + "," + sg_add_id
                            + "," + sg_keitai_id + "," + sg_id + "," + sg_name + "," + kosu + "," + suryo + "," + parts_id + "," + parts_name
                            + "," + smr + "," + text + "," + comment);
                    continue;
                }

                m++;

                //Order
                //History
                syaryo.addHistory(db, company, date, id);
                syaryo.addOrder(db, company, "-1", "-1", "-1", date, id, odr_kbn, "-1", "?", "?", "?", "?", cid, cname, "?", "-1", "-1", kosu, "-1", price1, price2, price3, text);

                //Parts
                if (odr_kbn.equals("1")) {
                    syaryo.addParts(db, company, date, id, sg_mid, parts_id, parts_name, suryo, "-1", price);
                } else if (odr_kbn.equals("2")) {
                    syaryo.addWork(db, company, date, id, sg_mid, sg_keitai_id, "?", sg_id, sg_name, "-1", suryo, price, "-1", "-1", "-1", "-1", "-1", kosu);
                }

                //Owner
                syaryo.addOwner(db, company, date, gyosyu, cid, cname);

                //SMR
                syaryo.addSMR(db, company, date, smr);

                //Last
                syaryo.addLast(db, company, last_date);

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
