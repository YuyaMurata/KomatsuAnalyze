/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package errordata;

import static creator.create.TemplateCreate.index;
import creator.template.SimpleTemplate;
import db.HiveDB;
import db.field.EQP;
import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import json.SyaryoTemplateToJson;
import param.KomatsuDataParameter;

/**
 *
 * @author ZZ17390
 */
public class ExportErrorData {
    private static String[] ERR_SOURCE = KomatsuDataParameter.ERR_SOURCE;
    private static String OUTPATH = KomatsuDataParameter.ERR_DATAPROCESS_PATH;
    
    public static void main(String[] args) {
        //Create Folder
        if(!(new File(OUTPATH)).exists())
            (new File(OUTPATH)).mkdir();
        
        //Layout Index
        Map<String, List> index = index();
        System.out.println("Layout:");
        index.entrySet().stream()
                        .filter(e -> Arrays.asList(ERR_SOURCE).contains(e.getKey()))
                        .map(e -> "  " + e.getKey() + ":" + e.getValue())
                        .forEach(System.out::println);
         //File
        SyaryoTemplateToJson json = new SyaryoTemplateToJson();
        Map allsyaryo = syaryoindex(json);
        
        for(String errsource : ERR_SOURCE){
            
        }
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
                + "from EQP_SYARYO e "
                + "join SYARYO s on (e.kisy=s.kisy and e.kiban=s.kiban)";
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

        json.write(file.getAbsolutePath(), index);
        return index;
    }
}
