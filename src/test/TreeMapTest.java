/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.util.TreeMap;

/**
 *
 * @author ZZ17807
 */
public class TreeMapTest {
    public static void main(String[] args) {
        TreeMap map = new TreeMap();
        map.put(1, "a");
        map.put(10, "b");
        map.put(100, "c");
        
        System.out.println(map.floorEntry(1));
        System.out.println(map.floorEntry(2));
        System.out.println(map.floorEntry(1001));
    }
}
