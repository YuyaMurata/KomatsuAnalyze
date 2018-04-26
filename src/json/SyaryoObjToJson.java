/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package json;

import com.google.gson.Gson;
import com.google.gson.stream.JsonWriter;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 *
 * @author ZZ17390
 */
public class SyaryoObjToJson {

    public void write(String filename, Map syaryoMap) {
        try (JsonWriter writer = new JsonWriter(
            new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(filename), "SJIS")
            ))) {
            writer.setIndent("  ");

            Gson gson = new Gson();

            gson.toJson(syaryoMap, Map.class, writer);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    

    

    public void write2(String filename, Map syaryoMap) {
        try (JsonWriter writer = new JsonWriter(new BufferedWriter(new FileWriter(filename)))) {
            //writer.setIndent("  ");

            Gson gson = new Gson();
            gson.toJson(syaryoMap, Map.class, writer);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
