/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package creator.index;

import creator.template.SyaryoTemplate;
import db.HiveDB;
import db.field.Customer;
import db.field.EQP;
import db.field.Syaryo;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.TreeMap;
import json.MapIndexToJSON;

/**
 *
 * @author ZZ17390
 */
public class CustomerIndex {
    public static void main(String[] args) {
        TreeMap<String, String> customerIndex = new TreeMap();
        
        try {
            Statement stmt = HiveDB.getConnection().createStatement();

            //Customer
            String sql = String.format("select %s, %s, %s, %s from %s",
                    Customer._Customer.KSYCD,
                    Customer._Customer.KKYKCD,
                    Customer._Customer.HNSCD,
                    Customer._Customer.RENT_KBN,
                    HiveDB.TABLE.CUSTOMER.get()
                );
            System.out.println("Running: " + sql);

            ResultSet res = stmt.executeQuery(sql);

            int n = 0;
            while (res.next()) {
                n++;
                //Name
                String company = res.getString(Customer._Customer.KSYCD.get());
                String customer = res.getString(Customer._Customer.KKYKCD.get());
                String honsya = res.getString(Customer._Customer.HNSCD.get());
                String rent_kbn = res.getString(Customer._Customer.RENT_KBN.get());

                //Customer Index
                customerIndex.put(company+"_"+customer, honsya+"_"+rent_kbn);

                if (n % 10000 == 0) {
                    //System.out.println("Syaryo Processed : " + n);
                }
            }

            System.out.println("Total Created CutomerIndex = " + n);
            
            new MapIndexToJSON().write("customer_index.json", customerIndex);
        } catch (SQLException sqlex) {
            sqlex.printStackTrace();
        }
    }
}
