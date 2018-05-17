/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package json;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import obj.SyaryoObject1;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;
import obj.SyaryoObject2;
import zip.ZipFile;

/**
 *
 * @author ZZ17390
 */
public class JsonToSyaryoObj {
    
    public Map<String, SyaryoObject1> reader(String filename){
        Map<String, SyaryoObject1> syaryoMap;
        
        try(JsonReader reader = new JsonReader(new BufferedReader(new FileReader(filename)))){
            Type type = new TypeToken<Map<String, SyaryoObject1>>(){}.getType();

            Gson gson = new Gson();
            syaryoMap = gson.fromJson(reader, type);
            syaryoMap.remove("_summary");
        }catch(IOException e){
            return null;
        }
        
        return syaryoMap;
    }
    
    public Map<String, SyaryoObject1> reader2(String filename){
        Map<String, SyaryoObject1> syaryoMap;
        
        try{
            Type type = new TypeToken<Map<String, SyaryoObject1>>(){}.getType();

            Gson gson = new Gson();
            syaryoMap = gson.fromJson(new ZipFile().unzip(filename), type);
            /*for(SyaryoObject obj : syaryoMap.values()){
                System.out.println(obj.dump());
            }*/
        }catch(IOException e){
            return null;
        }
        
        return syaryoMap;
    }
    
    public Map<String, SyaryoObject2> reader3(String filename){
        Map<String, SyaryoObject2> syaryoMap;
        
        try(JsonReader reader = new JsonReader(new BufferedReader(new FileReader(filename)))){
            Type type = new TypeToken<Map<String, SyaryoObject2>>(){}.getType();

            Gson gson = new Gson();
            syaryoMap = gson.fromJson(reader, type);
            syaryoMap.remove("_summary");
        }catch(IOException e){
            return null;
        }
        
        return syaryoMap;
    }
}
