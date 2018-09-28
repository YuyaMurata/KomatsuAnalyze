/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.analize;

import analizer.SyaryoAnalizer;
import data.code.CodeRedefine;
import file.CSVFileReadWrite;
import index.SyaryoObjectElementsIndex;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import json.SyaryoToZip3;
import obj.SyaryoObject4;
import param.KomatsuDataParameter;

/**
 *
 * @author ZZ17390
 */
public class CreateAssociationRule {

    //レイアウト
    private static Map<String, List> dataIndex = SyaryoObjectElementsIndex.getInstance().getIndex();
    private static String PATH = KomatsuDataParameter.SYARYOOBJECT_FDPATH;
    private static String KISY = "PC200";
    private static Map<String, SyaryoObject4> map;

    public static void main(String[] args) {
        //車両の読み込み
        map = new SyaryoToZip3().read(PATH + "syaryo_obj_" + KISY + "_sv_form.bz2");
        int ksycdIdx = dataIndex.get("作業").indexOf("KSYCD");
        int sgcdIdx = dataIndex.get("作業").indexOf("SGYOCD");
        int mainflgIdx = dataIndex.get("作業").indexOf("0");
        int hnbnIdx = dataIndex.get("部品").indexOf("HNBN");
        int markerIdx = dataIndex.get("部品").indexOf("None");

        try (PrintWriter pw = CSVFileReadWrite.writer(KISY + "_associationrule.csv")) {

            Map<String, List<String>> associationMap = new HashMap();

            List<String> allParts = new ArrayList<>();

            map.values().stream()
                .filter(s -> s.get("作業") != null)
                .filter(s -> s.get("部品") != null)
                .forEach(s -> {
                    SyaryoAnalizer analize = new SyaryoAnalizer(s);

                    //作業作番抽出
                    List<String> worksbn = analize.getValue("作業", new Integer[]{0}).keySet().stream()
                        .map(w -> w.split("#")[0])
                        .distinct()
                        .collect(Collectors.toList());

                    for (String sbn : worksbn) {
                        Optional<String> sgcdCheck = analize.getSBNWork(sbn).values().stream()
                            .filter(w -> w.get(mainflgIdx).equals("1"))
                            .filter(w -> !w.get(sgcdIdx).contains("9900"))
                            .map(w -> w.get(ksycdIdx) + "_" + w.get(sgcdIdx))
                            .findFirst();

                        if (!sgcdCheck.isPresent() || analize.getSBNParts(sbn) == null) {
                            continue;
                        }

                        String sgcd = sgcdCheck.get();

                        List<String> parts = analize.getSBNParts(sbn).values().stream()
                            .filter(p -> p.get(markerIdx).equals("10"))//コマツ部品
                            .map(p -> CodeRedefine.partsCDRedefine(p.get(hnbnIdx))) //部品コードを再定義
                            .collect(Collectors.toList());

                        allParts.addAll(parts);
                        
                        if (parts.isEmpty()) {
                            continue;
                        }

                        //アソシエーションルール
                        associationMap.put(s.name + "_" + sbn + "_" + sgcd.split("_")[1], parts);
                    }
                });

            List<String> allparts = associationMap.values().stream()
                .flatMap(l -> l.stream())
                .distinct()
                .sorted()
                .collect(Collectors.toList());

            //Header
            /*pw.println("SID," + String.join(",", allparts));
            associationMap.entrySet().stream()
                .map(al -> "'" + al.getKey().split("_")[2] + "," + allparts.stream()
                .map(pa -> al.getValue().contains(pa) ? "1" : "0")
                .collect(Collectors.joining(",")))
                .forEach(pw::println);
            */
        }
    }
}
