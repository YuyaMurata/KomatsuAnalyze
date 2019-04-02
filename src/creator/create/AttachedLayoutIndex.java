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
import obj.SyaryoLoader;
import obj.SyaryoObject;
import param.KomatsuDataParameter;

/**
 *
 * @author kaeru_yuya
 */
public class AttachedLayoutIndex {
    private static SyaryoLoader LOADER = SyaryoLoader.getInstance();
    private static String LAYOUT_INDEX = KomatsuDataParameter.LAYOUT_FORMAT_PATH;
    private static String KISY ="PC200";
    
    public static void main(String[] args) {
        File layoutfile = new File(LAYOUT_INDEX);
        if(!layoutfile.exists()){
            System.out.println("not find "+LAYOUT_INDEX);
            return;
        }
        
        LOADER.setFile(KISY);
        
        LOADER.updateHeader(createHeader());
        
        LOADER.close();
        
        new SyaryoToCompress().write(LOADER.getFilePath(), LOADER.getSyaryoMapWithHeader());
        
        System.out.println("Attached SyaryoData Index!");
    }
    
    public static SyaryoObject createHeader(){
        SyaryoObject header = new SyaryoObject("_header");
        Map<String, List> layout = new MapToJSON().toMap(LAYOUT_INDEX);
        Map<String, Map> formatter = new HashMap<>();
        
        for(String key : layout.keySet()){
            Map data = new HashMap();
            data.put(key, layout.get(key));
            formatter.put(key, data);
        }
        
        header.putAll(formatter);
        
        return header;
    }
}
