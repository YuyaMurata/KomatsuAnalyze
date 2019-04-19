/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package db.test;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author ZZ17807
 */
public class TestGenMeta {
    public String file;
    public int total = 0;
    public Map<String, Map<String, Integer>> metaSet;
    
    public TestGenMeta(String name){
        file = name;
        metaSet = new LinkedHashMap<>();
    }
    
    public void setField(String field){
        metaSet.put(field, new HashMap<>());
    }
    
    public void setData(String field, String data){
        if(metaSet.get(field) == null)
            setField(field);
        
        data = shortFor(field, data);
        
        if(metaSet.get(field).get(data) == null)
            metaSet.get(field).put(data, 0);
        
        metaSet.get(field).put(data, metaSet.get(field).put(data, 0)+1);
    }
    
    private String shortFor(String f, String d){
        if(f.contains("inp_dayt") || f.contains("last_upd_dayt") || f.contains("time"))
            return d.substring(0, 10);
        
        return d;
    }
    
    public void mapping(){
        metaSet.put("Total", new HashMap<>());
        metaSet.get("Total").put("_", total);
    }
}
