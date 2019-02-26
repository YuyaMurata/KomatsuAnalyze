/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package export;

import analizer.SyaryoAnalizer;
import file.CSVFileReadWrite;
import file.MapToJSON;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import obj.SyaryoLoader;
import obj.SyaryoObject;

/**
 *
 * @author ZZ17390
 */
public class WorkingParts {

    //レイアウト
    private static String KISY = "PC200";
    private static SyaryoLoader LOADER = SyaryoLoader.getInstance();
    private static Map<String, String> survey = new MapToJSON().toMap("user\\PC200調査品番.json");

    public static void main(String[] args) {
        //車両の読み込み
        LOADER.setFile(KISY + "_form");

        partsToWorking(LOADER.getSyaryoMap());
    }

    private static void partsToWorking(Map<String, SyaryoObject> map) {
        int hnbnIdx = LOADER.index("部品", "HNBN");
        int bhnnmIdx = LOADER.index("部品", "BHN_NM");
        int ksycdIdx = LOADER.index("部品", "会社CD");
        int sgcdIdx = LOADER.index("作業", "SGYOCD");
        int odrIdx = LOADER.index("受注", "ODR_KBN");

        Map<String, Integer> sgcd = new HashMap<>();
        Map<String, List<String>> data = new LinkedHashMap<>();
        map.values().stream().filter(syaryo -> syaryo.get("受注") != null).forEach(syaryo -> {
            try (SyaryoAnalizer s = new SyaryoAnalizer(syaryo, false)) {
                
                List<String> sbns = s.get().get("受注").entrySet().stream()
                                    .filter(odr -> odr.getValue().get(odrIdx).equals("2")) //単体販売を除く
                                    .map(odr -> odr.getKey())
                                    .collect(Collectors.toList());
                //System.out.println(s.get().getName()+" - "+sbns.size());

                if (s.get().get("部品") != null) {
                    sbns.stream().filter(sbn -> s.getSBNParts(sbn) != null).forEach(sbn -> {
                        List<String> hnbn = s.getSBNParts(sbn).values().stream()
                            .map(p -> s.get().name + "," + p.get(ksycdIdx) + "," + sbn + "," + p.get(hnbnIdx) + "," + p.get(bhnnmIdx))
                            .distinct()
                            .filter(h -> survey.keySet().stream().filter(suv -> h.contains(suv)).findFirst().isPresent())
                            .map(h -> h + "," + survey.entrySet().stream().filter(suv -> h.contains(suv.getKey())).findFirst().get().getValue())
                            .collect(Collectors.toList());

                        if (!hnbn.isEmpty()) {
                            List<String> w;
                            if (s.getSBNWork(sbn) != null) {
                                w = s.getSBNWork(sbn).values().stream()
                                    .map(sg -> sg.get(sgcdIdx))
                                    .distinct()
                                    .collect(Collectors.toList());
                            } else {
                                w = new ArrayList<>();
                            }
                            
                            //作業コードのまとめ
                            w.stream().forEach(sg -> {
                                if (sgcd.get(sg) == null) {
                                    sgcd.put(sg, 0);
                                }
                                sgcd.put(sg, sgcd.get(sg) + 1);
                            });

                            //データの登録
                            hnbn.stream().forEach(h -> data.put(h, w));
                        }

                    });
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        sgcd.entrySet().stream().map(sg -> sg.getKey() + "," + sg.getValue()).forEach(System.out::println);
        System.out.println("total," + data.size());
        
        List<String> sgh = new ArrayList<>();
        //累積比率による出力制限
        int sum = sgcd.values().stream().mapToInt(sg -> sg).sum();
        int acm = 0;
        for(String sg : sgcd.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).map(sg -> sg.getKey()).collect(Collectors.toList())){
            sgh.add(sg);
            acm += sgcd.get(sg);
            if(((double)acm / (double)sum) > 0.9d)
                break;
        }
        
        try (PrintWriter pw = CSVFileReadWrite.writerSJIS(KISY + "_workingparts.csv")) {
            //Header
            String header = "SID,会社コード,作番,品番,品名,調査,";
            pw.println(header + String.join(",", sgh));

            //出力
            data.entrySet().stream()
                .map(d -> d.getKey() + "," + sgh.stream().map(sg -> d.getValue().contains(sg) ? "1" : "0").collect(Collectors.joining(",")))
                .forEach(pw::println);
        }
    }
}
