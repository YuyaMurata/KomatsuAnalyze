/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package json;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;
import creator.template.SyaryoTemplate;
import creator.template.SyaryoTemplate1;
import zip.ZipFile;

/**
 *
 * @author ZZ17390
 */
public class JsonToSyaryoTemplate {

	public Map<String, SyaryoTemplate> reader(String filename) {
		Map<String, SyaryoTemplate> syaryoMap;
        try (JsonReader reader = new JsonReader(new BufferedReader(new FileReader(filename)))) {

			Type type = new TypeToken<Map<String, SyaryoTemplate>>() {
			}.getType();

			Gson gson = new Gson();
			syaryoMap = gson.fromJson(reader, type);
            
            //setting
            syaryoMap.values().stream().forEach(s -> s.setting());
		} catch (Exception e) {
			return null;
		}
        
        return syaryoMap;
	}
    
	public Map<String, SyaryoTemplate> reader2(String filename) {
		Map<String, SyaryoTemplate> syaryoMap;
        
        try {
			Type type = new TypeToken<Map<String, SyaryoTemplate>>() {
			}.getType();

			Gson gson = new Gson();
			syaryoMap = gson.fromJson(new ZipFile().unzip(filename), type);
			syaryoMap.remove("_summary");
            
            //setting
            syaryoMap.values().stream().forEach(s -> s.setting());
		} catch (IOException e) {
			return null;
		}
        
        return syaryoMap;
	}
    
    public Map<String, SyaryoTemplate1> reader3(String filename) {
		Map<String, SyaryoTemplate1> syaryoMap;
        try (JsonReader reader = new JsonReader(new BufferedReader(new FileReader(filename)))) {

			Type type = new TypeToken<Map<String, SyaryoTemplate1>>() {
			}.getType();

			Gson gson = new Gson();
			syaryoMap = gson.fromJson(reader, type);
            
		} catch (Exception e) {
			return null;
		}
        
        return syaryoMap;
	}
}
