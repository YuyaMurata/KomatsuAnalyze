/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package json;

import com.fasterxml.jackson.databind.ObjectMapper;
import creator.template.SyaryoTemplate;
import de.undercouch.bson4jackson.BsonFactory;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;
import obj.SyaryoObject2;

/**
 *
 * @author ZZ17390
 */
public class SyaryoObjToBson {
    public byte[] toBson(Object obj) throws IOException{
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectMapper mapper = new ObjectMapper(new BsonFactory());
        mapper.writeValue(baos, obj);
        //byte[] buffer = baos.toByteArray();
        
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        GZIPOutputStream gos = new GZIPOutputStream(bos);
        gos.write(baos.toByteArray());
        
        baos.close();
        bos.close();
        gos.finish();
        
        return bos.toByteArray();
    }
    
    public SyaryoObject2 toSyaryoObject(byte[] buffer) throws IOException{
        ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
        ObjectMapper mapper = new ObjectMapper(new BsonFactory());
        SyaryoObject2 syaryo = mapper.readValue(bais, SyaryoObject2.class);

        return syaryo;
    }
    
    public SyaryoTemplate toSyaryoTemplate(byte[] buffer) throws IOException{
        ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
        ObjectMapper mapper = new ObjectMapper(new BsonFactory());
        SyaryoTemplate syaryo = mapper.readValue(bais, SyaryoTemplate.class);

        return syaryo;
    }
}
