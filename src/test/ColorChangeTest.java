/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.util.Random;

/**
 *
 * @author ZZ17390
 */
public class ColorChangeTest {
    public static void main(String[] args) {
        String color = "#";
        Random rand = new Random();
        
        Integer r = rand.nextInt(256);
        Integer g = rand.nextInt(256);
        Integer b = rand.nextInt(256);
        
        color += Integer.toHexString(r)+Integer.toHexString(g)+Integer.toHexString(b);
        
        System.out.println(color);
    }
}
