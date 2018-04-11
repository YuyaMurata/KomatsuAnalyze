/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package creator.index;

import java.util.List;
import java.util.TreeMap;

/**
 *
 * @author ZZ17390
 */
public class LayoutIndex {
    public static void main(String[] args) {
        TreeMap index = new TreeMap();
        
        
    }
}

class Layout{
    public String table;
    public List<String> fields;
    
    public Layout(String table){
        this.table = table;
    }
    
    public void add(String field){
        fields.add(field);
    }
}
