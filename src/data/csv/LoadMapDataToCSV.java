/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.csv;

import file.CSVFileReadWrite;
import java.io.File;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import obj.SyaryoLoader;
import obj.SyaryoObject;
import param.KomatsuUserParameter;

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
        System.out.println("num of syaryo = " + f.size());

        //CSVData
        List<String> dh = KomatsuUserParameter.DATA_ORDER.stream().filter(d -> d.contains("LOADMAP")).collect(Collectors.toList());
        System.out.println(dh);

        //CSV
        /*f.stream().forEach(s -> {
            s.startHighPerformaceAccess();
            System.out.println(s.name);
            dh.stream().forEach(h -> {
                csv(h, s);
            });
            s.stopHighPerformaceAccess();
        });*/
        dh.stream().forEach(h -> {
            joincsv(h, f);
        });
    }

    private static void joincsv(String key, List<SyaryoObject> slist) {
        Map<String, List<String>> load = slist.get(0).get(key);

        String idx = load.keySet().stream().findFirst().get();
        Boolean td = true;

        //header
        if (!idx.contains("_")) {
            td = false;
        }

        //td = True Matrix
        if (td) {
            List<String> x = load.keySet().stream().map(i -> Integer.valueOf(i.split("_")[0])).distinct().sorted().map(i -> i.toString()).collect(Collectors.toList());
            List<String> y = load.keySet().stream().map(i -> Integer.valueOf(i.split("_")[1])).distinct().sorted().map(i -> i.toString()).collect(Collectors.toList());
            Double[][] matrix = new Double[x.size()][y.size()];

            //初期化
            Arrays.asList(matrix).stream().forEach(col -> Arrays.fill(col, 0d));

            //join
            slist.stream().forEach(s -> {
                s.get(key).entrySet().stream().forEach(e -> {
                    int i = x.indexOf(e.getKey().split("_")[0]);
                    int j = y.indexOf(e.getKey().split("_")[1]);

                    matrix[i][j] += Double.valueOf(e.getValue().get(LOADER.index(key, "VALUE")));
                });
            });

            //出力
            try (PrintWriter pw = CSVFileReadWrite.writer(key + "_join.csv")) {
                pw.println("," + y.stream().map(j -> j).collect(Collectors.joining(",")));
                for (int i = 0; i < x.size(); i++) {
                    pw.println(x.get(i) + "," + Arrays.asList(matrix[i]).stream().map(j -> j.toString()).collect(Collectors.joining(",")));
                }
            }
        }else{
            List<String> x;
            if(load.keySet().stream().findFirst().get().contains("."))
                x = load.keySet().stream().map(i -> Double.valueOf(i)).distinct().sorted().map(i -> i.toString()).collect(Collectors.toList());
            else
                x = load.keySet().stream().distinct().sorted().collect(Collectors.toList());
            
            Double[] matrix = new Double[x.size()];
            Arrays.fill(matrix, 0d);
            
            slist.stream().forEach(s -> {
                s.get(key).entrySet().stream().forEach(e -> {
                    int i = x.indexOf(e.getKey());
                    matrix[i] += Double.valueOf(e.getValue().get(LOADER.index(key, "VALUE")));
                });
            });
            
            //出力
            try (PrintWriter pw = CSVFileReadWrite.writer(key + "_join.csv")) {
                pw.println("x,v");
                for (int i = 0; i < x.size(); i++) {
                    pw.println(x.get(i) + "," + matrix[i].toString());
                }
            }
        }
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
            pw.println("Syaryo," + s.name + ":" + key);
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
                pw.println((td ? String.join(",", id.split("_")) : id) + "," + load.get(id).get(LOADER.index(key, "VALUE")));
            }
        }

    }
}
