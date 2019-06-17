/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.util.Arrays;

/**
 *
 * @author ZZ17390
 */
public class ContainsTest {
    public static void main(String[] args) {
        String t = " ";
        String[] str = new String[]{"8", "8N1", ""};
        String[] str2 = new String[]{" "};
        
        System.out.println(Arrays.asList(str).contains(t));
        System.out.println(Arrays.asList(str2).contains(t));
    }
}
