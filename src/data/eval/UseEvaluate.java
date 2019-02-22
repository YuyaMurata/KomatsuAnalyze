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

    public static List<String> addHeader(String kind, List<String> load) {
        return _header.put(kind, load);
    }

    public static Map<String, Map<String, Double>> nomalize(SyaryoAnalizer s, List<String> keys) {
        Map<String, Map<String, Double>> map = new LinkedHashMap<>();

        int idx = LOADER.index("LOADMAP_SMR", "VALUE"); //共通で利用可能

        for (String key : keys) {
            map.put(key, eval(key, s.get().get(key), s.get().get("LOADMAP_SMR"), idx));
        }

        return map;
    }

    private static Map<String, Double> eval(String key, Map<String, List<String>> loadmap, Map<String, List<String>> denom, int idx) {
        if (loadmap == null) {
            return null;
        }
        
        //正規化時の分母
        Double smr = Double.valueOf(denom.values().stream().findFirst().get().get(idx));

        //Header
        if (_header.get(key) == null) {
            _header.put(key, new ArrayList(loadmap.keySet()));
        }

        //初期化
        Map<String, Double> map = _header.get(key).stream().collect(Collectors.toMap(d -> d, d -> 0d));

        //正規化処理
        loadmap.entrySet().stream().forEach(e -> {
            map.put(e.getKey(), Double.valueOf(e.getValue().get(idx)) / smr);
        });
        
        return map;
    }
}
