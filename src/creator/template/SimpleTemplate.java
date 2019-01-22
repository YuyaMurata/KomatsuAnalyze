/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package creator.template;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author ZZ17390
 */
public class SimpleTemplate {
    public List<String> name = new ArrayList<>();
    public Map temp = new HashMap();
    
    public static transient Map<String, String> validate = new HashMap();

    public SimpleTemplate(String k, String t, String s, String b) {
        this.name.add(k);
        this.name.add(t);
        this.name.add(s);
        this.name.add(b);
        setting();
    }
    
    public SimpleTemplate(List id) {
        this.name.addAll(id);
    }
    
    public SimpleTemplate(String id) {
        this.name.add(id);
    }
    
    public void add(String key, String content){
        if(temp.get(key) != null)
            content = temp.get(key)+"\n"+content;
        temp.put(key, content);
    }
    
    public String getName(){
        return name.get(0)+"-"+name.get(1)+name.get(2)+"-"+name.get(3);
    }
    
    public String getShortName(){
        return name.get(0)+"-"+name.get(3);
    }
    
    public static String check(String kisy, String kiban){
        return validate.get(kisy+"-"+kiban);
    }
    
    public static void removeValidate(){
        validate = new HashMap();
    }
    
    public void setting(){
        validate.put(getShortName(), getName());
    }
}
