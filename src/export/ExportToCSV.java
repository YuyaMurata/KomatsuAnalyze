/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package export;

import file.CSVFileReadWrite;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import obj.LoadSyaryoObject;
import obj.SyaryoLoader;
import obj.SyaryoObject;
import param.KomatsuDataParameter;

/**
 *
 * @author ZZ17390
 */
public class ExportToCSV {

    private static Map<String, SyaryoObject> syaryoMap;
    static String KISY = "PC200";
    static Random rand = new Random();
    private static SyaryoLoader LOADER = SyaryoLoader.getInstance();

    public static void main(String[] args) {
        LOADER.setFile(KISY+"_km_form");
        syaryoMap = LOADER.getSyaryoMap();

        //単一データのみ
        String data = "KOMTRAX_SMR";
        String csv = KISY + "_" + data + "_group_dump.csv";

        //toCSV(csv, data);
        //toCntCSV(csv, data);
        toGroupSMRCntCSV(csv, data, 0);
    }

    public static void toCSV(String name, String data) {
        try (PrintWriter pw = CSVFileReadWrite.writer(name)) {
            syaryoMap.values().forEach(s -> {
                s.startHighPerformaceAccess();

                if (s.get(data) != null) {
                    Map<String, List<String>> dmap = randomPick(s.get(data), 100);
                    dmap.entrySet().stream()
                        .map(d -> s.name + "," + d.getKey() + "," + String.join(",", d.getValue()))
                        .forEach(pw::println);
                }

                s.stopHighPerformaceAccess();
            });
        }
    }

    private static Map<String, List> randomPick(Map<String, List> d, int n) {
        Map<String, List> pick = new TreeMap();

        if (d.size() < n) {
            return d;
        }

        List<String> keys = new ArrayList<>(d.keySet());
        rand.ints(n, 0, keys.size())
            .mapToObj(i -> keys.get(i))
            .forEach(k -> pick.put(k, d.get(k)));
        
        return pick;
    }

    public static void toCntCSV(String name, String data) {
        try (PrintWriter pw = CSVFileReadWrite.writer(name)) {
            syaryoMap.values().forEach(s -> {
                s.startHighPerformaceAccess();

                pw.println(s.name + "," + (s.get(data) != null ? s.get(data).size() : 0));

                s.stopHighPerformaceAccess();
            });
        }
    }
    
    public static void toGroupSMRCntCSV(String name, String data, int gridx) {
        try (PrintWriter pw = CSVFileReadWrite.writer(name)) {
            Map<String, Long> group = new TreeMap();
            syaryoMap.values().forEach(s -> {
                s.startHighPerformaceAccess();
                
                if(s.get(data) != null){
                    s.get(data).entrySet().stream().forEach(d ->{
                        String id;
                        if(gridx < 0)
                            id = d.getKey();
                        else
                            id = d.getValue().get(gridx).toString();
                        
                        if(group.get(id) == null)
                            group.put(id, 0L);
                        
                        group.put(id, group.get(id)+1);
                    });
                }
                s.stopHighPerformaceAccess();
            });
            
            group.entrySet().stream()
                        .map(g -> g.getKey()+","+g.getValue().toString())
                        .forEach(pw::println);
        }
    }
}
