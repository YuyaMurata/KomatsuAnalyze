/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.TreeMap;
import json.JsonToSyaryoObj;

import obj.SyaryoObject;

/**
 *
 * @author murata
 */
public class TestMachinePC200 {
    static String csv = "PC200_nu_time.csv";
    public static void main(String[] args) throws IOException {
        String fileName = "syaryo_history_pc200.json";
        Map<String, SyaryoObject> syaryoMap = new JsonToSyaryoObj().reader(fileName);
        
        PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(csv)));
        PrintWriter pw2 = new PrintWriter(new BufferedWriter(new FileWriter("all_"+csv)));
        
        TreeMap<Integer, Integer> map1 = new TreeMap();
        TreeMap<Integer, Integer> map2 = new TreeMap();
        for(String name : syaryoMap.keySet()){
            //System.out.println(name);
            SyaryoObject syaryo = syaryoMap.get(name);
            String year1 = null;
            if(!syaryo.getHistory().isEmpty()){
                TreeMap history = new TreeMap(syaryo.getHistory());
                history.remove("");
                year1 = history.firstKey().toString().substring(0, 4);
                
                String year2 = history.lastKey().toString().substring(0, 4);
                Integer year = Integer.valueOf(year2)-Integer.valueOf(year1);
                
                if(map2.get(year) == null) map2.put(year, 0);
                map2.put(year, (int)map2.get(year)+1);
                
                /*if(year > 90)
                    System.out.println("History, "+name);
                
                if(year < 0)
                    System.out.println("History, "+name);
                */
            }
            
            
            if(!syaryo.getNew().isEmpty())
                year1 = syaryo.getNew().substring(0, 4);
            else
                continue;
            
            if(!syaryo.getUsed().isEmpty()){
                TreeMap used = new TreeMap(syaryo.getUsed());
                String year2 = used.firstKey().toString().substring(0, 4);
                Integer year = Integer.valueOf(year2)-Integer.valueOf(year1);
                
                if(year == 0){
                    TreeMap history = new TreeMap(syaryo.getHistory());
                    if(!history.firstEntry().getValue().equals("1D"))
                        System.out.println("unexp.Used,"+syaryo.getName());
                }
                
                if(map1.get(year) == null) map1.put(year, 0);
                map1.put(year, (int)map1.get(year)+1);
            }else if(!syaryo.getDead().isEmpty()){
                String year2 = syaryo.getDead().toString().substring(0, 4);
                Integer year = Integer.valueOf(year2)-Integer.valueOf(year1);
                
                if(year == 0){
                    TreeMap history = new TreeMap(syaryo.getHistory());
                    if(!history.firstEntry().getValue().equals("9A"))
                        System.out.println("unexp.Dead,"+syaryo.getName());
                }
                    
                if(map1.get(year) == null) map1.put(year, 0);
                map1.put(year, (int)map1.get(year)+1);
            }
        }
        
        
        for(Integer year : map1.keySet()){
            pw.println(year+","+map1.get(year));
        }
        pw.close();
        
        for(Integer year : map2.keySet()){
            pw2.println(year+","+map2.get(year));
        }
        pw2.close();
    }
}
