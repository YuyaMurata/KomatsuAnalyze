/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package axg.obj;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author ZZ17807
 */
public class MSyaryoObject {
    String name;
    Map<String, Map<String, List<String>>> map;
    Map<String, Integer> count;

    public MSyaryoObject(String name) {
        this.name = name;
        this.map = new LinkedHashMap<>();
        this.count = new LinkedHashMap<>();
    }
    
    private void initialize(){
        
    }
}
