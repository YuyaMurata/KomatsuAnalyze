/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package export;

import file.CSVFileReadWrite;
import file.ListToCSV;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import obj.SyaryoLoader;

/**
 *
 * @author ZZ17807
 */
public class ClusterLoadMapList {

    public static void main(String[] args) {
        Map<String, String> cluster = ListToCSV.toMap("PC200_eng_評価結果.csv", 0, 2);
        SyaryoLoader LOADER = SyaryoLoader.getInstance();
        LOADER.setFile("PC200_loadmap");

        //String lkey = "LOADMAP_エンジン水温VS作動油温";
        String lkey = "LOADMAP_実エンジン回転VSエンジントルク";

        cluster.entrySet().stream().forEach(c -> {
            if (LOADER.getSyaryoMap().get(c.getKey()) != null) {
                Map load = LOADER.getSyaryoMap().get(c.getKey()).get(lkey);
                //printLoadMap(c.getValue(), c.getKey(), load);
                printAggreLoadMap(c.getValue(), c.getKey(), load);
            }
        });

        data.entrySet().stream().forEach(c -> {
        try (PrintWriter pw = CSVFileReadWrite.writerSJIS("clus_rank_eng" + c.getKey() + "_loadmap.csv")) {
            pw.println("," + String.join(",", col));
            c.getValue().entrySet().stream().map(d -> d.getKey() + "," + d.getValue().stream().map(di -> di.toString()).collect(Collectors.joining(","))).forEach(pw::println);
        }
        });
    }

    private static void printLoadMap(String cid, String sid, Map<String, List<String>> load) {
        try (PrintWriter pw = CSVFileReadWrite.addwriter("clus_rank_temp" + cid + "_loadmap.csv")) {
            row = load.keySet().stream().map(k -> k.split("_")[0])/*.filter(k -> !k.equals("1100"))*/.distinct().sorted().collect(Collectors.toList());
            col = load.keySet().stream().map(k -> k.split("_")[1])/*.filter(k -> !k.equals("10"))*/.distinct().sorted().collect(Collectors.toList());

            pw.println(sid + ",");
            pw.println("," + String.join(",", col));
            row.stream()
                    .map(r -> r + "," + (load.entrySet().stream()
                    .filter(l -> l.getKey().split("_")[0].equals(r))
                    .map(l -> l.getValue().get(0))
                    .collect(Collectors.joining(","))))
                    .forEach(pw::println);
            pw.println("");
        }
    }
    static Map<String, Map<String, List<Integer>>> data = new HashMap<>();
    static List<String> col, row;

    private static void printAggreLoadMap(String cid, String sid, Map<String, List<String>> load) {
        row = load.keySet().stream().map(k -> k.split("_")[0])/*.filter(k -> !k.equals("1100"))*/.distinct().sorted().collect(Collectors.toList());
        col = load.keySet().stream().map(k -> k.split("_")[1])/*.filter(k -> !k.equals("10"))*/.distinct().sorted().collect(Collectors.toList());

        if (data.get(cid) == null) {
            data.put(cid, new TreeMap<>());
        }

        row.stream()
                .map(r -> r + "," + (load.entrySet().stream()
                .filter(l -> l.getKey().split("_")[0].equals(r))
                .map(l -> l.getValue().get(0))
                .collect(Collectors.joining(","))))
                .forEach(s -> {
                    String[] sa = s.split(",");
                    if (data.get(cid).get(sa[0]) == null) {
                        data.get(cid).put(sa[0], IntStream.range(1, sa.length).boxed().map(i -> Double.valueOf(sa[i]).intValue()).collect(Collectors.toList()));
                    } else {
                        IntStream.range(1, sa.length).boxed().forEach(i -> {
                            int t = data.get(cid).get(sa[0]).get(i - 1) + Double.valueOf(sa[i]).intValue();
                            data.get(cid).get(sa[0]).set(i - 1, t);
                        });
                    }
                });

    }
}
