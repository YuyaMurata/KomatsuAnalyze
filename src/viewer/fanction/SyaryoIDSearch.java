/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package viewer.fanction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import obj.SyaryoObject;

/**
 *
 * @author kaeru
 */
public class SyaryoIDSearch {
    public static List<String> search(String select, String word, Map<String, SyaryoObject> map){
        if(word.equals("") || !word.contains(":"))
            return new ArrayList<>(map.keySet());
        
        word = word.replace(" ", "");
        String kind = word.split(":")[0];
        String searchwords = word.split(":")[1];
        
        switch(kind){
            case "type": return type(searchwords, map);
            case "no" : return no(searchwords, map);
            case "txt" : return txt(select, searchwords, map);
            default : return new ArrayList<>(map.keySet());
        }
    }
    
    private static List<String> type(String tw, Map<String, SyaryoObject> map){
        List<String> twl = Arrays.asList(tw.split(","));
        return map.keySet().parallelStream()
                        .filter(id -> id.contains("-")?twl.contains(id.split("-")[1]):true)
                        .sorted()
                        .collect(Collectors.toList());
    }
    
    private static List<String> no(String nw, Map<String, SyaryoObject> map){
        List<String> nwl = Arrays.asList(nw.replace(" ", "").split(","));
        return map.keySet().parallelStream()
                        .filter(id -> id.contains("-")?nwl.contains(id.split("-")[2]):true)
                        .sorted()
                        .collect(Collectors.toList());
    }
    
    private static List<String> txt(String select, String nw,  Map<String, SyaryoObject> map){
        List<String> nwl = Arrays.asList(nw.replace(" ", "").split(","));
        return map.values().parallelStream()
                        .filter(e -> !select.equals("ALL") && e.get(select) != null)
                        .filter(e -> e.get(select).values().stream().filter(data -> nwl.stream().filter(w -> data.toString().contains(w)).findFirst().isPresent()).findFirst().isPresent())
                        .map(e -> e.name)
                        .sorted()
                        .collect(Collectors.toList());
    }
}
