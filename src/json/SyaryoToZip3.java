/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package json;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import obj.SyaryoObject2;
import obj.SyaryoObject3;

/**
 *
 * @author ZZ17390
 */
public class SyaryoToZip3 {

    public Map<String, SyaryoObject3> readObject(File file) {
        try {
            return (Map<String, SyaryoObject3>) getObject(readGzipFile(file));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public void write(String filename, Map syaryoMap) {
        filename = filename.replace(".json", "").replace(".gz", "") + ".gz";
        try {
            writeGzipFile(filename, getBytes(syaryoMap));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private byte[] readGzipFile(File file) throws FileNotFoundException, IOException {
        byte[] result = new byte[]{};
        try (GZIPInputStream gis = new GZIPInputStream(
            new BufferedInputStream(
                new FileInputStream(
                    file)))) {
            gis.read(result);
            return result;
        }
    }
    
    public Map<String, SyaryoObject3> readOldObject(String filename) {
        filename = filename.replace(".gz", "") + ".gz";
        
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, SyaryoObject3>>() {
        }.getType();
        try {
            return gson.fromJson(readOldGzipFile(filename), type);
        } catch (IOException ex) {
            ex.printStackTrace();
            System.exit(0);
        }
        return null;
    }
    
    private String readOldGzipFile(String filename) throws FileNotFoundException, IOException {
        try (BufferedReader br = new BufferedReader(
            new InputStreamReader(
                new GZIPInputStream(
                    new BufferedInputStream(
                        new FileInputStream(
                            new File(filename))))))) {
            return br.readLine();
        }
    }

    private void writeGzipFile(String filename, byte[] syaryoMapBytes) throws FileNotFoundException, IOException {
        byte[] compress;
        try(ByteArrayOutputStream baos = new ByteArrayOutputStream(syaryoMapBytes.length);
            GZIPOutputStream gos = new GZIPOutputStream(baos)){
            gos.write(syaryoMapBytes);
            compress = baos.toByteArray();
        }
        
        try(FileOutputStream fos = new FileOutputStream(filename)){
            fos.write(compress);
        }
    }

    private static byte[] getBytes(Object obj) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(obj);
            return baos.toByteArray();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private static Object getObject(byte[] bytes) {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(bais)) {
            return ois.readObject();
        } catch (IOException ex) {
        } catch (ClassNotFoundException ex) {
        }
        return null;
    }

}
