/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package creator.form.item;

import static creator.form.SyaryoObjectFormatting.dup;
import google.map.MapPathData;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import obj.SyaryoObject;
import param.KomatsuUserParameter;

/**
 *
 * @author ZZ17807
 */
public class FormKomtraxt {

    //KOMTRAXデータの整形 (値の重複除去、日付の整形、小数->整数)
    public static void form(SyaryoObject syaryo, Map<String, List<String>> deploy) {
        //ALL
        List<String> kmList = KomatsuUserParameter.DATA_ORDER.stream().filter(s -> s.contains("KOMTRAX")).collect(Collectors.toList());
        String stdate = deploy.keySet().stream().findFirst().get();

        for (String id : kmList) {
            if (syaryo.get(id) == null) {
                continue;
            }

            //Formalize Date
            Map<String, List<String>> newMap = new TreeMap();
            String tmp = "";
            for (String date : syaryo.get(id).keySet()) {

                String d = date.split("#")[0].split(" ")[0].replace("/", "");
                String str = syaryo.get(id).get(date).toString();

                //出荷より前のセンサー情報を消す
                if (Integer.valueOf(d) < Integer.valueOf(stdate)) {
                    continue;
                }

                if (!tmp.equals(str)) {
                    List value = getTransformValue(id, syaryo.get(id).get(date));
                    if (id.equals("KOMTRAX_SMR")) {
                        newMap.put(d, value);
                    } else if (id.equals("KOMTRAX_ACT_DATA")) {  //ACT_DATAの構造が変わると使えない
                        if (newMap.get(d) == null) {
                            newMap.put(d, value);
                        } else {
                            Integer dup = Integer.valueOf(newMap.get(d).get(0).toString()) + Integer.valueOf(value.get(0).toString());
                            newMap.get(d).set(0, dup.toString());
                        }
                    } else {
                        newMap.put(dup(d, newMap), value);
                    }
                    tmp = str;

                }
            }
            
            //ACT_DATA
            if(id.equals("KOMTRAX_ACT_DATA"))
                newMap = transACTSMRData(newMap, 0, 1);
            
            syaryo.put(id, newMap);
        }
    }

    //KOMTRAXデータを整数値に変換(SMR, FUEL_CONSUME)
    private static List<String> getTransformValue(String id, List<String> kmvalue) {
        if (id.contains("SMR")) {
            kmvalue.set(0, String.valueOf(Double.valueOf(kmvalue.get(0)).intValue()));
        } else if (id.contains("FUEL")) {
            kmvalue.set(0, String.valueOf(Double.valueOf(kmvalue.get(0))));
        } else if (id.contains("GPS")) {
            //緯度
            kmvalue.set(0, String.valueOf(Double.valueOf(MapPathData.compValue(kmvalue.get(0)))));
            //経度
            kmvalue.set(1, String.valueOf(Double.valueOf(MapPathData.compValue(kmvalue.get(1)))));
        }

        return kmvalue;
    }

    //ACT_DATAの累積変換
    private static Map transACTSMRData(Map<String, List<String>> smr, int idx, int unit) {
        Map<String, Double> map = new TreeMap();

        if (smr == null) {
            return map;
        }

        smr.entrySet().forEach(s -> {
            map.put(s.getKey(), calcActSMR(s.getValue()));
        });

        //累積値に変換
        Double acm = 0d;
        Map<String, List<String>> actmap = new TreeMap();
        for (String d : map.keySet()) {
            acm += map.get(d);
            List v = new ArrayList();
            v.add(acm.toString());
            v.add(map.get(d));
            actmap.put(d, v);
        }

        return actmap;
    }

    private static Double calcActSMR(List<String> actList) {
        Integer value = Integer.valueOf(actList.get(0));
        Integer unit = Integer.valueOf(actList.get(1));

        return value / unit / 60d;
    }
}
