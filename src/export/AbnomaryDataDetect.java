/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package export;

import analizer.SyaryoAnalizer;
import static data.detect.AbnomalyDection.detectChiSquare;
import file.CSVFileReadWrite;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import obj.SyaryoLoader;
import obj.SyaryoObject;

/**
 *
 * @author ZZ17390
 */
public class AbnomaryDataDetect {

    private static String KISY = "PC200";
    private static SyaryoLoader LOADER = SyaryoLoader.getInstance();

    public static void main(String[] args) {
        LOADER.setFile(KISY + "_form");

        //工数分析
        //detectSteps(LOADER.getSyaryoMap());
        
        detectNumParts(LOADER.getSyaryoMap());
    }

    private static void detectSteps(Map<String, SyaryoObject> syaryoMap) {
        Map<String, String> data = new HashMap();

        int compidx = LOADER.index("受注", "会社CD");
        int smidx1 = LOADER.index("受注", "GAIYO_1");
        int smidx2 = LOADER.index("受注", "GAIYO_2");
        int idx = LOADER.index("作業", "INV_KOS");

        syaryoMap.values().stream().filter(s -> s.get("受注") != null).forEach(s -> {
            try (SyaryoAnalizer sa = new SyaryoAnalizer(s, false)) {
                Map<String, List<String>> odr = s.get("受注");

                odr.keySet().stream().filter(sbn -> sa.getSBNWork(sbn) != null).forEach(sbn -> {
                    Map<String, List<String>> w = sa.getSBNWork(sbn);
                    Double st = w.values().stream().mapToDouble(l -> Double.valueOf(l.get(idx))).sum();

                    String key = s.name + "_" + sbn + "_" + w.get(sbn).get(compidx) + "_" + odr.get(sbn).get(smidx1) + "_" + odr.get(sbn).get(smidx2);
                    data.put(key, st.toString());
                });
            } catch (Exception ex) {
                ex.printStackTrace();
                System.exit(0);
            }
        });

        Map<String, String> map = detectChiSquare(data, 0.1, true);
        map.entrySet().stream().filter(s -> s.getValue().equals("1")).map(s -> s.getKey().replace("_", ",") + "," + s.getValue()).forEach(System.out::println);

    }

    private static void detectNumParts(Map<String, SyaryoObject> syaryoMap) {
        Map<String, String> data = new HashMap();
        List<String> err = new ArrayList<>();

        int idx = LOADER.index("部品", "JISI_SU");

        syaryoMap.values().stream().filter(s -> s.get("部品") != null).forEach(s -> {
            Map<String, List<String>> parts = s.get("部品");
            parts.entrySet().stream().forEach(p -> {
                if(p.getValue().get(idx).equals("None"))
                    err.add(s.name+","+p.getKey()+","+String.join(",", p.getValue()));
                else
                    data.put(s.name+"|"+p.getKey().split("#")[0]+"|"+String.join("|", p.getValue()), p.getValue().get(idx));
            });
        });

        List<String> list = detectChiSquare(data, 0.1, true).entrySet().stream()
                                        .filter(d -> d.getValue().equals("1"))
                                        .map(d -> d.getKey().replace("|", ","))
                                        .collect(Collectors.toList());
        System.out.println("N="+list.size());
        
        try (PrintWriter pw = CSVFileReadWrite.writerSJIS("部品_発注数量異常検知.csv")) {
            //header
            pw.println("SID,SBN,"+String.join(",", LOADER.indexes("部品")));
            list.stream().map(s -> s.split("_")[0]).forEach(pw::println);
            
            //エラー情報
            pw.println("\nエラー情報");
            err.stream().forEach(pw::println);
        }
    }
}
