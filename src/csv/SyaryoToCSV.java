/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package csv;

import distance.DistanceCulc;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import json.JsonToSyaryoObj;
import obj.SyaryoObject0;

/**
 *
 * @author kaeru
 */
public class SyaryoToCSV {

    public static void main(String[] args) {
        String path = "分析結果\\";
        String kisy = "WA470";
        Map<String, SyaryoObject0> syaryoMap = new JsonToSyaryoObj().reader("syaryo_obj_" + kisy + "_form.json");

        String name = "WA470-7-10180";
        SyaryoObject0 syaryo = syaryoMap.get(name);

        komtrax(syaryo);
    }

    public static void komtrax(SyaryoObject0 syaryo) {
        try {
            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(new File(syaryo.getName() + "_komtrax_data.csv"))));
            
            //SMR
            Map dateMap = new HashMap();
            Double smrBefore = 0d;
            for (String date : syaryo.getSMR().keySet()) {
                if(!syaryo.getSMR().get(date).get(1).toString().contains("komtrax")) continue;
                if(dateMap.get(date.split(" ")[0]) == null) dateMap.put(date.split(" ")[0], date);
                else continue;
                
                Double smr = Double.valueOf(syaryo.getSMR().get(date).get(0).toString()) - smrBefore;
                if(smr < 0)
                    System.out.println(syaryo.getSMR().get(date)+" - "+smrBefore);
                pw.println(date.split(" ")[0] + ",smr," + smr);
                smrBefore = Double.valueOf(syaryo.getSMR().get(date).get(0).toString());
            }

            //Distance
            dateMap = new HashMap();
            DistanceCulc distance = new DistanceCulc();
            String  gps = "";
            for (String date : syaryo.getGPS().keySet()) {
                if(date.contains("#")) continue;
                if(dateMap.get(date.split(" ")[0]) == null) dateMap.put(date.split(" ")[0], date);
                else continue;
                
                if(!gps.equals("")){
                    String gps2 = (String) syaryo.getGPS().get(date).get(0);
                    Double d = distance.onoFormula(distance.compValue(gps.split("_")[0]), 
                                            distance.compValue(gps.split("_")[1]), 
                                            distance.compValue(gps2.split("_")[0]), 
                                            distance.compValue(gps2.split("_")[1]));
                    pw.println(date.split(" ")[0] + ",distance," + d);
                }
                gps = (String) syaryo.getGPS().get(date).get(0);
            }
            
            //Engine
            dateMap = new HashMap();
            for (String date : syaryo.getEngine().keySet()) {
                if(date.contains("#")) continue;
                if(dateMap.get(date.split(" ")[0]) == null) dateMap.put(date.split(" ")[0], date);
                else continue;
                
                pw.println(date.split(" ")[0] + ",engine," + syaryo.getEngine().get(date).get(0));
            }
            
            //Error
            Map<Object, Integer> errMap = new HashMap();
            dateMap = new HashMap();
            for (String date : syaryo.getError().keySet()) {
                if(date.contains("#")) continue;
                if(dateMap.get(date.split(" ")[0]) == null) dateMap.put(date.split(" ")[0], date);
                else continue;
                
                List err = syaryo.getError().get(date);
                if(errMap.get(err.get(0)) == null) errMap.put(err.get(0), 0);
                int cnt = Integer.valueOf(err.get(1).toString()) - errMap.get(err.get(0));
                pw.println(date.split(" ")[0] + ",error," + cnt);
                errMap.put(err.get(0), Integer.valueOf(err.get(1).toString()));
            }
            
            pw.close();
        } catch (IOException ex) {
        }
    }
}
