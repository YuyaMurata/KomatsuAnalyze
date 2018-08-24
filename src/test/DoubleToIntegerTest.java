/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

/**
 *
 * @author ZZ17390
 */
public class DoubleToIntegerTest {
    public static void main(String[] args) {
        String s = "-000000200.";
        String s2 = "+000000200.";
        
        System.out.println(Double.valueOf(s).intValue());
        System.out.println(Double.valueOf(s2).intValue());
    }
}
