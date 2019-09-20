/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package creator.form.item;

import creator.form.DataRejectRule;
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
public class FormOrder {

    //受注情報を整形
    public static Map form(Map<String, List<String>> order, List indexList, DataRejectRule reject) {
        if (order == null) {
            //System.out.println("Not found Order!");
            return null;
        }

        //日付ソート
        int date = indexList.indexOf("ODDAY"); //kom_orderが紐づく場合 ODDAY
        //System.out.println(order);

        Map<String, Integer> sortMap = order.entrySet().stream()
                .filter(e -> !e.getValue().get(date).equals("None"))
                //.filter(e -> Integer.valueOf(reject.getNew()) <= Integer.valueOf(e.getValue().get(date)))
                .collect(Collectors.toMap(e -> e.getKey(), e -> Integer.valueOf(e.getValue().get(date))));

        //作番重複除去
        List<String> sbnList = sortMap.entrySet().stream()
                .sorted(Map.Entry.comparingByValue()).map(s -> s.getKey())
                .map(k -> k.split("#")[0])
                .distinct()
                .collect(Collectors.toList());

        //System.out.println("作番:"+sbnList);
        Map<String, List<String>> map = new LinkedHashMap();

        int db = indexList.indexOf("DB");
        int price = indexList.indexOf("SKKG");
        int kind = indexList.indexOf("ODR_KBN");
        int fin_date = indexList.indexOf("SGYO_KRDAY");

        for (String sbn : sbnList) {
            //重複作番を取り出す
            List<String> sbnGroup = order.keySet().stream()
                    .filter(s -> s.split("#")[0].equals(sbn))
                    .collect(Collectors.toList());

            //KOMPAS 受注テーブル情報が存在するときは取り出す
            Optional<List<String>> kom = sbnGroup.stream()
                    .map(s -> order.get(s))
                    .filter(l -> l.get(db).equals("kom_order"))
                    .findFirst();

            if (kom.isPresent()) {
                map.put(sbn, kom.get());
            } else {
                for (String sg : sbnGroup) {
                    List<String> list = order.get(sg);
                    if (map.get(sbn) == null) {
                        map.put(sbn, list);
                    } else {
                        //System.out.println("サービス経歴に金額の入ったデータが2つ以上存在");
                        if (!(map.get(sbn).get(price).equals("None") || list.get(price).equals("None"))) {
                            if (Double.valueOf(map.get(sbn).get(price)) < Double.valueOf(list.get(price))) {
                                map.put(sbn, list);
                            }
                        } else if (list.get(price).contains("+")) {
                            map.put(sbn, list);
                        }
                    }
                }
            }

            //System.out.println(map.get(sbn));
            reject.addPARTSID(sbn);
            if (map.get(sbn).get(kind).equals("2")) {
                reject.addWORKID(sbn);
            }
        }

        //金額と作業完了日の整形処理
        for (String sbn : map.keySet()) {
            List<String> list = map.get(sbn);
            list.set(price, String.valueOf(Double.valueOf(list.get(price)).intValue()));

            if (list.get(fin_date).contains("None")) {
                list.set(fin_date, list.get(date));
                //System.out.println(list);
            }
            
            map.put(sbn, list);

            //最新の受注日
            reject.currentDate = list.get(date);
        }

        return map;
    }
}
