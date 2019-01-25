/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package file;

import file.MapToJSON;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import obj.LoadSyaryoObject;
import obj.SyaryoObject;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;
import param.KomatsuDataParameter;

/**
 *
 * @author ZZ17390
 */
public class SyaryoToCompress {
    public static Boolean runnable = true;
    public static Integer fileSize, availSize;
    
    private static LoadSyaryoObject LOADER = LoadSyaryoObject.getInstance();
    
    public void write(String file, Map map) {
        runnable = true;
        if(!LOADER.isClosable){
            System.err.println("LoadSyaryoObject is not close!");
            System.exit(0);
        }
            
        file = file.replace(".gz", "").replace(".bz2", "");

        int size = 1024;
        try (ByteArrayInputStream in = new ByteArrayInputStream(getBytes(map));
            OutputStream fout = Files.newOutputStream(Paths.get(file + ".bz2"));
            BufferedOutputStream out = new BufferedOutputStream(fout);
            BZip2CompressorOutputStream bzOut = new BZip2CompressorOutputStream(out)) {
            final byte[] buffer = new byte[size];
            int n = 0;
            while (-1 != (n = in.read(buffer))) {
                bzOut.write(buffer, 0, n);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        runnable = false;
    }

    public Map read(String file) {
        if (!file.contains(".bz2")) {
            System.out.println("ファイル拡張子が異なります:" + file);
            System.exit(0);
        }
        
        runnable = true;
        Map readObj = null;
        
        int size = 1024;
        try (InputStream fin = Files.newInputStream(Paths.get(file));
            BufferedInputStream in = new BufferedInputStream(fin);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            BZip2CompressorInputStream bzIn = new BZip2CompressorInputStream(in)) {

            final byte[] buffer = new byte[size];
            int n = 0;
            while (-1 != (n = bzIn.read(buffer))) {
                out.write(buffer, 0, n);
            }

            readObj = (Map) getObject(out.toByteArray());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        runnable = false;
        return readObj;
    }

    public Map guiRead(String file) {
        if (!file.contains(".bz2")) {
            System.out.println("ファイル拡張子が異なります:" + file);
            System.exit(0);
        }
        
        runnable = true;
        Map readObj = null;
        
        int size = 1024;
        try (InputStream fin = Files.newInputStream(Paths.get(file));
            BufferedInputStream in = new BufferedInputStream(fin);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            BZip2CompressorInputStream bzIn = new BZip2CompressorInputStream(in)) {

            //fileSize = 2 * in.available();
            //availSize = 0;
            
            final byte[] buffer = new byte[size];
            int n = 0;
            while (-1 != (n = bzIn.read(buffer))) {
                out.write(buffer, 0, n);
                //availSize += size;
                
                if(!runnable)
                    System.exit(0);
            }

            readObj = (Map) getObject(out.toByteArray());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        runnable = false;
        return readObj;
    }

    //車両オブジェクトクラスに依存するため使いまわし不可!
    public Map readJSON(String file) {
        if (!file.contains(".json")) {
            System.out.println("ファイル拡張子が異なります:" + file);
            System.exit(0);
        }

        Map<String, Map> map = new MapToJSON().toMap(file);
        Map<String, SyaryoObject> syaryoMap = new HashMap();
        for (String name : map.keySet()) {
            SyaryoObject syaryo = new SyaryoObject(name);
            syaryo.putAll(map.get(name));
            syaryoMap.put(name, syaryo);
        }

        return syaryoMap;
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
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
