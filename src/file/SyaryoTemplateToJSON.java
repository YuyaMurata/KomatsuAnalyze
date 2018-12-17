/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package file;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import creator.template.SimpleTemplate;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;

/**
 *
 * @author ZZ17390
 */
public class SyaryoTemplateToJSON {
    
    public Map<String, SimpleTemplate> ToTemplate(String filename) {
		Map<String, SimpleTemplate> syaryoMap;
        try (JsonReader reader = new JsonReader(new BufferedReader(new FileReader(filename)))) {

			Type type = new TypeToken<Map<String, SimpleTemplate>>() {
			}.getType();

			Gson gson = new Gson();
			syaryoMap = gson.fromJson(reader, type);
            
            //setting
            if(!filename.contains("error"))
                syaryoMap.values().stream().forEach(s -> s.setting());
            
		} catch (Exception e) {
			return null;
		}
        
        return syaryoMap;
	}
    
    public void toJSON(String filename, Map syaryoMap){
        if(syaryoMap == null)
            return ;
        
        try(JsonWriter writer = new JsonWriter(new BufferedWriter(new FileWriter(filename)))){
            writer.setIndent("  ");
            
            Gson gson = new Gson();
            gson.toJson(syaryoMap, Map.class, writer);    

        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
