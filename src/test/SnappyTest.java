/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import jdk.nashorn.internal.ir.debug.ObjectSizeCalculator;
import json.JSONToBSON;
import org.xerial.snappy.Snappy;

/**
 *
 * @author ZZ17390
 */
public class SnappyTest {

    public static void main(String[] args) throws UnsupportedEncodingException, IOException {
        String input = "Hello snappy-java! Snappy-java is a JNI-based wrapper of "
            + "Snappy, a fast compresser/decompresser.";
        System.out.println(input);
        byte[] compressed = Snappy.compress(input.getBytes("UTF-8"));
        byte[] uncompressed = Snappy.uncompress(compressed);

        String result = new String(uncompressed, "UTF-8");
        System.out.println(result);
        
        Map testMap = new HashMap();
        for(int i=0; i < 10000; i++)
            testMap.put(i, String.valueOf(i*1000));
        
        System.out.println(testMap);
        System.out.println("origin:"+ObjectSizeCalculator.getObjectSize(testMap));
        Long start = System.currentTimeMillis();
        byte[] snappy_comp = Snappy.compress(getBytes(testMap));
        Long stop1 = System.currentTimeMillis();
        byte[] bson_comp = JSONToBSON.toBson(testMap);
        Long stop2 = System.currentTimeMillis();
        System.out.println("snappy:"+ObjectSizeCalculator.getObjectSize(snappy_comp)+" ,time="+(stop1-start));
        System.out.println("bson:"+ObjectSizeCalculator.getObjectSize(bson_comp)+" ,time="+(stop2-stop1));
        
    }

    private static byte[] getBytes(Object obj) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(obj);
            return baos.toByteArray();
        } catch (IOException ex) {
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
