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
import json.JsonToSyaryoTemplate;
import obj.SyaryoObject;
import creator.template.SyaryoTemplate;

/**
 *
 * @author ZZ17390
 */
public class SyaryoTemplateCheck {
    private static final String kisy = "PC200";
    private static Map<String, SyaryoTemplate> syaryoMap;
    
    public static void main(String[] args) {
        JsonToSyaryoTemplate temp = new JsonToSyaryoTemplate();
        syaryoMap = temp.reader2("中間データ\\syaryo_mid_"+kisy+".zip");
        
        count();
        
        //Check 1:
        //randomSampling(10, syaryoMap);
    }
    
    public static void count(){
        List<String> typs = syaryoMap.values().stream()
                                        .map(s -> s.getName().split("-")[1])
                                        .distinct()
                                        .collect(Collectors.toList());
        
        for(String typ : typs){
            long cnt = 0L;//syaryoMap.keySet().stream().filter(s -> s.split("-")[1].contains(typ)).filter(s -> syaryoMap.get(s).getKomtrax()).count();
            for(SyaryoTemplate syaryo : syaryoMap.values()){
                if(!syaryo.getName().split("-")[1].equals(typ) || syaryo.get("KMSMR")==null) continue;
                if(syaryo.get("KMSMR").contains("komtrax")){
                    cnt = cnt + 1;
                }
            }
            
            System.out.println(typ+"="+cnt);
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
