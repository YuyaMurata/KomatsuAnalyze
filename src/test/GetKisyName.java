/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import creator.template.SyaryoTemplate;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import json.JsonToSyaryoTemplate;

/**
 *
 * @author ZZ17390
 */
public class GetKisyName {
    public static void main(String[] args) {
        File[] flist = (new File("E:\\vmshare\\komtrax")).listFiles();
        
        Map kisy = new TreeMap();
        
        for(File f : flist){
            if(f.getName().contains("komtrax"))
                continue;
            kisy.put(f.getName(), 1);
        }
        
        Map<String, SyaryoTemplate> json = new JsonToSyaryoTemplate().reader("index\\syaryo_index.json");
        List indexkisy = json.values().stream().map(temp -> temp.kisy).distinct().collect(Collectors.toList());
        
        for(Object k : indexkisy){
            if(kisy.get(k) == null)
                kisy.put(k, 0);
        }
        
        for(Object k : kisy.keySet())
            System.out.println(k+","+kisy.get(k));
    }
}
