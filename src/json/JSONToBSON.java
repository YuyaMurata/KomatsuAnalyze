/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package json;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.undercouch.bson4jackson.BsonFactory;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 *
 * @author ZZ17390
 */
public class JSONToBSON {

    public static byte[] toBson(Map map) throws IOException {
        
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ObjectMapper mapper = new ObjectMapper(new BsonFactory());
            mapper.writeValue(baos, map);
            //byte[] buffer = baos.toByteArray();
            try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
                GZIPOutputStream gos = new GZIPOutputStream(bos)) {
                gos.write(baos.toByteArray());
                return bos.toByteArray();
            }
        }
    }

    public static Map toMap(byte[] buffer) throws IOException {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
            GZIPInputStream gis = new GZIPInputStream(bais)) {

            ObjectMapper mapper = new ObjectMapper(new BsonFactory());
            Map map = mapper.readValue(gis, Map.class);
            return map;
        }
    }
}
