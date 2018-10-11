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
 * @author kaeru_yuya
 */
public class CompareList {
    public static void main(String[] args) {
        Map map = new HashMap();
        
        List a1 = new ArrayList();
        List a2 = new ArrayList();
        List a3 = new ArrayList();
        
        a1.add("1");a1.add("1");a1.add("1");a1.add("0");a1.add("1");
        a2.add("1");a2.add("0");a2.add("1");a2.add("0");a2.add("1");
        a3.add("1");a3.add("1");a3.add("1");a3.add("0");a3.add("1");
        
        map.put(a1, "a1");
        map.put(a2, "a2");
        map.put(a3, "a3");
        
        System.out.println(a1+"-"+a2+" "+(a1.equals(a2)));
        System.out.println(a1+"-"+a3+" "+(a1.equals(a3)));
        
        System.out.println(map);
    }
}
