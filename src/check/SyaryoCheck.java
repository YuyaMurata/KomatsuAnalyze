/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package check;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import json.JsonToSyaryoObj;
import obj.SyaryoObject;
import obj.SyaryoTemplate;

/**
 *
 * @author ZZ17390
 */
public class SyaryoCheck {
    private static final String kisy = "PC200";
    private static Map<String, SyaryoObject> syaryoMap;
    
    public static void main(String[] args) {
        JsonToSyaryoObj obj = new JsonToSyaryoObj();
        syaryoMap = obj.reader("syaryo_obj_"+kisy+"_form.json");
        System.out.println(syaryoMap.size());
        
        //count();
        smr("PC200-10-450972", syaryoMap);
        
        //nullCheck();
        
        //Check 1:
        //randomSampling(10, syaryoMap);
    }
    
    public static void count(){
        List<String> typs = syaryoMap.values().stream()
                                        .map(s -> s.getType())
                                        .distinct()
                                        .collect(Collectors.toList());

        for(String typ : typs){
            long cnt = 0L;//syaryoMap.keySet().stream().filter(s -> s.split("-")[1].contains(typ)).filter(s -> syaryoMap.get(s).getKomtrax()).count();
            for(SyaryoObject syaryo : syaryoMap.values()){
                if(!syaryo.getType().equals(typ)) continue;
                for(List smr : syaryo.getSMR().values()){
                    if(smr.get(1).toString().contains("komtrax")){
                        cnt = cnt + 1;
                        if(typ.equals("8"))
                            System.out.println(cnt+" - "+syaryo.getName());
                        break;
                    }
                }
            }
            
            System.out.println(typ+"="+cnt);
        }
    }
    
    public static void smr(String name, Map<String, SyaryoObject> map){
        map.get(name).getSMR().entrySet().stream().forEach(e -> System.out.println(e.getKey()+":"+e.getValue()));
    }
    
    public static void nullCheck(){
        for(SyaryoObject syaryo : syaryoMap.values()){
            if(syaryo.getNew() == null){
                System.out.println(syaryo.getName());
            }
        }
    }
    
    public static void randomSampling(int size, Map<String, SyaryoTemplate> map){
        Random rand = new Random();
        List<String> sampling = rand.ints(0, map.size())
                                    .distinct()
                                    .limit(size)
                                    .mapToObj(new ArrayList<String>(map.keySet())::get)
                                    .collect(Collectors.toList());
        
        //File[] flist = (new File(path)).listFiles();
        System.out.println("sampling:"+sampling);
    }
}
