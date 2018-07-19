/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gis;

import db.PostGIS;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author kaeru_yuya
 */
public class AddressToPostGIS {

    private Connection con;
    private static AddressToPostGIS instance = new AddressToPostGIS();

    private AddressToPostGIS() {
        con = PostGIS.getConnection();
    }

    public static AddressToPostGIS getInstance() {
        return instance;
    }

    public Double[] getGPS(String pref, String address) {
        Double[] gps = new Double[2];

        //街区
        //政令指定都市
        int idx = 0;
        if (address.contains("区")) {
            idx = address.indexOf("区") + 1;
        } else {
            idx = address.indexOf("市") + 1;
        }

        String city = address.substring(0, idx);
        address = address.substring(idx, address.length());
        if(city.length() < 2)
            return null;

        System.out.println(city + ":" + address);
        //大字
        Pattern p = Pattern.compile("(\\d+)");
        Matcher m;
        String oaza = "";
        if (address.length() > 0) {
            m = p.matcher(address);
            if (address.contains("丁目")) {
                idx = address.indexOf("丁目") + 2;
            } else if (m.find()) {
                idx = address.indexOf(m.group(1));
            } else {
                idx = address.length();
            }
            oaza = address.substring(0, idx);
            address = address.substring(idx, address.length());
        }

        //System.out.println(oaza+":"+address);
        //番地
        String block = "";
        if (address.length() > 0) {
            m = p.matcher(address);
            if (m.find()) {
                if (address.contains("-")) {
                    address = address.substring(0, address.indexOf("-"));
                } else if (address.contains("番")) {
                    address = address.substring(0, address.indexOf("番"));
                }
                block = address;
            }
            address = "";
        }
        //System.out.println(block+":"+address);

        try {
            Statement statement = con.createStatement();
            String sql = "select latitude, longitude from m_address_gaiku"
                    + " where prefectures_nm='" + pref + "'"
                    + " and city_nm='" + city + "'";
            if (oaza.length() > 0) {
                sql += " and oaza_nm like'" + oaza + "%'";
            }
            if (block.length() > 0) {
                sql += " and block_no='" + block + "'";
            }
            System.out.println(sql);
            ResultSet res = statement.executeQuery(sql);

            while (res.next()) {
                gps[0] = Double.valueOf(res.getString("latitude"));
                gps[1] = Double.valueOf(res.getString("longitude"));
            }

            return gps;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return null;
    }
}
