/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.eval;

import analizer.SyaryoAnalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import obj.SyaryoLoader;

/**
 *
 * @author ZZ17390
 */
public class UseEvaluate {

    private static SyaryoLoader LOADER = SyaryoLoader.getInstance();
    private static Map<String, List<String>> h = new HashMap<>();

    public static List<String> header(String load) {
        return h.get(load);
    }
    
    public static List<String> addHeader(String kind, List<String> load) {
        return h.put(kind, load);
    }

    public static Map<String, Map<String, Double>> nomalize(SyaryoAnalizer s, List<String> keys) {
        Map<String, Map<String, Double>> map = new LinkedHashMap<>();
        
        int idx = LOADER.index("LOADMAP_SMR", "VALUE"); //共通で利用可能
        
        //正規化時の分母
        Double smr = Double.valueOf(s.get().get("LOADMAP_SMR").values().stream().findFirst().get().get(idx));

        for (String key : keys) {
            if (s.get().get(key) == null) {
                return null;
            }

            //Header
            h.put(key, new ArrayList(s.get().get(key).keySet()));

            //初期化
            h.get(key).stream().forEach(d -> {
                if (map.get(key) == null) {
                    map.put(key, new LinkedHashMap());
                }
                map.get(key).put(d, 0d);
            });

            //正規化処理
            
            Map<String, List<String>> loadmap = s.get().get(key);
            loadmap.entrySet().stream().forEach(e -> {
                map.get(key).put(e.getKey(), Double.valueOf(e.getValue().get(idx)) / smr);
            });
        }

        return map;
    }
    
    private static Map<String, Double> eval(Map<String, List<String>> s.get().get(key)){
        
    }
}
