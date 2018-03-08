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

/**
 *
 * @author ZZ17390
 */
public class SyaryoObjToBson {
    public byte[] toBson(Map obj) throws IOException{
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectMapper mapper = new ObjectMapper(new BsonFactory());
        mapper.writeValue(baos, obj);
        //byte[] buffer = baos.toByteArray();
        
        //ByteArrayOutputStream bos = new ByteArrayOutputStream();
        //GZIPOutputStream gos = new GZIPOutputStream(bos);
        //gos.write(baos.toByteArray());
        
        baos.close();
        //bos.close();
        //gos.finish();
        
        return baos.toByteArray();
    }
    
    public Map toMap(byte[] buffer) throws IOException{
        ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
        ObjectMapper mapper = new ObjectMapper(new BsonFactory());
        Map map = mapper.readValue(bais, Map.class);

        return map;
    }
}
