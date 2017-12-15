/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package check;

import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import json.JsonToSyaryoObj;
import json.SyaryoObjToJson;
import obj.SyaryoObject;

/**
 *
 * @author ZZ17390
 */
public class SyaryoFiltering {
    private static final String kisy = "WA470";
    private static Map<String, SyaryoObject> syaryoMap;
    
    public static void main(String[] args) {
        JsonToSyaryoObj obj = new JsonToSyaryoObj();
        SyaryoObjToJson json = new SyaryoObjToJson();
        syaryoMap = obj.reader("json\\syaryo_obj_" + kisy + "_form.json");
        System.out.println(syaryoMap.size());
        
        TreeMap map1 = new TreeMap();
        TreeMap map2 = new TreeMap();
        for(SyaryoObject syaryo : syaryoMap.values()){
            if(syaryo.getType().equals("8"))
                map1.put(syaryo.getName(), syaryo);
            else
                map2.put(syaryo.getName(), syaryo);
        }
        
        json.write("json\\syaryo_obj_"+kisy+"_8.json", map1);
        System.out.println("type-8:"+map1.size());
        json.write("json\\syaryo_obj_"+kisy+"_7.json", map2);
        System.out.println("type-7:"+map2.size());
    }
}
