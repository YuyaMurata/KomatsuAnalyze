/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import json.SyaryoToZip;
import obj.SyaryoObject2;

/**
 *
 * @author ZZ17390
 */
public class TreeMapSortTest {
    public static void main(String[] args) {
        String filename = "json\\syaryo_obj_PC138US_form";
        Map<String, SyaryoObject2> syaryoMap = new SyaryoToZip().readObject(filename);
        
        SyaryoObject2 syaryo = syaryoMap.values().stream().findAny().get();
        syaryo.decompress();
        List date1 = syaryo.getSMR().keySet().stream().collect(Collectors.toList());
        TreeMap treesmr = new TreeMap(syaryo.getSMR());
        
        for(Object date : date1){
            System.out.println(date);
        }
        System.out.println("last:"+treesmr.lastKey());
    }
}
