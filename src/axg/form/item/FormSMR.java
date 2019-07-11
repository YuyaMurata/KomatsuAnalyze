/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package axg.form.item;

import static creator.form.SyaryoObjectFormatting.currentKey;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 *
 * @author ZZ17807
 */
public class FormSMR {
    //サービスのSMRを整形
    public static Map form(Map<String, List<String>> smr, List indexList) {
        if (smr == null) {
            //System.out.println("Not found Work!");
            return null;
        }

        int smridx = indexList.indexOf("VALUE");
        String checkNumber = currentKey.split("-")[2].substring(0, currentKey.split("-")[2].length() - 1);

        //日付重複除去
        List<String> dateList = smr.keySet().stream()
                .filter(s -> !s.contains("None")) //日付が存在しない
                .map(s -> s.split("#")[0])
                .distinct()
                .collect(Collectors.toList());

        Map<String, List<String>> map = new TreeMap();
        Boolean zeroflg = false;
        for (String date : dateList) {
            String stdate = date;

            //重複日付を取り出す
            List<String> dateGroup = smr.keySet().stream()
                    .filter(s -> s.contains(stdate))
                    .filter(s -> !smr.get(s).get(smridx).equals("None")) //SMRが存在しない
                    .filter(s -> !smr.get(s).get(smridx).equals("999")) //怪しい数値の削除
                    .filter(s -> !smr.get(s).get(smridx).equals("9999")) //怪しい数値の削除
                    .filter(s -> !smr.get(s).get(smridx).equals(checkNumber)) //怪しい数値の削除
                    .collect(Collectors.toList());

            //欠損データのみのため
            if (dateGroup.isEmpty()) {
                continue;
            }

            if (date.length() > 8) {
                date = date.substring(0, 8);
            }

            for (String dg : dateGroup) {
                List list = smr.get(dg);

                if (map.get(date) == null) {
                    map.put(date, list);
                } else {
                    if (Float.valueOf(map.get(date).get(smridx)) < Float.valueOf(list.get(smridx).toString())) {
                        map.put(date, list);
                    }
                }
            }

            //整形処理
            map.get(date).set(smridx, map.get(date).get(smridx).split("\\.")[0]);
            if (map.get(date).get(smridx).equals("0")) {
                if (zeroflg) {
                    map.remove(date);
                } else {
                    zeroflg = true;
                }
            }
        }

        if (map.isEmpty()) {
            return null;
        }

        //異常データの排除
        return map;
    }
}
