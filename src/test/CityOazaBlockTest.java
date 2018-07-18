/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

/**
 *
 * @author kaeru_yuya
 */
public class CityOazaBlockTest {
    String[] kansu = new String[]{"一"}; 
    
    public static void main(String[] args) {
        String address = "旭川市四条通二十丁目左4";
        
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
        if(address.contains("丁目"))
            idx = address.indexOf("丁目") + 2;
        else if(address.contains("-"))
            idx = address.indexOf("-") + 1;
        String oaza = address.substring(0, idx);
        address = address.substring(idx, address.length());
        
        System.out.println(oaza+":"+address);
        
        //番地
        String block = address;
        address = "";
        System.out.println(block+":"+address);
    }
}
