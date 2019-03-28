/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.merge;

import java.util.HashMap;
import java.util.Map;
import obj.SyaryoObject;

/**
 *
 * @author ZZ17807
 */
public class SyaryoDataMerge {
    public static Map<String, String> unikeyMerge(Map<String, SyaryoObject> map, String key, int ckey, int idx){
        Map<String, String> merge = new HashMap<>();
        map.values().parallelStream()
                        .map(s -> s.get(key))
                        .filter(m -> m != null)
                        .flatMap(m -> m.entrySet().stream())
                        .forEach(me -> merge.put(me.getValue().get(ckey)+"."+me.getKey(), me.getValue().get(idx)));
        
        return merge;
    }
}
