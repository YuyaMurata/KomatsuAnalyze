/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package creator.index;

import creator.create.TemplateCreate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import json.MapIndexToJSON;

/**
 *
 * @author zz17390
 */
public class ShuffleIndex {

    public static void main(String[] args) {
        Map shuffleMap = createShuffleMap();
        new MapIndexToJSON().write("index\\shuffle_default_format.json", shuffleMap);
    }
    
    private static Map createShuffleMap(){
        Map map = new HashMap();
        Map<String, List<String>> layoutMap = TemplateCreate.index();
        System.out.println(layoutMap);
        
        for(String key : layoutMap.keySet()){
            Map m = new HashMap();
            final int inc;
            if(layoutMap.get(key).contains("None"))
                inc = 0;
            else
                inc = 2;
            m.put("subKey_"+key, 
                layoutMap.get(key).stream()
                            .map(s -> key+"."+s+"#"+(layoutMap.get(key).indexOf(s)+inc))
                            .limit(layoutMap.get(key).size()-1)
                            .collect(Collectors.toList())
            );
            map.put(key, m);
        }
        
        return map;
    }
}
