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
public class MapNulltest {
    public static void main(String[] args) {
        Map map = new HashMap();
        map.put("test", null);
        
        System.out.println(map);
    }
}
