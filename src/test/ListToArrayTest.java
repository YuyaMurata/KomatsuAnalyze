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
public class ListToArrayTest {
    public static void main(String[] args) {
        List list = new ArrayList();
        list.add("A");list.add("B");list.add("C");
        
        System.out.println(list);
        
        System.out.println(list.toArray(new Object[list.size()]).length);
        
        System.out.println(list.subList(0, 2).toArray(new Object[1]).length);
    }
}
