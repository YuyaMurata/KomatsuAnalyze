/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.filter;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * @author ZZ17390
 */
public class DataFilter {
    //文字フィルタ
    public static Map simpleFilter(Map<String, List> dataMap, Integer index, List filterList, Boolean flg){
        if(dataMap == null)
            return null;
        
        Map map = dataMap.entrySet().stream()
                                .filter(d -> index < 0?filterList.contains(d.getKey())==flg:filterList.contains(d.getValue().get(index))==flg)
                                .collect(Collectors.toMap(d -> d.getKey(), d -> d.getValue()));
        
        return map;
    }
    
    //装置コードのフィルタ
    public static Map deviceFilter(Map<String, List> dataMap, Integer index, List<String> filterList, Boolean flg){
        if(dataMap == null)
            return null;
        
        List deviceList = filterList.stream().map(s -> s.substring(0, 4)).collect(Collectors.toList());
        
        Map map = dataMap.entrySet().stream()
                                .filter(d -> deviceList.contains(d.getValue().get(index).toString().substring(0, 4))==flg)
                                .collect(Collectors.toMap(d -> d.getKey(), d -> d.getValue()));
        
        return map;
    }
    
    //ANDフィルタ
    public static Map dataFilter(Map<String, List> dataMap1, Integer idx1, Map<String, List> dataMap2, Integer idx2, Boolean deviceflg){
        if(dataMap1 == null || dataMap2 == null)
            return null;
        
        List data1FilterList = dataMap1.values().stream().map(l -> l.get(idx1)).collect(Collectors.toList());
        if(deviceflg)
            return deviceFilter(dataMap2, idx2, data1FilterList, true);
        else
            return simpleFilter(dataMap2, idx2, data1FilterList, true);
    }
}
