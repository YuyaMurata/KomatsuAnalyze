/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package export;

import analizer.SyaryoAnalizer;
import file.CSVFileReadWrite;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import file.SyaryoToCompress;
import java.util.ArrayList;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import obj.LoadSyaryoObject;
import obj.SyaryoObject;
import param.KomatsuDataParameter;

/**
 *
 * @author ZZ17390
 */
public class AccidentData {

    private static String kisy = "PC200";
    private static LoadSyaryoObject LOADER = KomatsuDataParameter.LOADER;
    private static List<String> ACCIDENT_WORDS = KomatsuDataParameter.ACCIDENT_WORDS;

    public static void main(String[] args) {
        LOADER.setFile(kisy + "_sv_form");
        Map<String, SyaryoObject> syaryoMap = LOADER.getSyaryoMap();

        String outputname = "accident_" + kisy + ".csv";
        try (PrintWriter csv = CSVFileReadWrite.writer(outputname)) {
            extractAccident(syaryoMap, csv);
        }
    }

    public static void extractAccident(Map<String, SyaryoObject> syaryoMap, PrintWriter csv) {
        int cnt = 0;

        int compidx = LOADER.index("受注", "会社CD");
        int priceidx = LOADER.index("受注", "SKKG");

        int odrtxtidx = LOADER.index("受注", "GAIYO_1");
        int odrtxtidx2 = LOADER.index("受注", "GAIYO_2");
        int wrktxtidx = LOADER.index("作業", "SGYO_NM");

        csv.println("SID,会社CD,作番,金額,テキスト");
        for (SyaryoObject syaryo : syaryoMap.values()) {
            try (SyaryoAnalizer analize = new SyaryoAnalizer(syaryo)) {

                cnt++;
                if (syaryo.get("受注") == null) {
                    System.out.println(syaryo.getName());
                    continue;
                }

                for (String sbn : syaryo.get("受注").keySet()) {
                    String txt = syaryo.get("受注").get(sbn).get(odrtxtidx) + ","
                        + syaryo.get("受注").get(sbn).get(odrtxtidx2) + ","
                        + (analize.getSBNWork(sbn) != null ? analize.getSBNWork(sbn).values().stream().map(w -> w.get(wrktxtidx)).collect(Collectors.joining(",")) : "");
                    
                    Optional<String> accidents = ACCIDENT_WORDS.stream().filter(w -> txt.contains(w)).findFirst();
                    if(!accidents.isPresent())
                        continue;
                    
                    List line = new ArrayList();
                    line.add(syaryo.name);
                    line.add(syaryo.get("受注").get(sbn).get(compidx));
                    line.add(sbn);
                    line.add(syaryo.get("受注").get(sbn).get(priceidx));
                    line.add(txt);
                    
                    csv.println(String.join(",", line));
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
