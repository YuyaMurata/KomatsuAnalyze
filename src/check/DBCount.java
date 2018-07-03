/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package check;

import creator.template.SimpleTemplate;
import db.HiveDB;
import static db.HiveDB.getConnection;
import db.field.EQP;
import db.field.Komtrax;
import file.CSVFileReadWrite;
import java.io.File;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import json.SyaryoTemplateToJson;
import param.KomatsuDataParameter;

/**
 *
 * @author ZZ17390
 */
public class DBCount {

    private static String OUTPATH = KomatsuDataParameter.SUMMARY_PATH + "DB\\";

    public static void main(String[] args) {

        if (!(new File(OUTPATH)).exists()) {
            (new File(OUTPATH)).mkdir();
        }

        //ALL
        Map<String, Integer[]> all = allCount();
        
        //Komatsu
        Map index = syaryoindex(new SyaryoTemplateToJson());
        Map<String, Integer[]> komatsu = syaryoCount(index, true);
        
        System.out.println(komatsu);
        
        //Other
        SimpleTemplate.removeValidate();
        index = othersyaryoindex(new SyaryoTemplateToJson());
        Map<String, Integer[]> other = syaryoCount(index, true);
        
        //Error
        index = syaryoindex(new SyaryoTemplateToJson());
        Map<String, Integer[]> error = syaryoCount(index, false);
        
        try(PrintWriter pw = CSVFileReadWrite.writer(OUTPATH+"db_count.csv")){
            //header
            String header = "KIND";
            for(String tb : all.keySet())
                header += ","+tb;
            pw.println(header);
            
            String data = "ALL(DATA)";
            String number = "ALL(NUMBER)";
            String kdata = "KOMATSU(DATA)";
            String knumber = "KOMATSU(NUMBER)";
            String odata = "OTHER(DATA)";
            String onumber = "OTHER(NUMBER)";
            String edata = "ERROR(DATA)";
            String enumber = "ERROR(NUMBER)";
            for(String tb : all.keySet()){
                data    += ","+all.get(tb)[0];
                number  += ","+all.get(tb)[1];
                kdata   += ","+komatsu.get(tb)[0];
                knumber += ","+komatsu.get(tb)[1];
                odata   += ","+other.get(tb)[0];
                onumber += ","+other.get(tb)[1];
                edata   += ","+error.get(tb)[0];
                enumber += ","+error.get(tb)[1];
            }
            pw.println(data);
            pw.println(number);
            pw.println(kdata);
            pw.println(knumber);
            pw.println(odata);
            pw.println(onumber);
            pw.println(edata);
            pw.println(enumber);
        }
        
        //countlist.stream().forEach(System.out::println);
    }

    private static List<String> tableList() {
        List list = new ArrayList();

        for (HiveDB.TABLE table : HiveDB.TABLE.values()) {
            if (table.get().equals("komtrax")) {
                for (Komtrax.TABLE kmtable : Komtrax.TABLE.values()) {
                    list.add(kmtable.get());
                }
            } else {
                list.add(table.get());
            }
        }

        return list;
    }

    private static Map<String, Integer[]> allCount() {

        //DB接続
        Connection con = getConnection(); //HiveDB

        Map cntMap = new HashMap();

        for (String table : tableList()) {
            //System.out.println(table);

            try {
                Statement stmt = con.createStatement();

                String sql = "select count(*) from " + table;

                System.out.println("Running:" + sql);
                //exec SQL
                ResultSet rs = stmt.executeQuery(sql);

                //Header Info
                ResultSetMetaData rsmd = rs.getMetaData();

                Integer[] cnt = new Integer[]{0,0};

                while (rs.next()) {
                    cnt[0] = rs.getInt(rsmd.getColumnName(1));
                }

                cntMap.put(table, cnt);
            } catch (SQLException ex) {
                //System.out.println("Not kisy and type !");
                //ex.printStackTrace();
            }
        }

        return cntMap;
    }

