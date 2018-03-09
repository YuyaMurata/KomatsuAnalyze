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
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.lang.reflect.Type;

/**
 *
 * @author ZZ17390
 */
public class SyaryoToZip {

    public void write(String filename, Map syaryoMap) {
        filename = filename.replace(".json", "") + ".gz";
        
        Gson gson = new Gson();
        String json = gson.toJson(syaryoMap);
        try {
            writeGzipFile(filename, json);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public Map<String, SyaryoTemplate> readTemplate(String filename) {
        filename = filename.replace(".gz", "") + ".gz";
        
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, SyaryoTemplate>>() {
        }.getType();
        try {
            return gson.fromJson(readGzipFile(filename), type);
        } catch (IOException ex) {
            ex.printStackTrace();
            System.exit(0);
        }
        return null;
    }

    private void writeGzipFile(String filename, String json) throws FileNotFoundException, IOException {
        try (BufferedWriter bw = new BufferedWriter(
            new OutputStreamWriter(
                new GZIPOutputStream(
                    new BufferedOutputStream(
                        new FileOutputStream(
                            new File(filename))))))) {
            bw.write(json);
        }
    }

    private String readGzipFile(String filename) throws FileNotFoundException, IOException {
        try (BufferedReader br = new BufferedReader(
            new InputStreamReader(
                new GZIPInputStream(
                    new BufferedInputStream(
                        new FileInputStream(
                            new File(filename))))))) {
            return br.readLine();
        }
    }
}
