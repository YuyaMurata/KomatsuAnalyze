/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.undercouch.bson4jackson.BsonFactory;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

/**
 *
 * @author ZZ17390
 */
public class JSONToBSON {

    public static void main(String[] args) throws IOException, DecoderException {
        Map map = new HashMap();
        map.put("name", "ABC");
        map.put("age", 13);
        System.out.println(map);
        byte[] b = JSONToBSON.toBson(map);
        System.out.println(b);
        
        //String str = new String(Hex.encodeHex(b));
        //System.out.println(str);
        
        Map map2 = JSONToBSON.toMap(b);
        System.out.println(map2);
    }

    public static byte[] toBson(Map map) throws IOException {
        ByteArrayOutputStream bos;
        GZIPOutputStream gos;
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ObjectMapper mapper = new ObjectMapper(new BsonFactory());
            mapper.writeValue(baos, map);
            //byte[] buffer = baos.toByteArray();
            bos = new ByteArrayOutputStream();
            gos = new GZIPOutputStream(bos);
            gos.write(baos.toByteArray());
        }
        bos.close();
        gos.finish();

        return bos.toByteArray();
    }

    public static Map toMap(byte[] buffer) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
        GZIPInputStream gis = new GZIPInputStream(bais);
        
        ObjectMapper mapper = new ObjectMapper(new BsonFactory());
        Map map = mapper.readValue(gis, Map.class);

        return map;
    }
}
