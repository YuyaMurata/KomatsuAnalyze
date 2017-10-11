/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zip;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import json.JsonToSyaryoObj;
import obj.SyaryoObject;

/**
 *
 * @author murata
 */
public class ZipFile {
    
    public Reader unzip(String file) throws FileNotFoundException, IOException {
        String fileZip = file;
        byte[] buffer = new byte[4096];
        ZipInputStream zis = new ZipInputStream(new FileInputStream(fileZip));
        ZipEntry zipEntry = zis.getNextEntry();
        String fileName = zipEntry.getName();
        System.out.println(fileName);
        
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        int len;
        while ((len = zis.read(buffer)) > 0) {
            bos.write(buffer, 0, len);
        }
        
        zis.closeEntry();
        zis.close();
        
        Reader reader = new InputStreamReader(new ByteArrayInputStream(bos.toByteArray()));
        
        return reader;
    }
    
    //Test
    public static void main(String[] args) {
        JsonToSyaryoObj json = new JsonToSyaryoObj();
        Map<String, SyaryoObject> syaryoMap = json.reader2("syaryo_obj_WA470.zip");
        
        System.out.println(syaryoMap.keySet().stream().findFirst());
    }
}
