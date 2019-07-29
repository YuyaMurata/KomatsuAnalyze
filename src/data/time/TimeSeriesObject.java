/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.time;

import analizer.SyaryoAnalizer;
import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import obj.SyaryoLoader;
import param.KomatsuUserParameter;
import param.obj.UserPartsObject;

/**
 *
 * @author ZZ17807
 */
public class TimeSeriesObject {

    private static SyaryoLoader LOADER = SyaryoLoader.getInstance();
    private static UserPartsObject PARTS = KomatsuUserParameter.PC200_USERPARTS_DEF;
    private static DecimalFormat df = new DecimalFormat("0000");

    public String sid;
    public String key;
    public String target;
    public Map<String, String> series;

    public TimeSeriesObject(SyaryoAnalizer s, String key, String target) {
        this.sid = s.name;
        this.key = key;
        this.target = target;
        this.series = toSeries(s, key, target);
    }

    private Map<String, String> toSeries(SyaryoAnalizer s, String key, String target) {
        Map<String, String> t;

        if (key.equals("受注")) {
            if (target.equals("")) {
                t = orderToSeries(s);
            } else {
                t = partsToSeries(s, target);
            }
        } else {
            t = komtraxErrorToSeries(s, target);
        }

        return t;
    }

    //サービス実績の時系列を取得
    public Map<String, String> orderToSeries(SyaryoAnalizer s) {
        Map<String, String> t = new LinkedHashMap<>();
        String skey = "受注";
        int idx = LOADER.index(skey, "SGYO_KRDAY");

        if (s.get(skey) != null) {
            s.get(skey).entrySet().stream()
                    .sorted(Comparator.comparing(o -> Integer.valueOf(o.getValue().get(idx))))
                    .forEach(o ->{
                        t.put(dup(o.getValue().get(idx), t), o.getKey());
                    });
        }
        return t;
    }

    public String getFirstOrder(SyaryoAnalizer s) {
        String t = s.get("受注").entrySet().stream()
                .sorted(Comparator.comparing(o -> Integer.valueOf(o.getValue().get(LOADER.index("受注", "SGYO_KRDAY")).split("#")[0])))
                .map(o -> o.getKey() + "," + String.join(",", o.getValue()))
                .findFirst().get();

        return t;
    }

    //特定部品の交換実績時系列を取得
    public Map<String, String> partsToSeries(SyaryoAnalizer s, String target) {
        Map<String, String> t = new LinkedHashMap<>();
        String skey = "受注";
        int idx = LOADER.index(skey, "SGYO_KRDAY");

        if (PARTS.check(s.name, target)) {
            try {
                PARTS.index.get(s.name).entrySet().stream() //ユーザー定義の部品
                        .filter(e -> e.getValue().equals(target)) //ターゲット以外の部品は除去
                        .map(e -> e.getKey()[1]) //ユーザー定義部品の作番情報
                        .filter(sbn -> s.get(skey).get(sbn) != null) //分析器による除外処理を考慮
                        .sorted(Comparator.comparing(sbn -> Integer.valueOf(s.get(skey).get(sbn).get(idx).split("#")[0])))
                        .distinct()
                        .forEach(sbn -> t.put(dup(s.get(skey).get(sbn).get(idx), t), sbn));
            } catch (Exception e) {
                System.err.println(s.name + ":" + key);
                e.printStackTrace();
                System.exit(0);
            }
        }

        return t;
    }
    
    public String firstService(){
        if(!series.isEmpty())
            return series.keySet().stream().limit(1).findFirst().get();
        else
            return null;
    }
    
    public Boolean isEmpty(){
        return series.isEmpty();
    }

    public Map<String, String> komtraxErrorToSeries(SyaryoAnalizer s, String target) {
        Map<String, String> t = new HashMap<>();
        String key = "KOMTRAX_ERROR";
        int idx = LOADER.index(key, "ERROR_CODE");

        if (s.get(key) != null) {
            t = s.get(key).entrySet().stream()
                    .filter(e -> e.getValue().get(idx).equals(target))
                    .sorted(Comparator.comparing(e -> Integer.valueOf(e.getKey().split("#")[0])))
                    .collect(Collectors.toMap(
                            o -> o.getKey(),
                            o -> o.getValue().get(idx),
                            (o1, o2) -> o1, LinkedHashMap::new));

        }

        return t;
    }
    
    //日付が重複する場合に連番を付与　20180809が2つ登場したとき-> 20180809#0001
    public static String dup(String key, Map map) {
        int cnt = 0;
        String k = key;
        while (map.get(k) != null) {
            k = key + "#" + df.format(++cnt);
        }
        return k;
    }
    
    public List<String> floorExt(String date){
        return series.keySet().stream()
                        .filter(d -> Integer.valueOf(d.split("#")[0]) <= Integer.valueOf(date.split("#")[0]))
                        .map(d -> d.split("#")[0])
                        .distinct()
                        .collect(Collectors.toList());
    }
    
    public List<String> upperExt(String date){
        return series.keySet().stream()
                        .filter(d -> Integer.valueOf(d.split("#")[0]) > Integer.valueOf(date.split("#")[0]))
                        .map(d -> d.split("#")[0])
                        .distinct()
                        .collect(Collectors.toList());
    }
}
