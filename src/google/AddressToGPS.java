/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package google;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.GeocodingResult;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import json.MapIndexToJSON;
import param.KomatsuDataParameter;

/**
 *
 * @author kaeru_yuya
 */
public class AddressToGPS {

    private static AddressToGPS instace = new AddressToGPS();
    private GeoApiContext context;

    private AddressToGPS() {
        Map map = new MapIndexToJSON().reader(KomatsuDataParameter.AUTH_PATH);
        System.out.println(map);

        //Proxy
        //System.setProperty("https.proxyHost", ((List) map.get("proxy")).get(0).toString());
        //System.setProperty("https.proxyPort", ((List) map.get("proxy")).get(1).toString());

        context = new GeoApiContext.Builder()
            .apiKey(map.get("apikey").toString())
            .build();
    }

    public static AddressToGPS getInstance() {
        return instace;
    }

    public Double[] getGPS(String address) {
        Double[] gps = new Double[2];

        try {
            GeocodingResult[] results = GeocodingApi.geocode(context, address).await();
            gps[0] = results[0].geometry.location.lat;
            gps[1] = results[0].geometry.location.lng;
        } catch (ApiException e) {
            e.printStackTrace();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return gps;
    }

    public String getMesh(int n, Double lat, Double lng) {
        String meshID = null;
        
        if(n==0)
            return meshID;
        
        //1次メッシュ
        meshID = String.valueOf((int) (lat * 60 / 40)) + String.valueOf((int) (lng - 100));
        Double rLat = lat * 60 % 40;
        Double rLng = lng - lng.intValue();

        if(n==1)
            return meshID;

        //2次メッシュ
        meshID = meshID + String.valueOf((int) (rLat / 5)) + String.valueOf((int) (rLng * 60 / 7.5));
        rLat = rLat % 5;
        rLng = rLng * 60 % 7.5;
        
        if(n==2)
            return meshID;

        //3次メッシュ
        meshID = meshID + String.valueOf((int) (rLat * 60 / 30)) + String.valueOf((int) (rLng * 60 / 45));

        return meshID;
    }

    public static void main(String[] args) {
        AddressToGPS adgps = AddressToGPS.getInstance();
        System.out.println("溝の口駅:" + Arrays.asList(adgps.getGPS("溝の口駅")));

        System.out.println(adgps.getMesh(1,35.6640352,139.6982122));
    }
}
