/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package creator.create;

import creator.template.SyaryoTemplate;
import java.io.File;
import java.util.Map;
import json.SyaryoToZip;
import json.SyaryoToZip2;
import obj.SyaryoObject2;

/**
 *
 * @author ZZ17390
 */
public class ObjectsJoiner {
    private static String kisy = "PC138US";
    private static String path = "..\\KomatsuData\\中間データ\\"+kisy+"\\obj\\";
    private static SyaryoToZip zip = new SyaryoToZip();
    private static SyaryoToZip2 zip2 = new SyaryoToZip2();
    
    public static void main(String[] args) {
        File[] flist = (new File(path)).listFiles();
        String objFile = "json\\syaryo_obj_" + kisy;
        
        //exists Syaryo Object
        File f = new File(objFile+".gz");
        if(!f.exists()){
            System.out.println("syaryo_obj_"+kisy+" not found!");
            System.exit(0);
        
        }
        Map<String, SyaryoObject2> obj = zip.readObject(objFile);
        
        for(File file : flist)
            join("PC138US", file, obj);
        
        zip.write(objFile, obj);
    }
    
    public static void join(String kisy, File file, Map<String, SyaryoObject2> obj){
        System.out.println(file.getName());
        
        Map<String, SyaryoObject2> joinobj = zip2.readObject(file);
        
        joinobj.values().parallelStream().forEach(data ->{
            data.decompress();
            
            SyaryoObject2 syaryo = obj.get(data.getName());
            syaryo.decompress();
            syaryo.map.putAll(data.getAll());
            syaryo.compress(true);
            
            data.compress(false);
        });
        
        joinobj = null;
    }
}
