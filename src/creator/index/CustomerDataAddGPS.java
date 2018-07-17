/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package creator.index;

import google.AddressToGPS;
import java.util.HashMap;
import java.util.Map;
import json.MapIndexToJSON;
import param.KomatsuDataParameter;

/**
 *
 * @author kaeru_yuya
 */
public class CustomerDataAddGPS {
    private static String filename = KomatsuDataParameter.CUSTOMER_INDEX_PATH;
    public static void main(String[] args) {
        int pref = 3;
        int address1 = 4;
        
        AddressToGPS adgps = AddressToGPS.getInstance();
        
        Map<String, String> custMap = new MapIndexToJSON().reader(filename);
        Map<String, String> map = new HashMap<>();
        for(String id : custMap.keySet()){
            String[] data = custMap.get(id).split(",");
            String address = data[pref]+data[address1];
            
            //Double[] gps = adgps.getGPS(address);
            //String gpsstr = ","+gps[0]+","+gps[1];
            
            //System.out.println(address+gpsstr);
            map.put(address, "");
        }
        
        map.keySet().stream().forEach(System.out::println);
        System.out.println(custMap.size()+","+map.size());
        
        
        //new MapIndexToJSON().write(filename+".gps", map);
    }
}
