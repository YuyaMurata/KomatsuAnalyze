/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package export;

import distance.DistanceCulc;
import static export.AccidentData.extractAccident;
import file.CSVFileReadWrite;
import java.io.PrintWriter;
import java.util.Map;
import java.util.TreeMap;
import json.SyaryoToZip3;
import obj.SyaryoObject4;

/**
 *
 * @author 産総研・東工大OIL_2-2
 */
public class GPSDataAcc {

    private static String kisy = "PC138US";

    public static void main(String[] args) {
        String filename = "syaryo\\syaryo_obj_" + kisy + "_form.bz2";
        Map<String, SyaryoObject4> syaryoMap = new SyaryoToZip3().read(filename);

        String outputname = "gps_" + kisy + ".csv";
        try (PrintWriter csv = CSVFileReadWrite.writer(outputname)) {
            extractZENRIN(syaryoMap, csv);
        }
    }
    
    private static void extractZENRIN(Map<String, SyaryoObject4> syaryoMap, PrintWriter csv){
        csv.println("id,lon,lat,label");
        String id = "100";
        
        SyaryoObject4 syaryo = syaryoMap.get("PC138US-8-22337");

        Map<String, String> gps = new TreeMap<>();
        for(String date : syaryo.get("KOMTRAX_GPS").keySet()){
            String lat = DistanceCulc.compValue(syaryo.get("KOMTRAX_GPS").get(date).get(0).toString()).toString();
            String lon = DistanceCulc.compValue(syaryo.get("KOMTRAX_GPS").get(date).get(1).toString()).toString();
            gps.put(date, lon+","+lat);
        }
        
        Map<String, String> label = new TreeMap<>();
        for(String key : syaryo.get("受注").keySet()){
            label.put(syaryo.get("受注").get(key).get(4).toString(), syaryo.get("受注").get(key).get(10).toString());
        }
        
        for(String date : syaryo.get("KOMTRAX_SMR").keySet()){
            if(label.get(date) == null)
                label.put(date, syaryo.get("KOMTRAX_SMR").get(date).get(0).toString());
            else
                label.put(date, label.get(date)+"_"+syaryo.get("KOMTRAX_SMR").get(date).get(0).toString());
        }
        
        //CSV
        for(String date : gps.keySet()){
            if(label.get(date) != null)
                csv.println(id+","+gps.get(date)+","+date+"_"+label.get(date));
            else
                csv.println(id+","+gps.get(date)+",");
        }
    }
}
