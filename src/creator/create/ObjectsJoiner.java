/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package creator.create;

import java.io.File;
import java.util.Map;
import json.SyaryoToZip3;
import obj.SyaryoObject3;

/**
 *
 * @author ZZ17390
 */
public class ObjectsJoiner {
    private static String kisy = "PC200";
    private static String path = "..\\KomatsuData\\中間データ\\"+kisy+"\\obj\\";
    //private static String path = "middle\\"+kisy+"\\obj\\";
    private static SyaryoToZip3 zip3 = new SyaryoToZip3();
    
    public static void main(String[] args) {
        File[] flist = (new File(path)).listFiles();
        String objFile = "json\\syaryo_obj_" + kisy;
        
        //exists Syaryo Object
        File f = new File(objFile+".bz2");
        if(!f.exists()){
            System.out.println("syaryo_obj_"+kisy+" not found!");
            System.exit(0);
        }
        Map<String, SyaryoObject3> obj = zip3.read(objFile+".bz2");
        
        for(File file : flist)
            join(file, obj);
        
        zip3.write(objFile, obj);
    }
    
    public static void join(File file, Map<String, SyaryoObject3> obj){
        System.out.println(file.getName());
        
        Map<String, SyaryoObject3> joinobj = zip3.read(file.getAbsolutePath());
        
        joinobj.values().parallelStream().forEach(data ->{
            data.decompress();
            
            SyaryoObject3 syaryo = obj.get(data.getName());
            syaryo.decompress();
            syaryo.map.putAll(data.getAll());
            syaryo.compress(true);
            
            data.compress(false);
        });
        
        joinobj = null;
        System.gc();
    }
}
