/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author ZZ17390
 */
public class MapTest {
    public static void main(String[] args) {
        Map updateMap = new HashMap();
        List list = new ArrayList();
        list.add("A");list.add("B");list.add("C");
        Map temp = new HashMap();
        temp.put("ID", "ABC");
        temp.put("Name", "DEF");
        temp.put("list", list);
        updateMap.put(temp.get("ID"), temp.get("list"));
        
        list.set(0, "X");
        //temp = new HashMap();
        temp.put("ID", "XYZ");
        temp.put("Name", "XYZ");
        temp.put("list", list);
        updateMap.put(temp.get("ID"), temp.get("list"));
        
        System.out.println(updateMap);
    }
}
