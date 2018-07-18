/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package db;

import db.field.AddressGPS;
import db.field.Fields;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author kaeru_yuya
 */
public class PostGIS {
    //Setting DB
    public enum DB {
        DRIVER("org.postgresql.Driver"),
        URL("jdbc:postgresql://192.168.1.12:5432/gis");
        
        private final String text;
        private DB(final String text){
            this.text = text;
        }
        
        public String get(){
            return this.text;
        }
    }
    
    //Connect DB
    public static Connection getConnection(){
        String driverName = PostGIS.DB.DRIVER.get();

        try {
            Class.forName(driverName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        }
        
        try {
            return DriverManager.getConnection(PostGIS.DB.URL.get());
        } catch (SQLException ex) {
            ex.printStackTrace();
            System.exit(1);
        }
        
        return null;
    }
    
    //Setting Table
    public enum TABLE {
        ADDRESS_GPS("m_address_gaiku", AddressGPS._AddressGPS.values());

        private final String text;
        private final Fields[] header;
        
        private TABLE(final String text, Fields[] header) {
            this.text = text;
            this.header = header;
        }

        public String get(){
            return this.text;
        }
        
        public Fields[] getHeader(){
            return this.header;
        }
    }
}
