/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package axg.form.item;

import creator.form.SyaryoObjectFormatting;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 *
 * @author ZZ17807
 */
public class FormParts {

    //部品明細を整形
    public static Map form(Map<String, List<String>> parts, List odrSBN, List indexList, List partsOrder) {
        if (parts == null || odrSBN == null) {
            //System.out.println("Not found Parts!");
            return null;
        }

        Map<String, List<String>> map = new LinkedHashMap();

        int db = indexList.indexOf("DB");
        
        //削除される情報を抽出
        //parts.keySet().stream().filter(k -> !odrSBN.contains(k.split("#")[0])).forEach(k ->{
        //    String s = SyaryoObjectFormatting.currentKey+","+k+","+String.join(",", parts.get(k));
        //    SyaryoObjectFormatting.p.add(s);
        //});

        for (Object sbn : odrSBN) {
            //重複作番を取り出す
            List<String> sbnGroup = parts.keySet().stream()
                    .filter(s -> s.contains(sbn.toString()))
                    .collect(Collectors.toList());

            //KOMPAS 部品情報が存在するときは取り出す
            Optional<List<String>> kom = sbnGroup.stream()
                    .map(s -> parts.get(s))
                    .filter(l -> l.get(db).equals("parts"))
                    .findFirst();
            if (kom.isPresent()) {
                sbnGroup.stream()
                        .filter(s -> !parts.get(s).get(db).equals("service"))
                        .forEach(s -> map.put(s, parts.get(s)));
            } else {
                sbnGroup.stream()
                        .forEach(s -> map.put(s, parts.get(s)));
            }
        }

        int quant = indexList.indexOf("JISI_SU");
        int price = indexList.indexOf("SKKG");
        List cancel = new ArrayList();
        //金額の整形処理・キャンセル作番の削除
        for (String sbn : map.keySet()) {
            List<String> list = map.get(sbn);
            if (list.get(quant).equals("0")) {
                cancel.add(sbn);
            }

            if (!list.get(price).equals("None")) {
                list.set(price, String.valueOf(Double.valueOf(list.get(price)).intValue()));
            }
        }
        cancel.stream().forEach(s -> map.remove(s));

        return map;
    }
}
