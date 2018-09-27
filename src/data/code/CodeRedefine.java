/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.code;

/**
 *
 * @author ZZ17390
 */
public class CodeRedefine {
    /**
     * 主要部品のみ (品番体系で解釈可能)
     * 123-45-6789 -> 1045
     * @param origin 
     */
    public static String partsCDRedefine(String origin){
        String define = "";
        if(origin.split("-").length < 3)
            return null;
        
        define = define + origin.charAt(0);
        
        
        return define;
    }
}
