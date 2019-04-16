/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package param.obj;

import file.ListToCSV;
import file.MapToJSON;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import param.KomatsuUserParameter;

/**
 *
 * @author ZZ17807
 */
public class UserPartsObject {
    private static String PATH = KomatsuUserParameter.AZ_PATH;
    public Map<String, Map<String[], String>> index;

    public UserPartsObject(String file) {
        index = createObject(file);
    }
    
    private Map createObject(String file){
        Map<String, Map<String[], String>> map = new HashMap<>();
        Map<String, String> fmap = new MapToJSON().toMap(file);
        
        //Create Map
        fmap.entrySet().stream().forEach(f ->{
            String defname = f.getKey();
            List<String> csv = ListToCSV.toList(PATH+f.getValue());
            List<String> h = Arrays.asList(csv.get(0).split(","));
            csv.remove(0);
            
            //key-value
            csv.stream().map(l -> l.split(",")).forEach(s ->{
                String sid = s[h.indexOf("SID")];
                
                String[] key = new String[]{
                            s[h.indexOf("部品.会社")],
                            s[h.indexOf("部品.作番")].split("#")[0],
                            s[h.indexOf("部品.品番")],
                            s[h.indexOf("部品.品名")]};
                
                if(map.get(sid) == null)
                    map.put(sid, new HashMap<>());
                
                map.get(sid).put(key, defname);
            });
        });
        
        return map;
    }
    
    public Boolean check(String sid){
        return index.get(sid) != null;
    }
    
    public String defineName(String sid, String[] key){
        return index.get(sid).get(key);
    }
}
