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

/**
 *
 * @author ZZ17390
 */
public class JSONToBSON {
    public static void main(String[] args) throws IOException {
        Map map = new HashMap();
        map.put("name", "ABC");
        map.put("age", 32);
        map.put("tel", "0101-0002-3421");
        
        byte[] b = toBson(map);
        System.out.println(b);
        
        Map deMap = toMap(b);
        System.out.println(deMap);
    }
    
    public static byte[] toBson(Map map) throws IOException{
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectMapper mapper = new ObjectMapper(new BsonFactory());
        mapper.writeValue(baos, map);
        byte[] buffer = baos.toByteArray();

        return buffer;
    }
    
    public static Map toMap(byte[] buffer) throws IOException{
        ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
        ObjectMapper mapper = new ObjectMapper(new BsonFactory());
        Map map = mapper.readValue(bais, Map.class);

        return map;
    }
}
