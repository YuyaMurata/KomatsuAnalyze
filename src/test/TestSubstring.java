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
public class TestSubstring {
    public static void main(String[] args) {
        String s = "123456";
        System.out.println(s);
        System.out.println(s.substring(0, 5));
        
        String f = "syaryo_mid_PC200_経歴.bz2";
        String d = f.split("_").length < 5?f.split("_")[3].replace(".bz2", ""):f.split("_")[3]+"_"+f.split("_")[4].replace(".bz2", "");
        System.out.println(d);
        
    }
}
