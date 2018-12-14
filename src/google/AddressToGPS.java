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
import com.google.maps.model.LatLng;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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
        System.setProperty("https.proxyHost", ((List) map.get("proxy")).get(0).toString());
        System.setProperty("https.proxyPort", ((List) map.get("proxy")).get(1).toString());

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

    public String getAddress(Double lat, Double lon) {
        String address = "";
        try {

            GeocodingResult[] results = GeocodingApi.reverseGeocode(context, new LatLng(lat, lon)).language("ja").await();
            address = results[0].formattedAddress;

        } catch (ApiException e) {
            e.printStackTrace();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return address;
    }

    public String getMesh(int n, Double lat, Double lng) {
        String meshID = null;

        if (n == 0) {
            return meshID;
        }

        //1次メッシュ
        meshID = String.valueOf((int) (lat * 60 / 40)) + String.valueOf((int) (lng - 100));
        Double rLat = lat * 60 % 40;
        Double rLng = lng - lng.intValue();

        if (n == 1) {
            return meshID;
        }

        //2次メッシュ
        meshID = meshID + String.valueOf((int) (rLat / 5)) + String.valueOf((int) (rLng * 60 / 7.5));
        rLat = rLat % 5;
        rLng = rLng * 60 % 7.5;

        if (n == 2) {
            return meshID;
        }

        //3次メッシュ
        meshID = meshID + String.valueOf((int) (rLat * 60 / 30)) + String.valueOf((int) (rLng * 60 / 45));

        return meshID;
    }

    public static void main(String[] args) {
        AddressToGPS adgps = AddressToGPS.getInstance();
        //System.out.println("溝の口駅:" + Arrays.asList(adgps.getGPS("溝の口駅")));
        //System.out.println(adgps.getMesh(1,35.6640352,139.6982122));

        //parser
        List<List<Double>> gps = Arrays.asList(addr.split("\n")).stream()
            .map(a -> a.replace("[", ",").replace("]", ","))
            .map(a -> Arrays.asList(a.split(",")).subList(1, 3).stream().map(l -> Double.valueOf(l)).collect(Collectors.toList()))
            .collect(Collectors.toList());
        
        for(List<Double> latlng : gps){
            System.out.println(latlng+":" + adgps.getAddress(latlng.get(0), latlng.get(1)));
        }
    }

    static String addr = "20110702[42.9337038, 143.1569907]\n" +
"20110702#0001[42.9287037, 143.1865278]\n" +
"20110702#0002[42.9199075, 143.2487963]\n" +
"20110702#0003[42.9213426, 143.3307408]\n" +
"20110702#0004[42.9169907, 143.3937499]\n" +
"20110702#0005[42.9141667, 143.3923147]\n" +
"20110702#0006[42.9702315, 143.4401388]\n" +
"20110702#0007[42.9922222, 143.4544907]\n" +
"20110702#0008[43.0386574, 143.4391667]\n" +
"20110702#0009[43.0835649, 143.3851852]\n" +
"20110718[43.4698148, 143.685139]";

}
