/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package export;

import creator.template.SimpleTemplate;
import db.HiveDB;
import file.CSVFileReadWrite;
import file.SyaryoTemplateToJSON;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author ZZ17390
 */
public class DBDataExport {

    //車両リスト
    private static String ALL = "resource\\allsyaryo_index.json";
    private static Map<String, SimpleTemplate> allsyaryo = new SyaryoTemplateToJSON().ToTemplate(ALL);

    public static void main(String[] args) {
        //System.out.println(allsyaryo.values().stream().findFirst().get().validate);
        //サービス経歴
        export("作業工数分析_20190122.csv", "service", "SVCKR_KNRNO", "JSDAY", "", new String[]{"KSYCD", "SVCKR_KNRNO","JSDAY", "KISY", "TYP", "KIBAN", "SGYOCD", "JKOS"});
    }

    public static Map<String, String> export(String file, String db, String id, String idData, String kisy, String[] element) {
        Map sbnmap = new HashMap();
        
        try (PrintWriter pw = CSVFileReadWrite.writer(file)) {
            pw.println("機種,型式,小変形,機番,"+String.join(",", element));

            try (Connection con = HiveDB.getConnection()) {
                Statement stmt = con.createStatement();
                String temp_sql = kisy != ""
                    ? "select " + String.join(",", element)
                    + " from " + db
                    + "where kisy='" + kisy + "'"
                    : "select " + String.join(",", element)
                    + " from " + db;
                System.out.println("Running: " + temp_sql);
                ResultSet res = stmt.executeQuery(temp_sql);

                int n = 0;
                int m = 0;
                while (res.next()) {
                    if (n++ % 10000 == 0) {
                        System.out.println(n + " 件処理");
                    }
                    
                    //Name
                    String _kisy = res.getString("KISY");
                    String kiban = res.getString("KIBAN");

                    //Syaryo Indexに存在するか確認
                    String name = SimpleTemplate.check(_kisy, kiban);

                    //エラーが発生していないデータは除外
                    if (name == null) {
                        //Not Exists Syaryo!
                        continue;
                    }
                    
                    //ID reg
                    String _id = res.getString(id);
                    String _data = res.getString(idData);
                    sbnmap.put(_id, _data);
                    
                    //
                    List<String> data = new ArrayList();
                    SimpleTemplate temp = allsyaryo.get(name);
                    data.add(String.join(",", temp.name));
                    for (String e : element) {
                        data.add(res.getString(e));
                    }

                    pw.println(String.join(",", data));
                    m++;
                    
                }
                
                System.out.println(m+"/"+n);
                
            } catch (SQLException ex) {
            }
        }
        
        return sbnmap;
    }
}
