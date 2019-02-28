/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.csv;

import file.CSVFileReadWrite;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import obj.SyaryoLoader;
import obj.SyaryoObject;
import param.KomatsuDataParameter;

/**
 *
 * @author ZZ17390
 */
public class LoadMapDataToCSV {

    private static SyaryoLoader LOADER = SyaryoLoader.getInstance();
    private static String ROOT = "data\\";

    public static void main(String[] args) {
        LOADER.setFile("PC200_form");
        Map<String, SyaryoObject> map = LOADER.getSyaryoMap();

        //Filtering
        List<SyaryoObject> f = map.values().stream().filter(s -> s.get("LOADMAP_SMR") != null).collect(Collectors.toList());
        System.out.println("num of syaryo = "+f.size());
        
        //CSVData
        List<String> dh = KomatsuDataParameter.DATA_ORDER.stream().filter(d -> d.contains("LOADMAP")).collect(Collectors.toList());
        System.out.println(dh);

        //CSV
        f.stream().forEach(s -> {
            s.startHighPerformaceAccess();
            System.out.println(s.name);
            dh.stream().forEach(h -> {
                csv(h, s);
            });
            s.stopHighPerformaceAccess();
        });
    }

    private static void csv(String key, SyaryoObject s) {
        File path = new File(ROOT + s.name + "\\loadmap\\");
        Map<String, List<String>> load = s.get(key);

        if (!path.exists()) {
            path.mkdirs();
        }
        
        try (PrintWriter pw = CSVFileReadWrite.writer(path.getAbsolutePath() + "\\" + key + ".csv")) {
            String idx = load.keySet().stream().findFirst().get();
            
            //GraphTitle
            pw.println("Syaryo,"+s.name+":"+key);
            Boolean td = true;
            
            //header
            if (idx.contains("_")) {
                pw.println("x,y,v");
            } else {
                pw.println("x,v");
                td = false;
            }

            //data
            for (String id : load.keySet()) {
                pw.println((td?String.join(",", id.split("_")):id) + "," + load.get(id).get(LOADER.index(key, "VALUE")));
            }
        }

    }
}
