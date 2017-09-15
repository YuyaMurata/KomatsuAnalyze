/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package json;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import obj.SyaryoObject;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;

/**
 *
 * @author ZZ17390
 */
public class JsonToSyaryoObj {
    
    public Map<String, SyaryoObject> reader(String filename){
        try(JsonReader reader = new JsonReader(new BufferedReader(new FileReader(filename)))){
            
            Type type = new TypeToken<Map<String, SyaryoObject>>(){}.getType();

            Gson gson = new Gson();
            Map<String, SyaryoObject> syaryoMap = gson.fromJson(reader, type);
            /*for(SyaryoObject obj : syaryoMap.values()){
                System.out.println(obj.dump());
            }*/
            
            return syaryoMap;
        }catch(IOException e){
            return null;
        }
    }
}
