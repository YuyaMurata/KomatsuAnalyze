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
 * @author zz17390
 */
public class MapPutNullTest {
    public static void main(String[] args) {
        Map map = new HashMap();
        
        map.put("A", 1);
        map.put("B", null);
        map.put("C", 3);
        
        System.out.println(map);
    }
}
