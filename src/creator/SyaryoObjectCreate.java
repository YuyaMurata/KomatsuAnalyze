/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package creator;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import json.JsonToSyaryoTemplate;
import obj.SyaryoObject;
import obj.SyaryoTemplate;

/**
 *
 * @author ZZ17390
 */
public class SyaryoObjectCreate {
    public static void main(String[] args) {
        String path = "車両テンプレート";
        File[] flist = (new File(path)).listFiles();
        
        Map<String, SyaryoTemplate> syaryoBase = new JsonToSyaryoTemplate().reader("syaryo_history_template_syaryou.json");
        TreeMap map = new TreeMap();
        for(SyaryoTemplate syaryo : syaryoBase.values()){
            map.put(syaryo.getName(), new SyaryoObject());
        }
        
        
        for (File f : flist) {
            System.out.println(f.getName());
            Map<String, SyaryoTemplate> syaryoTemplates = new JsonToSyaryoTemplate().reader(f.getName());
            
            for(SyaryoTemplate syaryo : syaryoTemplates){
                
            }
        }
    }
}
