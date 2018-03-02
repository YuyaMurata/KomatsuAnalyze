/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package json;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
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
public class MapIndexToJSON {
    public void write(String filename, Map index){
        try(JsonWriter writer = new JsonWriter(new BufferedWriter(new FileWriter(filename)))){
            writer.setIndent("  ");
            
            Gson gson = new Gson();
            gson.toJson(index, Map.class, writer);    

        }catch(IOException e){
            e.printStackTrace();
        }
    }
    
    public Map<String, String> reader(String filename) {
		Map<String, String> index;
        try (JsonReader reader = new JsonReader(new BufferedReader(new FileReader(filename)))) {

			Type type = new TypeToken<Map<String, String>>() {
			}.getType();

			Gson gson = new Gson();
			index = gson.fromJson(reader, type);
		} catch (Exception e) {
			return null;
		}
        
        return index;
	}
}