    private static Map<String, Integer[]>  syaryoCount(Map index, Boolean flg) {
        //DB接続
        Connection con = getConnection(); //HiveDB

        Map cntMap = new HashMap();

        for (String table : tableList()) {
            //System.out.println(table);
            
            try {
                Statement stmt = con.createStatement();

                String sql = "select kisy, kiban from " + table;
                if(table.contains("work") || table.contains("parts"))
                    sql = "select o.kisy, o.kiban from "+table+" e join kom_order o on (e.ksycd = o.ksycd and e.sbn=o.sbn)";
                else if(table.contains("customer"))
                    sql = "select o.kisy, o.kiban from "+table+" e join syaryo o on (e.ksycd = o.ksycd and e.kkykcd=o.hy_kkykcd)";
                    
                System.out.println("Running:" + sql);
                //exec SQL
                ResultSet res = stmt.executeQuery(sql);

                Integer[] cnt = new Integer[]{0, 0};

                Map numSyaryo = new HashMap();
                while (res.next()) {
                    //Name
                    String kisy = res.getString(EQP.Syaryo.KISY.get());
                    String kiban = res.getString(EQP.Syaryo.KIBAN.get());
                    
                    String name = SimpleTemplate.check(kisy, kiban);
                    if(flg)
                        if(name == null)
                            continue;
                    else
                        if(name != null)
                            continue;

                    
                    numSyaryo.put(name, true);
                    
                    cnt[0]++;
                }
                
                cnt[1] = numSyaryo.size();

                cntMap.put(table, cnt);
            } catch (SQLException ex) {
                //System.out.println("Not kisy and type !");
                //ex.printStackTrace();
            }
        }

        return cntMap;

    }

    //Set Syaryo Index
    private static Map syaryoindex(SyaryoTemplateToJson json) {
        Map index = new HashMap();
        File file = new File(OUTPATH + "allsyaryo_index.json");
        if (file.exists()) {
            return json.reader(file.getAbsolutePath());
        }

        try (Connection con = HiveDB.getConnection()) {
            Statement stmt = con.createStatement();
            String temp_sql = "select e.kisy, e.typ, e.syhk, e.kiban "
                + "from EQP_SYARYO e ";
            
            System.out.println("Running: " + temp_sql);
            ResultSet res = stmt.executeQuery(temp_sql);

            int n = 0;
            while (res.next()) {
                n++;

                //Name
                String skisy = res.getString(EQP.Syaryo.KISY.get());
                String type = res.getString(EQP.Syaryo.TYP.get());
                String s_type = res.getString(EQP.Syaryo.SYHK.get());
                String kiban = res.getString(EQP.Syaryo.KIBAN.get());

                //SimpleTemplate
                SimpleTemplate temp = new SimpleTemplate(skisy, type, s_type, kiban);
                index.put(temp.getName(), temp);

                if (n % 1000 == 0) {
                    System.out.println(n + " 台処理");
                }
            }
        } catch (SQLException ex) {
        }
        
        System.out.println("Syaryo:"+index.size());
        
        json.write(file.getAbsolutePath(), index);
        return index;
    }

    //Set Other Corp. Syaryo Index
    private static Map othersyaryoindex(SyaryoTemplateToJson json) {
        Map index = new HashMap();
        File file = new File(OUTPATH + "othersyaryo_index.json");
        if (file.exists()) {
            return json.reader(file.getAbsolutePath());
        }

        try (Connection con = HiveDB.getConnection()) {
            Statement stmt = con.createStatement();
            String temp_sql = "select e.kisy, e.typ, e.syhk, e.kiban "
                + "from SYARYO e where MAKR_KBN not rlike '^A.*'";
            System.out.println("Running: " + temp_sql);
            ResultSet res = stmt.executeQuery(temp_sql);

            int n = 0;
            while (res.next()) {
                n++;

                //Name
                String skisy = res.getString(EQP.Syaryo.KISY.get());
                String type = res.getString(EQP.Syaryo.TYP.get());
                String s_type = res.getString(EQP.Syaryo.SYHK.get());
                String kiban = res.getString(EQP.Syaryo.KIBAN.get());

                //SimpleTemplate
                SimpleTemplate temp = new SimpleTemplate(skisy, type, s_type, kiban);
                index.put(temp.getName(), temp);

                if (n % 1000 == 0) {
                    System.out.println(n + " 台処理");
                }
            }
        } catch (SQLException ex) {
        }
        
        System.out.println("Other Syaryo:"+index.size());
        json.write(file.getAbsolutePath(), index);
        return index;
    }
}
