/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package json;

import com.google.gson.Gson;
import com.google.gson.stream.JsonWriter;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

/**
 *
 * @author ZZ17390
 */
public class SyaryoTemplateToJson {
    
    public void write(String filename, Map syaryoMap){
        try(JsonWriter writer = new JsonWriter(new BufferedWriter(new FileWriter(filename)))){
            //writer.setIndent("  ");
            
            Gson gson = new Gson();
            gson.toJson(syaryoMap, Map.class, writer);    

        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
