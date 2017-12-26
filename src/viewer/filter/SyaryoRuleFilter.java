/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package viewer.filter;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author ZZ17390
 */
public class SyaryoRuleFilter {
    private final Map origin;
    private Map clone;
    private Map<String, String[]> map;
    
    public SyaryoRuleFilter(Map map){
        this.origin = map;
    }
    
    public void setRule(String condition){
        map = new HashMap();
        
        String rule = "";
        if(condition.contains(">="))
            rule = ">=";
        else if(condition.contains("<="))
            rule = "<=";
        else if(condition.contains("!="))
            rule = "!=";
        else if(condition.contains("="))
            rule = "=";
        else if(condition.contains("<"))
            rule = "<";
        else if(condition.contains(">"))
            rule = ">";
        
        map.put(condition.split(rule)[0], new String[]{rule, condition.split(rule)[1]});
    }
    
    public Boolean getRule(String field, String value){
        if(map.get(field) == null)
            return true;
        
        String[] rule = map.get(field);
        
        if(rule[0].equals(">="))
            return rule[1].compareTo(value) <= 0;
        else if(rule[0].equals("<="))
            return rule[1].compareTo(value) >= 0;
        else if(rule[0].equals("!="))
            return rule[1].compareTo(value) != 0;
        else if(rule[0].equals("="))
            return rule[1].compareTo(value) == 0;
        else if(rule[0].equals("<"))
            return rule[1].compareTo(value) > 0;
        else if(rule[0].equals(">"))
            return rule[1].compareTo(value) < 0;
        else
            return null;
    }
}