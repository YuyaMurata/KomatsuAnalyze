/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.analize;

import analizer.SyaryoAnalizer;
import data.detect.AccidentDetect;
import data.merge.SyaryoDataMerge;
import file.CSVFileReadWrite;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import obj.SyaryoLoader;
import obj.SyaryoObject;

/**
 *
 * @author ZZ17807
 */
public class AccidentsEstimate {

    private static SyaryoLoader LOADER = SyaryoLoader.getInstance();
    private static String KISY = "PC200";

    public static void main(String[] args) {
        LOADER.setFile(KISY + "_form");
        accidents(LOADER.getSyaryoMap());
    }

    private static void accidents(Map<String, SyaryoObject> map) {
        try (PrintWriter pw = CSVFileReadWrite.writerSJIS(KISY + "_accident.csv")) {
            //header
            //pw.println("SID," + String.join(",", LOADER.indexes("受注")) + ",TXT判定,P判定");
            pw.println("SID,KEY,修/単,受注情報," + String.join(",", LOADER.indexes("受注")));
            pw.println("SID,KEY,修/単,作業情報," + String.join(",", LOADER.indexes("作業")));
            pw.println("SID,KEY,修/単,部品情報," + String.join(",", LOADER.indexes("部品")));

            List<String> priceSBN = AccidentDetect.priceDetect(SyaryoDataMerge.unikeyMerge(map, "受注", LOADER.index("受注", "会社CD"), LOADER.index("受注", "SKKG")));

            map.values().stream().forEach(syaryo -> {
                try (SyaryoAnalizer s = new SyaryoAnalizer(syaryo, false)) {

                    System.out.println(s.get().name);

                    Map<String, List<String>> odr = s.get("受注");
                    Map<String, List<String>> work = s.get("作業");

                    List<String> sbnsP = odr.entrySet().stream()
                            .map(e -> e.getValue().get(LOADER.index("受注", "会社CD")) + "." + e.getKey())
                            .filter(sbn -> priceSBN.contains(sbn))
                            .collect(Collectors.toList());
                    List<String> sbnsT = AccidentDetect.wordsDetect(odr, work);

                    List<String> sbns = new ArrayList<>();
                    //all
                    //sbns.addAll(sbnsT);
                    //sbns.addAll(sbnsP);
                    sbns.addAll(sbnsP.stream().filter(sbn -> !sbnsT.contains(sbn)).collect(Collectors.toList()));
                    
                    //テキスト判定の出力
                    /*sbns.stream()
                        .filter(sbn -> odr.get(sbn.split("\\.")[1]) != null)
                        .map(sbn -> s.name + "," + String.join(",", odr.get(sbn.split("\\.")[1])) + "," + (sbnsT.contains(sbn) ? "1" : "0") + "," + (sbnsP.contains(sbn) ? "1" : "0"))
                        .forEach(pw::println);
                     */
                    
                    for(String sbn : sbns){
                        String key = sbn.split("\\.")[1];
                        Map<String, List<String>> w = s.getSBNWork(key);
                        Map<String, List<String>> p = s.getSBNParts(key);
                        
                        String f = odr.get(key).get(LOADER.index("受注", "ODR_KBN"));
                        pw.println(baseInfo(s.get().name, sbn+","+f, odr.get(key)));
                        
                        if(w != null)
                            w.values().stream().map(d -> workInfo(s.get().name, sbn+","+f, d)).forEach(pw::println);
                        
                        if(p != null)
                            p.values().stream().map(d -> partsInfo(s.get().name, sbn+","+f, d)).forEach(pw::println);
                    }
                    
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        }
    }

    private static String baseInfo(String name, String key, List<String> odr) {
        return name + "," + key + ",受注情報," + String.join(",", odr);
    }

    private static String workInfo(String name, String key, List<String> work) {
        return name + "," + key + ",作業情報," + String.join(",", work);
    }

    private static String partsInfo(String name, String key, List<String> parts) {
        return name + "," + key + ",部品情報," + String.join(",", parts);
    }
}
