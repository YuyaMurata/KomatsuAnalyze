/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package viewer.fanction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javafx.scene.control.ToggleButton;
import obj.SyaryoObject;

/**
 *
 * @author kaeru
 */
public class SyaryoIDFilter {
    //Curent Filter
    private static Map<String, Integer> currentMap;
    private static Map<String, Integer> enableFilterMap;
    private static Map<String, Integer> disableFilterMap;
    private static Integer cnt;
    private static Integer total = 0;
    
    public static void filtering(String filter, Map<String, SyaryoObject> map) {
        if(total == 0)
            totalCalc(map);
        
        if (filter.equals("ALL"))
            all(map);
        else
            data(filter, map);
    }
    
    private static void totalCalc(Map<String, SyaryoObject> map){
        total = map.values().parallelStream()
                    .mapToInt(s -> s.getMap().values().stream().mapToInt(v -> v.size()).sum())
                    .sum();
        
        System.out.println("total="+total);
    }
    
    private static void all(Map<String, SyaryoObject> map){
        enableFilterMap = map.entrySet().parallelStream()
                                .collect(Collectors.toMap(e -> e.getKey(), e -> 0));
        disableFilterMap = new HashMap<>();
        cnt = total;
    }
    
    private static void data(String filter, Map<String, SyaryoObject> map){
        enableFilterMap = map.entrySet().parallelStream()
                                        .filter(e -> e.getValue().get(filter) != null)
                                        .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue().get(filter).size()));
        disableFilterMap = map.keySet().parallelStream()
                                        .filter(k -> enableFilterMap.get(k) == null)
                                        .collect(Collectors.toMap(k -> k, k -> 0));
        cnt = enableFilterMap.values().stream().mapToInt(v -> v).sum();
    }
    
    public static Map getCurrent(Boolean enable, ToggleButton btn){
        if (enable) {
            currentMap = enableFilterMap;
            btn.setText("EN");
        } else {
            currentMap = disableFilterMap;
            btn.setText("DS");
        }
        
        return currentMap;
    }
    
    public static List<String> getCurrentList(Boolean enable, ToggleButton btn){
        getCurrent(enable, btn);
        return currentMap.keySet().stream()
                                    .sorted()
                                    .collect(Collectors.toList());
    }
    
    public static String getAmount(Boolean enable){
        if (enable) {
            return "データ合計 : "+cnt+" 件 / "+total;
        } else {
            return "データ合計 : "+0+" 件 / "+total;
        }
    }
}
