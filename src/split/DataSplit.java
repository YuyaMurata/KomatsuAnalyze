/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package split;

import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import json.JsonToSyaryoObj;
import obj.SyaryoObject;

/**
 *
 * @author ZZ17390
 */
public class DataSplit {
    public static void main(String[] args) {
        String kisy = "WA470";
        String filename = "syaryo_obj_"+kisy+"_form.json";
        Map<String, SyaryoObject> syaryoMap = new JsonToSyaryoObj().reader2(filename);
        
    }
    
    public static void maintenance(Map<String, SyaryoObject> syaryoMap){
        //Map map = syaryoMap.entrySet().stream().filter(s -> s.getValue().getOrder() == null);
        
        
    }
}
