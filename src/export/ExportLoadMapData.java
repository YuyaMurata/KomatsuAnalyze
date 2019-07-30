/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package export;

import data.cluster.WEKAClustering;
import file.CSVFileReadWrite;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import obj.SyaryoLoader;

/**
 *
 * @author ZZ17807
 */
public class ExportLoadMapData {

    static SyaryoLoader LOADER = SyaryoLoader.getInstance();

    public static void main(String[] args) {
        LOADER.setFile("PC200_loadmap");

        Map<String, List<Double>> data = new LinkedHashMap<>();
        Map<String, String> dataCsv = new HashMap<>();
        LOADER.getSyaryoMap().values().stream()
                .filter(s -> s.get("LOADMAP_作業モード選択状況") != null)
                .forEach(s -> {
                    Map<String, List<String>> mode = s.get("LOADMAP_作業モード選択状況");
                    Double htotal = Double.valueOf(s.get("LOADMAP_SMR").get("SMR").get(0));
                    List<Double> d = mode.values().stream().map(m -> Double.valueOf(m.get(0)) / htotal).collect(Collectors.toList());
                    data.put(s.name, d);
                    dataCsv.put(s.name,
                            htotal.toString() + ","
                            + mode.values().stream()
                                    .map(m -> m.get(0)).collect(Collectors.joining(",")) + ","
                            + d.stream().map(v -> v.toString()).collect(Collectors.joining(","))
                    );
                });

        List<String> header = Arrays.asList(new String[]{"P", "E", "L", "B"});
        createARFF("PC200_loadmap_mode.arff", "LOADMAP_作業モード選択状況", header, data);

        //clustering
        WEKAClustering weka = new WEKAClustering();
        weka.set("PC200_loadmap_mode.arff", 3, new ArrayList(LOADER.getSyaryoMap().keySet()));
        Map<String, Integer> result = weka.clustering();

        try (PrintWriter pw = CSVFileReadWrite.writerSJIS("PC200_loadmap_mode.csv")) {
            //header
            pw.println("SID,SMR,"+String.join(",", header)+","+header.stream().map(h -> h+"/T").collect(Collectors.joining(","))+",CID");
            
            result.entrySet().stream().forEach(r -> {
                pw.println(r.getKey() + "," +dataCsv.get(r.getKey())+","+r.getValue());
            });
        }
    }

    public static void createARFF(String file, String key, List<String> hl, Map<String, List<Double>> dl) {
        Map<String, List<Double>> data = dl;
        List<String> header = hl;

        try (PrintWriter pw = CSVFileReadWrite.writerSJIS(file)) {
            pw.println("@RELATION " + key + "\n");
            header.stream().map(h -> "@ATTRIBUTE " + h + " REAL").forEach(pw::println);
            pw.println("\n@DATA");
            data.values().stream()
                    .map(d -> d.stream().map(di -> di.toString()).collect(Collectors.joining(",")))
                    .forEach(pw::println);
        }
    }
}
