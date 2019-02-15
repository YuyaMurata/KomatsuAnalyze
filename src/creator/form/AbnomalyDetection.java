/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package creator.form;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import obj.SyaryoLoader;
import obj.SyaryoObject;

/**
 *
 * @author ZZ17390
 */
public class AbnomalyDetection {
    private static SyaryoLoader LOADER = SyaryoLoader.getInstance();
    
    public static Map<String, List> smr(Map<String, SyaryoObject> syaryoMap, String key){
        int idx = LOADER.index(key, "VALUE");
        Map<String, List> detect = new TreeMap<>();
        
        for (String sid : syaryoMap.keySet()) {
            Map<String, List<String>> smr = syaryoMap.get(sid).get(key);
            
            //存在しなかったら無視
            if(smr == null)
                continue;
            
            Integer temp = 0;
            for(String date : smr.keySet()){
                Integer v = Integer.valueOf(smr.get(date).get(idx));
                if(temp <= v) //SMR下降検出
                    temp = v;
                else{
                    if(detect.get(sid) == null)
                        detect.put(sid, new ArrayList());
                    detect.get(sid).add(date);
                }
            }
        }
        
        System.out.println("Detection SIDs : "+detect.keySet());
        
        return detect;
    }
    
    public static Map<String, String> nonActive(Map<String, SyaryoObject> syaryoMap, String key, String date){
        Map<String, String> detect = new TreeMap<>();
        
        for (String sid : syaryoMap.keySet()) {
            Map<String, List<String>> data = syaryoMap.get(sid).get(key);
            
            //存在しなかったら無視
            if(data == null){
                detect.put(sid, "None");
                continue;
            }
            
            List<String> l = new ArrayList<>(data.keySet());
            String last = l.get(l.size()-1).substring(0, date.length());
            
            if(Integer.valueOf(last) < Integer.valueOf(date)){
                detect.put(key, last);
            }
        }
        
        System.out.println("Detection SIDs : "+detect.keySet());
        
        return detect;
    }
}
