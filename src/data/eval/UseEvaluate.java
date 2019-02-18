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
    private static List<String> h = new ArrayList<>();
    
    //未定
    public static List<String> header(){
        return h;
    }
    
    public static Map<String, Double> nomalize(SyaryoAnalizer s, String key){
        Map<String, Double> map = new LinkedHashMap<>();
        
        if(s.get().get(key) == null)
            return null;
        
        //Header
        h = new ArrayList(s.get().get(key).keySet());
        
        //初期化
        h.stream().forEach(d -> map.put(d, 0d));
        
        //正規化時の分母
        int smr_idx = LOADER.index("LOADMAP_SMR", "VALUE");
        Double smr = Double.valueOf(s.get().get("LOADMAP_SMR").values().stream().findFirst().get().get(smr_idx));
        
        //正規化処理
        int eg_idx = LOADER.index(key, "VALUE");
        Map<String, List<String>> engine = s.get().get(key);
        engine.entrySet().stream().forEach(e ->{
            map.put(e.getKey(), Double.valueOf(e.getValue().get(eg_idx))/smr);
        });
        
        return map;
    }
}
