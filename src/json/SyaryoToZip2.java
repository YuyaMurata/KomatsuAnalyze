/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package json;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import creator.template.SyaryoTemplate;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.lang.reflect.Type;
import obj.SyaryoObject2;

/**
 *
 * @author ZZ17390
 */
public class SyaryoToZip2 {

    public Map<String, SyaryoTemplate> readTemplate(File file) { 
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, SyaryoTemplate>>() {
        }.getType();
        try {
            return gson.fromJson(readGzipFile(file), type);
        } catch (IOException ex) {
            ex.printStackTrace();
            System.exit(0);
        }
        return null;
    }
    
    public Map<String, SyaryoObject2> readObject(File file) {

        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, SyaryoObject2>>() {
        }.getType();
        try {
            return gson.fromJson(readGzipFile(file), type);
        } catch (IOException ex) {
            ex.printStackTrace();
            System.exit(0);
        }
        return null;
    }

    private String readGzipFile(File file) throws FileNotFoundException, IOException {
        try (BufferedReader br = new BufferedReader(
            new InputStreamReader(
                new GZIPInputStream(
                    new BufferedInputStream(
                        new FileInputStream(
                            file)))))) {
            return br.readLine();
        }
    }
}
