/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package creator.source;

import db.HiveDB;
import db.field.Customer;
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
public class SyaryoData {
    //SYARYO DATA
    public Map<String, SyaryoTemplate> addSyaryoCategory(Connection con, PrintWriter errpw, Map<String, SyaryoTemplate> syaryoMap, Map<String, SyaryoTemplate> noneType) {
        Map<String, SyaryoTemplate> map = new TreeMap<>();
        
        try {
            Statement stmt = con.createStatement();

            //Syaryo
            String sql = String.format("select s.%s,s.%s,s.%s, s.%s, s.%s, s.%s, s.%s, s.%s, s.%s, s.%s, s.%s, s.%s, c.%s, c.%s, c.%s from %s s join %s c on (s.%s=c.%s and s.%s=c.%s) ",
                    Syaryo._Syaryo.KISY, Syaryo._Syaryo.TYP, Syaryo._Syaryo.KIBAN, //Unique ID
                    Syaryo._Syaryo.KSYCD,   //会社コード
                    Syaryo._Syaryo.SEHN_BNR_CD_B, //製品分類コード B
                    Syaryo._Syaryo.NU_KBN, //NU区分
                    Syaryo._Syaryo.NNY_YMD, //納入年月日
                    Syaryo._Syaryo.HY_KKYKCD, //保有顧客
                    Syaryo._Syaryo.SYRK_KBN, //車歴
                    Syaryo._Syaryo.SYRK_YMD, //車歴変更日付
                    Syaryo._Syaryo.KOMTRX_SOTY_KBN, //Komtrax
                    Syaryo._Syaryo.LAST_UPD_DAYT,  //最終更新日
                    Customer.Common.KYKNM_1,
                    Customer.Common.GYSD_BNRCD,
                    Customer.Common.GYSCD,
                    HiveDB.TABLE.SYARYO,
                    HiveDB.TABLE.CUSTOMER_COMMON,
                    Syaryo._Syaryo.KSYCD, Customer.Common.KSYCD,
                    Syaryo._Syaryo.HY_KKYKCD, Customer.Common.KKYKCD
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
                if(syaryo != null){
                    syaryo = new SyaryoTemplate(syaryo.getName());
                    if(map.get(syaryo.name) != null) syaryo = (SyaryoTemplate) map.get(syaryo.name);
                }else if((noneType.get(kisy + "-" + kiban) != null)){
                    syaryo = new SyaryoTemplate(syaryoMap.get(noneType.get(kisy + "-" + kiban)).getName());
                    if(map.get(syaryo.name) != null) syaryo = (SyaryoTemplate) map.get(syaryo.name);
                }

                //車両マスタ
                String category = res.getString(Syaryo._Syaryo.SEHN_BNR_CD_B.get());
                String nu = res.getString(Syaryo._Syaryo.NU_KBN.get());

                //Code
                String syareki = res.getString(Syaryo._Syaryo.SYRK_KBN.get());
                String komtrax = res.getString(Syaryo._Syaryo.KOMTRX_SOTY_KBN.get());

                //Customer
                String cid = res.getString(Syaryo._Syaryo.HY_KKYKCD.get());
                String cname = res.getString(Customer.Common.KYKNM_1.get());
                String gyosyu = res.getString(Customer.Common.GYSD_BNRCD.get())+
                                "-"+res.getString(Customer.Common.GYSCD.get());

                //Date
                String nounyu = res.getString(Syaryo._Syaryo.NNY_YMD.get());
                String syareki_date = res.getString(Syaryo._Syaryo.SYRK_YMD.get());
                String last_date = res.getString(Syaryo._Syaryo.LAST_UPD_DAYT.get()).replace("-", "").substring(0,8);

                //DB
                String db = "syaryo";
                String company = res.getString(Syaryo._Syaryo.KSYCD.get());

                //車両チェック
                String name = kisy + "-" + type + "-" + kiban;
                if (syaryo == null) {
                    errpw.println(n + "," + name + "," + last_date + "," + db + "," + company + "," + cid + "," + gyosyu + "," + cname + "," + category
                                    + "," + komtrax + "," + nu + "," + nounyu + "," + syareki_date + "," + syareki);
                    continue;
                }
                
                m++;

                //Spec
                syaryo.addSpec(komtrax, category);
                //NU
                if (nu.equals("N")) {
                    syaryo.addNew(db, company, nounyu, "-1", "-1", "-1");
                    syaryo.addOwner(db, company, nounyu, gyosyu, cid, cname);
                } else if (nu.equals("U")) {
                    syaryo.addUsed(db, company, nounyu, "-1", "-1", "-1");
                    syaryo.addOwner(db, company, nounyu, gyosyu, cid, cname);
                }

                //History
                syaryo.addHistory(db, company, syareki_date, syareki);
                syaryo.addLast(db, company, last_date);

                //AddSyaryo
                map.put(syaryo.getName(), syaryo);

                if (n % 10000 == 0) {
                    System.out.println("Syaryo Processed : " + n);
                }
            }

            System.out.println("Total Processed Syaryo = "+  m + "/" + n);
            System.out.println("Total Update Syaryo = " + map.size());

            return map;
        } catch (SQLException sqlex) {
            sqlex.printStackTrace();
            return null;
        }
    }
}
