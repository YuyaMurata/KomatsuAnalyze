/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analyze.link;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import json.SyaryoToZip;
import obj.SyaryoObject2;

/**
 *
 * @author ZZ17390
 */
public class LinkSyaryoAnalysis {
    private static String kisy = "WA470";
    
    public static void main(String[] args) {
        String filename = "json\\syaryo_obj_" + kisy + "_form";
        Map<String, SyaryoObject2> syaryoMap = new SyaryoToZip().readObject(filename);
        
        List key = syaryoMap.keySet().stream().collect(Collectors.toList());
        for(int i=0; i< key.size()-1; i++){
            for(int j=1; j< key.size()-1; j++){
                SyaryoObject2 sourceSyaryo = syaryoMap.get(key.get(i));
                SyaryoObject2 targetSyaryo = syaryoMap.get(key.get(j));
                
                
            }
        }
    }
}
