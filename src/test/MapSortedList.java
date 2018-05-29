/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * @author zz17390
 */
public class MapSortedList {
    public static void main(String[] args) {
        Map map = new HashMap();
        
        map.put("A", 1);
        map.put("B", 3);
        map.put("C", 4);
        map.put("D", 2);
        map.put("E", 1);
        
        List list;
        list = (List) map.entrySet().stream()
                        .sorted(Map.Entry.comparingByValue())
                        .map(s -> ((Map.Entry)s).getKey())
                        .collect(Collectors.toList());
        System.out.println(list);
        
        list = (List) map.entrySet().stream()
                        .sorted(Map.Entry.comparingByValue().reversed())
                        .map(s -> ((Map.Entry)s).getKey())
                        .collect(Collectors.toList());
        System.out.println(list);
    }
}
