/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ZZ17390
 */
public class StringContainsTestinList {
    public static void main(String[] args) {
        List<String> list = new ArrayList<>();
        list.add("ABC");list.add("A"); list.add("B"); list.add("C");
        
        System.out.println(list.stream().filter(l ->l.contains("A")).count());
        
        List<String> list2 = new ArrayList<>();
        list2.add("ABC");list2.add("A"); list2.add("B"); list2.add("C");
        
        List<List<String>> listAll = new ArrayList<>();
        listAll.add(list); listAll.add(list2);
        
        System.out.println(list.stream().filter(l -> l.contains("A")).count());
        
    }
}
