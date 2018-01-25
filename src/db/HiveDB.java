/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package db;

import db.field.Fields;
import db.field.Order;
import db.field.Work;
import db.field.Customer;
import db.field.EQP;
import db.field.Gaichu;
import db.field.Sell;
import db.field.Komtrax;
import db.field.Syaryo;
import db.field.Service;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author ZZ17390
 */
public abstract class HiveDB {
    
    //Setting DB
    public enum DB {
        DRIVER("org.apache.hive.jdbc.HiveDriver"),
        URL("jdbc:hive2://192.168.174.129:10000/default");
        
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
        String driverName = HiveDB.DB.DRIVER.get();

        try {
            Class.forName(driverName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        }
        
        try {
            return DriverManager.getConnection(HiveDB.DB.URL.get());
        } catch (SQLException ex) {
            ex.printStackTrace();
            System.exit(1);
        }
        
        return null;
    }
    
    //Setting Table
    public enum TABLE {
        CUSTOMER("customer", Customer._Customer.values()),
        CUSTOMER_COMMON("customer_common", Customer.Common.values()),
        SYARYO("syaryo", Syaryo._Syaryo.values()),
        SERVICE("service", Service._Service.values()),
        ORDER("kom_order", Order._Order.values()),
        PARTS("parts", Order.Parts.values()),
        WORK("work", Work._Work.values()),
        WORK_DETAIL("work_detail", Work.Detail.values()),
        WORK_INFO("work_info", Work.Info.values()),
        EQP_SYARYO("eqp_syaryo", EQP.Syaryo.values()),
        EQP_KEIREKI("eqp_keireki", EQP.Keireki.values()),
        EQP_HANBAI("eqp_hanbai", EQP.Hanbai.values()),
        EQP_SPEC("eqp_spec", EQP.Spec.values()),
        SELL("sell", Sell._Sell.values()),
        SELL_ATTSPEC("sell_attspec", Sell.ATTSpec.values()),
        SELL_SPEC("sell_spec", Sell.Spec.values()),
        SELL_TRADE("sell_trade", Sell.Trade.values()),
        SELL_USED("sell_used", Sell.Used.values()),
        SELL_OLD("sell_old", Sell.Old.values()),
        //GAICHU("gaichu", Gaichu._Gaichu.values()),
        //GAICHU_WORK("gaichu_work", Gaichu.Work.values()),
        //GAICHU_WORK_INFO("gaichu_work_info", Gaichu.Work_Info.values()),
        KOMTRAX("komtrax", Komtrax.TABLE.values());

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
