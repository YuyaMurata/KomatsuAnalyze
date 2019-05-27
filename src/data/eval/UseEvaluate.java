/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.eval;

import analizer.SyaryoAnalizer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import obj.SyaryoLoader;
import obj.SyaryoObject;

/**
 *
 * @author ZZ17390
 */
public class UseEvaluate extends EvaluateTemplate {

    private static SyaryoLoader LOADER = SyaryoLoader.getInstance();
    private static Integer R = 3;
    private static Boolean flg = true;

    public UseEvaluate() {
        if (flg) {
            SyaryoObject s = LOADER.getSyaryoMap().get("PC200-10-454720"); //ヘッダー取得用車両
            setHeader("LOADMAP_実エンジン回転VSエンジントルク", new ArrayList<>(s.get("LOADMAP_実エンジン回転VSエンジントルク").keySet()));
            setHeader("LOADMAP_エンジン水温VS作動油温", new ArrayList<>(s.get("LOADMAP_エンジン水温VS作動油温").keySet()));
            flg = false;
        }
    }

    @Override
    public Map<String, Double> normalize(SyaryoAnalizer s, String key, Map<String, List<String>> aggregatedata) {
        Map norm = header(key).stream()
                .collect(Collectors.toMap(
                        h -> h,
                        h -> aggregatedata.get(key).contains(h)?1d:0d,
                        (h1, h2) -> h1,
                        LinkedHashMap::new
                ));
        
        return norm;
    }

    @Override
    public Map<String, List<String>> aggregate(SyaryoAnalizer s) {
        Map<String, List<String>> data = new HashMap<>();

        data.put("LOADMAP_実エンジン回転VSエンジントルク", engine(s.get("LOADMAP_実エンジン回転VSエンジントルク")));
        data.put("LOADMAP_エンジン水温VS作動油温", temperature(s.get("LOADMAP_エンジン水温VS作動油温")));

        return data;
    }

    private List<String> engine(Map<String, List<String>> map) {
        if (map == null) {
            return null;
        }

        //トルク10の値を削除  0=VALUE
        Map<String, Double> data = map.entrySet().stream()
                .filter(e -> !e.getKey().split("_")[1].contains("10"))
                .collect(Collectors.toMap(e -> e.getKey(), e -> Double.valueOf(e.getValue().get(0))));

        //PEEK DESC RANK
        List<String> peekRank = rank(data, R);

        return peekRank;
    }

    private List<String> temperature(Map<String, List<String>> map) {
        if (map == null) {
            return null;
        }

        //0=VALUE
        Map<String, Double> data = map.entrySet().stream()
                .collect(Collectors.toMap(e -> e.getKey(), e -> Double.valueOf(e.getValue().get(0))));

        //PEEK DESC RANK
        List<String> peekRank = rank(data, R);

        return peekRank;
    }

    private List<String> rank(Map<String, Double> data, int r) {
        return data.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).limit(R)
                .map(e -> e.getKey()).collect(Collectors.toList());
    }

    public static void main(String[] args) {
        LOADER.setFile("PC200_form");
        SyaryoAnalizer.rejectSettings(false, false, false);
        SyaryoAnalizer s =  new SyaryoAnalizer(LOADER.getSyaryoMap().get("PC200-10-450635"), true);

        UseEvaluate use = new UseEvaluate();

        String testkey = "LOADMAP_エンジン水温VS作動油温";
        Map<String, List<String>> data = use.getdata(s);
        Map<String, Double> result = use.evaluate(testkey, s);
        
        data.entrySet().stream().forEach(d -> {
            System.out.println(d.getKey());
            System.out.println("  " + d.getValue());
            if(testkey.equals(d.getKey()))
                System.out.println("  " + result.values());
            else
                System.out.println("  " + result.get("None Evaluate"));
        });
        
        //クラスタ用データ
        System.out.println("\n"+use.header(testkey));
        System.out.println(use.getClusterData(testkey));
    }
}
