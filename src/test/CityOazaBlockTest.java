/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author kaeru_yuya
 */
public class CityOazaBlockTest {
    String[] kansu = new String[]{"一"}; 
    
    public static void main(String[] args) {
        String address = "千葉市花見川区幕張本郷7丁目24番8";
        
        System.out.println(address);
        
        //街区
        //政令指定都市
        int idx = 0;
        if(address.contains("区"))
            idx = address.indexOf("区") + 1;
        else
            idx = address.indexOf("市") + 1;
        
        String city = address.substring(0, idx);
        address = address.substring(idx, address.length());
        
        System.out.println(city+":"+address);
        
        //大字
        Pattern p = Pattern.compile("(\\d+)");
        Matcher m = p.matcher(address);
        if(address.contains("丁目"))
            idx = address.indexOf("丁目") + 2;
        else if(m.find())
            idx = address.indexOf(m.group(1));
        String oaza = address.substring(0, idx);
        address = address.substring(idx, address.length());
        
        System.out.println(oaza+":"+address);
        
        //番地
        if(address.contains("-"))
            address = address.substring(0, address.indexOf("-"));
        if(address.contains("番"))
            address = address.substring(0, address.indexOf("番"));
        String block = address;
        address = "";
        System.out.println(block+":"+address);
    }
}
