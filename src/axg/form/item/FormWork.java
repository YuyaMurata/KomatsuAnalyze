/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package axg.form.item;

import creator.form.SyaryoObjectFormatting;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 *
 * @author ZZ17807
 */
public class FormWork {
    //作業明細を整形
    public static Map form(Map<String, List<String>> work, List odrSBN, List indexList, List workOrder) {
        if (work == null || odrSBN == null) {
            //System.out.println("Not found Work!");
            return null;
        }

        Map<String, List<String>> map = new LinkedHashMap();

        int db = indexList.indexOf("DB");
        
        //削除される情報を抽出
        //work.keySet().stream().filter(k -> !odrSBN.contains(k.split("#")[0])).forEach(k ->{
        //    String s = SyaryoObjectFormatting.currentKey+","+k+","+String.join(",", work.get(k));
        //    SyaryoObjectFormatting.w.add(s);
        //});
        
        for (Object sbn : odrSBN) {
            //重複作番を取り出す
            List<String> sbnGroup = work.keySet().stream()
                    .filter(s -> s.contains(sbn.toString()))
                    .collect(Collectors.toList());

            //KOMPAS 作業情報が存在するときは取り出す
            Optional<List<String>> kom = sbnGroup.stream()
                    .map(s -> work.get(s))
                    .filter(l -> l.get(db).equals("work_info"))
                    .findFirst();
            if (kom.isPresent()) {
                sbnGroup.stream()
                        .filter(s -> !work.get(s).get(db).equals("service"))
                        .forEach(s -> map.put(s, work.get(s)));
            } else {
                sbnGroup.stream()
                        .forEach(s -> map.put(s, work.get(s)));
            }
        }

        //代表作業抽出と設定
        int daihyoIdx = indexList.indexOf("DIHY_SGYO_FLG");
        int sgcdIdx = indexList.indexOf("SGYOCD");
        int sgnmIdx = indexList.indexOf("SGYO_NM");
        work.entrySet().stream()
                .filter(w -> w.getValue().get(daihyoIdx).equals("1"))
                .forEach(w -> {
                    map.entrySet().stream()
                            .filter(e -> e.getKey().contains(w.getKey().split("#")[0]))
                            .filter(e -> e.getValue().get(sgcdIdx).equals(w.getValue().get(sgcdIdx)))
                            .filter(e -> e.getValue().get(sgnmIdx).equals(w.getValue().get(sgnmIdx)))
                            .forEach(e -> map.get(e.getKey()).set(daihyoIdx, "1"));
                });

        Integer[] kosu = new Integer[]{
            indexList.indexOf("HJUN_KOS"),
            indexList.indexOf("INV_KOS"),
            indexList.indexOf("SIJI_KOS")
        };
        int price = indexList.indexOf("SKKG");

        //工数・金額の整形処理
        for (String sbn : map.keySet()) {
            List<String> list = map.get(sbn);
            for (int i : kosu) {
                if (!list.get(i).equals("None")) {
                    list.set(i, String.valueOf(Double.valueOf(list.get(i)).floatValue()));
                }
            }
            if (!list.get(price).equals("None")) {
                list.set(price, String.valueOf(Double.valueOf(list.get(price)).intValue()));
            }
        }

        return map;
    }
}
