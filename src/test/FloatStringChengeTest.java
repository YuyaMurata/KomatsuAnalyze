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
public class FloatStringChengeTest {
    public static void main(String[] args) {
        String s = "+0001234.8";
        Float f = Float.valueOf(s);
        System.out.println(f.intValue());
    }
}
