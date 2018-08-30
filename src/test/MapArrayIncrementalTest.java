/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author ZZ17390
 */
public class MapArrayIncrementalTest {
    public static void main(String[] args) {
        Map<String, Integer[]> idr = new HashMap();
        idr.put("A", new Integer[10]);
        idr.get("A")[1] = 0;
        
        for(int i=0; i < 10; i++)
            idr.get("A")[1]++;
        
        System.out.println(idr.get("A")[1]);
    }
}
