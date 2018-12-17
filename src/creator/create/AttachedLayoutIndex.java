/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package creator.create;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import file.MapToJSON;
import file.SyaryoToCompress;
import obj.LoadSyaryoObject;
import obj.SyaryoObject4;
import param.KomatsuDataParameter;

/**
 *
 * @author kaeru_yuya
 */
public class AttachedLayoutIndex {
    private static String LAYOUT_INDEX = KomatsuDataParameter.LAYOUT_FORMAT_PATH;
    public static void main(String[] args) {
    }
    
    public static void attached(String kisy){
        File layoutfile = new File(LAYOUT_INDEX);
        if(!layoutfile.exists()){
            System.out.println("not find "+LAYOUT_INDEX);
            return;
        }
        
        Map<String, SyaryoObject4> map = LoadSyaryoObject.load("PC200.bz2");
        
        SyaryoObject4 header = new SyaryoObject4("_header");
        Map<String, List> layout = new MapToJSON().reader(LAYOUT_INDEX);
        Map<String, Map> formatter = new HashMap<>();
        for(String key : layout.keySet()){
            Map data = new HashMap();
            data.put(key, layout.get(key));
            formatter.put(key, data);
        }
        
        header.putAll(formatter);
        map.put("_header", header);
        
        new SyaryoToCompress().write("syaryo\\syaryo_obj_PC200.bz2", map);
        System.out.println("Attached SyaryoData Index!");
    }
}
