/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.analize;

import analizer.SyaryoAnalizer;
import data.code.PartsCodeConv;
import file.CSVFileReadWrite;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.TreeMap;
import obj.LoadSyaryoObject;
import obj.SyaryoLoader;
import obj.SyaryoObject;
import param.KomatsuDataParameter;

/**
 *
 * @author ZZ17390
 */
public class CreateAssociationRule {

    //レイアウト
    private static String PATH = KomatsuDataParameter.SYARYOOBJECT_FDPATH;
    private static String KISY = "PC200";
    private static SyaryoLoader LOADER = SyaryoLoader.getInstance();

    public static void main(String[] args) {
        //車両の読み込み
        LOADER.setFile(KISY + "_km_form");
        Map<String, SyaryoObject> syaryoMap = LOADER.getSyaryoMap();

        //部品フィルタ
        //AnalizeDataFilter.partsdatafilter(syaryoMap, LOADER._header);
        asscoiationRule(syaryoMap, LOADER.getHeader());
    }

    private static void asscoiationRule(Map<String, SyaryoObject> syaryoMap, SyaryoObject dataHeader) {
        int ksycdIdx = dataHeader.get("作業").get("作業").indexOf("会社CD");
        int sgcdIdx = dataHeader.get("作業").get("作業").indexOf("SGYOCD");
        int mainflgIdx = dataHeader.get("作業").get("作業").indexOf("DIHY_SGYO_FLG");
        int hnbnIdx = dataHeader.get("部品").get("部品").indexOf("HNBN");

        Map<List<String>, String> associationMap = new HashMap();

        syaryoMap.values().stream()
            .filter(s -> s.get("作業") != null)
            .filter(s -> s.get("部品") != null)
            .forEach(s -> {
                try (SyaryoAnalizer analize = new SyaryoAnalizer(s, true)) {
                    System.out.println(s.name);
                    
                    //作業作番抽出
                    List<String> worksbn = analize.getValue("作業", new Integer[]{0}).keySet().stream()
                        .map(w -> w.split("#")[0])
                        .distinct()
                        .collect(Collectors.toList());

                    for (String sbn : worksbn) {
                        Optional<String> sgcdCheck = analize.getSBNWork(sbn).values().stream()
                            .filter(w -> w.get(mainflgIdx).equals("1"))
                            .map(w -> w.get(ksycdIdx) + "_" + w.get(sgcdIdx))
                            .findFirst();

                        if (!sgcdCheck.isPresent() || analize.getSBNParts(sbn) == null) {
                            continue;
                        }

                        String sgcd = sgcdCheck.get();

                        List<String> parts = analize.getSBNParts(sbn).values().stream()
                            .map(p -> PartsCodeConv.mainPartsDefineCode(p.get(hnbnIdx))) //部品コードを再定義
                            .filter(p -> p != null)
                            .distinct()
                            .collect(Collectors.toList());

                        if (parts.isEmpty()) {
                            continue;
                        }

                        //アソシエーションルール
                        String data = associationMap.get(parts);
                        if (data == null) {
                            associationMap.put(parts, sgcd.split("_")[1] + "_1");
                        } else {
                            associationMap.put(parts, sgcd.split("_")[1] + "_" + (Integer.valueOf(data.split("_")[1]) + 1));
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });

        try (PrintWriter pw = CSVFileReadWrite.writer(KISY + "_associationrule_new.csv")) {
            //Header
            pw.println("Info,N,Parts");
            associationMap.entrySet().stream()
                .map(al -> "'" + al.getValue().split("_")[0] + "," + al.getValue().split("_")[1] + "," + String.join(",", al.getKey()))
                .forEach(pw::println);
        }
        
        try (PrintWriter pw = CSVFileReadWrite.writer(KISY + "_associationrule_vector.csv")) {
            Map redef = new TreeMap(KomatsuDataParameter.PC_PARTS_EDEFNAME);
            //Header
            pw.println("Info,N,"+String.join(",", redef.values()));
            associationMap.entrySet().stream()
                .map(   al -> "'" + 
                        al.getValue().split("_")[0] + "," + 
                        al.getValue().split("_")[1] + "," + 
                        redef.keySet().stream()
                                        .map(r -> al.getKey().contains(r.toString())==true?"1":"")
                                        .collect(Collectors.joining(",")))
                .forEach(pw::println);
        }
    }
}
