/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package filter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author ZZ17390
 */
public abstract class DataFilter {
    public Boolean sw = true;
    
    public List<String> filter(String key, List<String> data){
        if(sw)
            if(flogic(key, data))
                return data;
            else
                return null;
        else
            if(flogic(key, data))
                return null;
            else
                return data;
    }
    
    public void set(Boolean sw){
        this.sw = sw;
    }
    
    public abstract Boolean flogic(String key, List<String> data);
}
