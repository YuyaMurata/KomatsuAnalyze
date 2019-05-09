/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.filter;

import java.util.Map;
import java.util.stream.Collectors;
import obj.SyaryoObject;

/**
 *
 * @author ZZ17807
 */
public class SyaryoFilter {
    public static Map<String, SyaryoObject> evaluateFilter(Map<String, SyaryoObject> map){
        //負荷マップと受注情報が存在する車両
        //LOADMAP_SMR && 受注
        Map<String, SyaryoObject> exists = map.entrySet().parallelStream()
                                                .filter(e -> e.getValue().get("LOADMAP_SMR") != null)
                                                .filter(e -> e.getValue().get("受注") != null)
                                                .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
        
        return exists;
    }
}
