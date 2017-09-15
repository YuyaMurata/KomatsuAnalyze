/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.stream.Collectors;
import json.JsonToSyaryoObj;
import obj.SyaryoObject;

/**
 *
 * @author ZZ17390
 */
public class ExtractSyaryoData {
    private static final String FILENAME = "syaryo_history_pc200.json";
    
    public static void main(String[] args) throws IOException {
        JsonToSyaryoObj json = new JsonToSyaryoObj();
        Map<String, SyaryoObject> syaryoMap = json.reader(FILENAME);
        
        SyaryoObject syaryo = syaryoMap.get("PC200-300226");
        
        TreeMap map = new TreeMap(syaryo.getSMR());
        for(Object date : map.keySet())
            System.out.println(date+","+map.get(date));
        
        System.out.println("");
        
        map = new TreeMap(syaryo.getHistory());
        for(Object date : map.keySet())
            System.out.println(date+","+map.get(date));
    }
}
