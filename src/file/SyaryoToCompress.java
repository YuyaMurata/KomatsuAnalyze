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
import java.util.List;
import java.util.Map;
import obj.SyaryoObject4;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;

/**
 *
 * @author ZZ17390
 */
public class SyaryoToCompress {
    public static Boolean runnable = true;
    public static Integer fileSize, availSize;
    
    public void write(String file, Map map) {
        runnable = true;
        
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

        Map<String, Map> map = new MapToJSON().reader(file);
        Map<String, SyaryoObject4> syaryoMap = new HashMap();
        for (String name : map.keySet()) {
            SyaryoObject4 syaryo = new SyaryoObject4(name);
            //_header(name, map);
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

    //データに含まれる異常オブジェクトを定型化(※SyaryoObject4とデータを参照)
    private void _header(String name, Map<String, Map> map) {
        if (!name.equals("_headers")) {
            return;
        }

        for (Object d : map.get(name).keySet()) {
            List head = (List) map.get(name).get(d);
            HashMap headMap = new HashMap();
            headMap.put(d, head);
            map.get(name).put(d, headMap);
        }

    }
}
