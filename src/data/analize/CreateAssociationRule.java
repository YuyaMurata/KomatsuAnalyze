/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.analize;

import analizer.SyaryoAnalizer;
import data.code.CodeRedefine;
import file.CSVFileReadWrite;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import file.SyaryoToCompress;
import obj.SyaryoObject4;
import param.KomatsuDataParameter;

/**
 *
 * @author ZZ17390
 */
public class CreateAssociationRule {

    //レイアウト
    private static String PATH = KomatsuDataParameter.SYARYOOBJECT_FDPATH;
    private static final String KISY = "PC200";
    private static final String exportFile = "ExportData_" + KISY + "_ALL.json";

    public static void main(String[] args) {
        //車両の読み込み
        Map<String, SyaryoObject4> syaryoMap = new SyaryoToCompress().readJSON(exportFile);
        SyaryoObject4 dataHeader = syaryoMap.get("_headers");
        System.out.println(dataHeader.getMap());
        syaryoMap.remove("_headers");

        //部品フィルタ
        AnalizeDataFilter.partsdatafilter(syaryoMap, dataHeader);

        asscoiationRule(syaryoMap, dataHeader);
    }

    private static void asscoiationRule(Map<String, SyaryoObject4> syaryoMap, SyaryoObject4 dataHeader) {
        int ksycdIdx = dataHeader.get("作業").get("作業").indexOf("KSYCD");
        int sgcdIdx = dataHeader.get("作業").get("作業").indexOf("SGYOCD");
        int mainflgIdx = dataHeader.get("作業").get("作業").indexOf("0");
        int hnbnIdx = dataHeader.get("部品").get("部品").indexOf("HNBN");

        Map<List<String>, String> associationMap = new HashMap();
        
        syaryoMap.values().stream()
            .filter(s -> s.get("作業") != null)
            .filter(s -> s.get("部品") != null)
            .forEach(s -> {
                SyaryoAnalizer analize = new SyaryoAnalizer(s, dataHeader.toMap(true));
                
                
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
                        .map(p -> CodeRedefine.partsCDRedefine(p.get(hnbnIdx))) //部品コードを再定義
                        .filter(p -> p != null)
                        .distinct()
                        .collect(Collectors.toList());

                    if (parts.isEmpty()) {
                        continue;
                    }

                    //アソシエーションルール
                    String data = associationMap.get(parts);
                    if(data == null)
                        associationMap.put(parts, sgcd.split("_")[1]+"_1");
                    else
                        associationMap.put(parts, sgcd.split("_")[1]+"_"+(Integer.valueOf(data.split("_")[1])+1));
                }
            });

        try (PrintWriter pw = CSVFileReadWrite.writer(KISY + "_associationrule.csv")) {
            //Header
            pw.println("Info,N,Parts");
            associationMap.entrySet().stream()
                .map(al -> "'" + al.getValue().split("_")[0] + "," + al.getValue().split("_")[1] +","+String.join(",", al.getKey()))
                .forEach(pw::println);
        }
    }
}
