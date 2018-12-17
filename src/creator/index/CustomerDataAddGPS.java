/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package creator.index;

import gis.AddressToPostGIS;
import google.AddressToGPS;
import java.util.HashMap;
import java.util.Map;
import file.MapToJSON;
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
        
        //AddressToGPS adgps = AddressToGPS.getInstance();
        AddressToPostGIS adgps = AddressToPostGIS.getInstance();
        
        Map<String, String> custMap = new MapToJSON().toMap(filename);
        Map<String, String> map = new HashMap<>();
        int n = 0;
        for(String id : custMap.keySet()){
            String[] data = custMap.get(id).split(",");
            String address = data[pref]+data[address1];
            
            Double[] gps = adgps.getGPS(data[pref], data[address1]);
            if(gps == null){
                continue;
            }
            String gpsstr = gps[0]+","+gps[1];
            n++;
            System.out.println(n+":"+address+","+gpsstr);
            map.put(address, gpsstr);
        }
        
        map.keySet().stream().forEach(System.out::println);
        System.out.println(custMap.size()+","+map.size());
        
        
        //new MapIndexToJSON().write(filename+".gps", map);
    }
}
