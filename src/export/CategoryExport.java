/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package export;

import analizer.SyaryoAnalizer;
import file.CSVFileReadWrite;
import file.ListToCSV;
import file.MapToJSON;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import obj.SyaryoLoader;
import obj.SyaryoObject;

/**
 *
 * @author ZZ17807
 */
public class CategoryExport {

    private static SyaryoLoader LOADER = SyaryoLoader.getInstance();
    private static String KISY = "PC200";

    public static void main(String[] args) {
        LOADER.setFile(KISY + "_form");
        Map<String, SyaryoObject> map = LOADER.getSyaryoMap();

        Map<String, Integer> headers = new LinkedHashMap();
        headers.put("部品.会社", LOADER.index("部品", "会社CD"));
        headers.put("部品.作番", LOADER.index("部品", "KEY"));
        headers.put("部品.品番", LOADER.index("部品", "HNBN"));
        headers.put("部品.品名", LOADER.index("部品", "BHN_NM"));
        headers.put("部品.数量", LOADER.index("部品", "JISI_SU"));
        headers.put("部品.金額", LOADER.index("部品", "SKKG"));

        //カテゴリファイル 品名|品番
        Map<String, String> category = ListToCSV.toKeyMap("PC200_カテゴリ1_1.csv", 1, 0);
        
        try (PrintWriter pw = CSVFileReadWrite.writerSJIS("カテゴリ1_1_部品リスト.csv")) {
            map.values().stream().forEach(s -> {
                try(SyaryoAnalizer a = new SyaryoAnalizer(s, Boolean.FALSE)){
                    export(pw, headers, a, category);
                } catch (Exception ex) {
                    System.err.println(s.name);
                }
            });
        }
    }

    private static void export(PrintWriter pw, Map<String, Integer> h, SyaryoAnalizer syaryo, Map<String, String> c) {
        List<Integer> index = new ArrayList(h.values());
        String key = h.keySet().stream().findFirst().get().split("\\.")[0];
        syaryo.getValue(key, index.toArray(new Integer[index.size()])).values().stream()
                .filter(s -> c.get(s.get(2)+s.get(3)) != null)
                .map(s -> syaryo.name + "," + String.join(",", s))
                .forEach(pw::println);
    }
}
