/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.util.Arrays;
import java.util.List;

/**
 *
 * @author ZZ17390
 */
public class StringCompareTest {
    public static void main(String[] args) {
        String test1 = "2017";
        String test2 = "2018";
        
        System.out.println(test2.compareTo(test1));
        
        String id = "XXXXXXXXXXXX";
        List<String> s = Arrays.asList(new String[]{"XXX"});
        System.out.println(id.contains("-"));
    }
}
