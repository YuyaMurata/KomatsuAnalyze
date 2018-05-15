/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package obj;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author ZZ17390
 */
public class SyaryoObject implements Serializable{
    public String name;
    public transient Map<String, Map> map = new LinkedHashMap();
    public byte[] mapData;
    
    public SyaryoObject(String name) {
        this.name = name;
    }
    
    //Update
    public SyaryoObject(String name, Map map) {
        this.name = name;
        this.map.putAll(map);
    }
    
    
    //Get
    public Map<String, List<String>> get(String table){
        return this.map.get(table);
    }
    
    //Add
    public void add(String table, String key, List value){
        Map v = this.map.get(table);
        if(v == null)
            v = new HashMap();
        v.put(key, value);
        this.map.put(key, v);
    }
    
    
}
