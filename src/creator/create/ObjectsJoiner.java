/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package creator.create;

import java.io.File;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import json.SyaryoToZip3;
import obj.SyaryoObject4;

/**
 *
 * @author ZZ17390
 */
public class ObjectsJoiner {
    private static String KISY = "WA470";
    private static String PATH = KomatsuDataParameter.MIDDLEDATA_PATH;
    private static String OUTPATH = KomatsuDataParameter.OBJECT_PATH;
       
    public static void main(String[] args) {
        create(KISY, false);
    }
    
    public static Integer create(String kisy, Boolean iot){
        SyaryoToZip3 zip3 = new SyaryoToZip3();
        String objPath = PATH+"\\"+kisy+"\\shuffle\\";
        String syaryoPath = OUTPATH;
        String filename = syaryoPath+"syaryo_obj_"+kisy+".bz2";
        
        //Folder
        if(!(new File(syaryoPath)).exists())
            (new File(syaryoPath)).mkdir();
        
        //Filec Check
        if((new File(filename)).exists()){
            System.out.println("exists file " + filename);
            return -1;
        }
        
        //Syaryo Map
        Map<String, SyaryoObject4> syaryoMap = new ConcurrentHashMap<>();
        
        //Middle File
        File[] flist = (new File(objPath)).listFiles();
        for(File file : flist){
            //IoTデータは統合しない処理
            if(iot)
                if(file.getName().contains("KOMTRAX"))
                    continue;
            
            //統合処理
            System.out.println(file.getName());
            join(syaryoMap, zip3.read(file.getAbsolutePath()));
        }
        
        zip3.write(filename, syaryoMap);
        
        return syaryoMap.size();
    }
    
    public static void join(Map<String, SyaryoObject4> map1, Map<String, SyaryoObject4> map2){
        if(map2.isEmpty())
            System.out.println("Target File is Empty!");
        
        map2.values().parallelStream().forEach(s ->{
            s.decompress();
            
            SyaryoObject4 syaryo = map1.get(s.getName());
            if(syaryo == null)
                syaryo = new SyaryoObject4(s.getName());
            
            //車両情報を結合
            syaryo.decompress();
            syaryo.map.putAll(s.map);
            syaryo.compress(true);
            
            map1.put(syaryo.getName(), syaryo);
            
            s.compress(false);
        });
    }
}
