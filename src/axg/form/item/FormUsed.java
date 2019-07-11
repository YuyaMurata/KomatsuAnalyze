/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package axg.form.item;

import static creator.form.SyaryoObjectFormatting.dateFormalize;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author ZZ17807
 */
public class FormUsed {
    public static Map form(Map<String, List<String>> used, List indexList, String newd, List kuec) {
        if (used == null) {
            //System.out.println("Not found Used");
            return null;
        }

        //List price index
        int hyomen = indexList.indexOf("HM_URI_KN");
        int jitsu = indexList.indexOf("RL_URI_KN");
        int hyojun = indexList.indexOf("STD_SY_KKU");

        //日付揃え
        used = dateFormalize(used);

        Map<String, List<String>> map = new TreeMap();

        //修正しない
        if (used.size() == 1) {
            //KUEC売却後、使用ユーザー存在しない
            if (kuec.size() > 0 || (Integer.valueOf(used.keySet().stream().findFirst().get()) <= Integer.valueOf(newd))) {
                return null;
            }

            List<String> list = used.values().stream().findFirst().get();
            if (list.get(hyomen).contains("+") || list.get(hyomen).contains("_")) {
                for (int i = hyomen; i < list.size(); i++) {
                    list.set(i, String.valueOf(Double.valueOf(list.get(i).replace("_", "")).intValue()));
                }
            }
            return used;
        }

        //複数存在するときの処理
        String key = "";
        for (String date : used.keySet()) {
            List list = used.get(date);
            String d = date.split("#")[0].replace("/", "");

            //KUECを除外
            if (kuec.contains(d)) {
                continue;
            }

            //新車より前に存在する中古車情報を削除
            if (Integer.valueOf(d) <= Integer.valueOf(newd)) {
                continue;
            }

            if (!key.equals(d)) {
                key = d;
                map.put(key, list);
            } else {
                if (!list.get(hyomen).toString().equals("None") && !map.get(d).get(hyomen).toString().contains("+")) {
                    map.get(d).set(hyomen, list.get(hyomen).toString());
                }
                if (!list.get(jitsu).toString().equals("None") && !map.get(d).get(jitsu).toString().contains("+")) {
                    map.get(d).set(jitsu, list.get(jitsu).toString());
                }
                if (!list.get(hyojun).toString().equals("None") && !map.get(d).get(hyojun).toString().contains("+")) {
                    map.get(d).set(hyojun, list.get(hyojun).toString());
                }
            }
        }

        //KUECしか存在しない
        if (map.isEmpty()) {
            //System.out.println("KUECデータしか存在しない!");
            //System.exit(0);
            return null;
        }

        //List整形
        for (String d : map.keySet()) {
            List list = map.get(d);
            for (int i = hyomen; i < hyomen + 3; i++) {
                if (!list.get(i).toString().equals("None")) {
                    list.set(i, String.valueOf(Double.valueOf(list.get(i).toString().replace("_", "")).intValue()));
                }
            }
        }

        return map;
    }
}
