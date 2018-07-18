/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gis;

import db.PostGIS;
import java.sql.Connection;

/**
 *
 * @author kaeru_yuya
 */
public class AddressToPostGIS {
    private Connection con;
    private static AddressToPostGIS instance = new AddressToPostGIS();
    
    private AddressToPostGIS(){
        con = PostGIS.getConnection();
    }
    
    public static AddressToPostGIS getInstance(){
        return instance;
    }
    
    public Double[] getGPS(String pref, String address) {
        Double[] gps = new Double[2];
        //政令指定都市
        String city = "";
        if(address.contains("区"))
            city = address.substring(0, address.indexOf("区"));
        String oaza = "";
        String block = "";
        
        return null;
    }
}
