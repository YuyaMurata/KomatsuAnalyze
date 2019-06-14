/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package axg.obj;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * @author ZZ17807
 */
public class MSyaryoObject {
    String name;
    Map<String, Map<String, List<String>>> map;
    Map<String, Integer> count;

    public MSyaryoObject(Map map) {
        this.name = (String) map.get("name");
        this.map = (Map<String, Map<String, List<String>>>) map.get("map");
        this.count = (Map<String, Integer>) map.get("count");
    }
    
    public Map<String, List<String>> get(String key){
        return this.map.get(key);
    }
    
    public void removeAll(String key, List<String> subKeys){
        subKeys.stream().forEach(sk -> this.map.get(key).remove(sk));
        if(this.map.get(key).isEmpty())
            this.map.remove(key);
    }
    
    public void recalc(){
        this.count = this.map.entrySet().stream()
                            .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue().size()));
    }
    
    public void print(){
        System.out.println(name);
        System.out.println("map-"+map);
        System.out.println("cnt-"+count);
    }
}
