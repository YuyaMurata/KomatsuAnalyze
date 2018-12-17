/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.GeocodingResult;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import file.MapToJSON;
import param.KomatsuDataParameter;

/**
 *
 * @author ZZ17390
 */
public class GoogleMapTest {

    public static void main(String[] args) throws ApiException, InterruptedException, IOException, IOException {
        Map map = new MapToJSON().reader(KomatsuDataParameter.AUTH_PATH);
        System.out.println(map);
        //System.setProperty("https.proxyHost", ((List)map.get("proxy")).get(0).toString());
        //System.setProperty("https.proxyPort", ((List)map.get("proxy")).get(1).toString());
        
        GeoApiContext context = new GeoApiContext.Builder()
            .apiKey(map.get("apikey").toString())
            .build();
        System.out.println("Build");
        GeocodingResult[] results = GeocodingApi.geocode(context,
            "千葉駅").await();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        System.out.println(results.length);
        System.out.println(gson.toJson(results[0].geometry));
    }
}
