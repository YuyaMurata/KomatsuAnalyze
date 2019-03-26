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
import java.util.TreeMap;
import java.util.stream.Collectors;
import obj.SyaryoLoader;

/**
 *
 * @author ZZ17390
 */
public class UseEvaluate {

    private static SyaryoLoader LOADER = SyaryoLoader.getInstance();
    private static Map<String, List<String>> _header = new HashMap<>();

    public static List<String> header(String load) {
        return _header.get(load);
    }

    public static void addHeader(String kind, List<String> load) {
        if(_header.get(kind) == null){
            _header.put(kind, load);
            System.out.println(_header);
        }
    }

    public static Map<String, Map<String, Double>> nomalize(SyaryoAnalizer s, List<String> keys) {
        Map<String, Map<String, Double>> map = new LinkedHashMap<>();

        for (String key : keys) {
            if(key.contains("実エンジン回転")){
                map.put(key, evalEngine(key, s.get().get(key), LOADER.index(key, "VALUE")));     
            }
        }

        return map;
    }
    
    private static Map<String, Double> evalEngine(String key, Map<String, List<String>> loadmap, int idx){
        //トルク10の値を削除
        Map<String, Double> data = loadmap.entrySet().stream()
                                        .filter(e -> !e.getKey().split("_")[1].contains("10"))
                                        .collect(Collectors.toMap(e -> e.getKey(), e -> Double.valueOf(e.getValue().get(idx))));
        
        Double peek = data.values().stream().mapToDouble(d -> d).max().getAsDouble();
        
        //正規化
        data = data.entrySet().stream().collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()/peek, (e1, e2) -> e1, TreeMap::new));
        
        addHeader(key, new ArrayList<>(data.keySet()));
        
        return data;
    }
}
