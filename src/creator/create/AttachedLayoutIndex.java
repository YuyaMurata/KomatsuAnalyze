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
import obj.SyaryoObject;
import param.KomatsuDataParameter;

/**
 *
 * @author kaeru_yuya
 */
public class AttachedLayoutIndex {
    private static String LAYOUT_INDEX = KomatsuDataParameter.LAYOUT_FORMAT_PATH;
    public static void main(String[] args) {
        attached("PC200");
    }
    
    public static void attached(String kisy){
        File layoutfile = new File(LAYOUT_INDEX);
        if(!layoutfile.exists()){
            System.out.println("not find "+LAYOUT_INDEX);
            return;
        }
        
        KomatsuDataParameter.LOADER.setFile(kisy+".bz2");
        Map<String, SyaryoObject> map = KomatsuDataParameter.LOADER.getSyaryoMap();
        
        SyaryoObject header = new SyaryoObject("_header");
        Map<String, List> layout = new MapToJSON().toMap(LAYOUT_INDEX);
        Map<String, Map> formatter = new HashMap<>();
        for(String key : layout.keySet()){
            Map data = new HashMap();
            data.put(key, layout.get(key));
            formatter.put(key, data);
        }
        
        header.putAll(formatter);
        map.put("_header", header);
        
        new SyaryoToCompress().write(KomatsuDataParameter.LOADER.getFilePath(), map);
        System.out.println("Attached SyaryoData Index!");
    }
}