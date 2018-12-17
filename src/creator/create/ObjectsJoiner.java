/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package creator.create;

import param.KomatsuDataParameter;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import file.SyaryoToCompress;
import obj.SyaryoObject4;

/**
 *
 * @author ZZ17390
 */
public class ObjectsJoiner {
    private static String KISY = "PC200";
    private static String PATH = KomatsuDataParameter.MIDDLEDATA_PATH;
    private static String OUTPATH = KomatsuDataParameter.OBJECT_PATH;
    private static Map<String, String[]> JOIN_TYPE = KomatsuDataParameter.joinMap;
    
    public static void main(String[] args) {
        //1から結合
        //create(KISY, true);
        
        //追加で結合
        add(KISY, false);
    }
    
    public static Integer create(String kisy, Boolean iot){
        KISY = kisy;
        SyaryoToCompress zip3 = new SyaryoToCompress();
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
            
            if(!file.getName().contains(".bz2"))
                continue;
                
            //統合処理
            System.out.println(file.getName());
            join(syaryoMap, zip3.read(file.getAbsolutePath()));
        }
        
        zip3.write(filename, syaryoMap);
        
        return syaryoMap.size();
    }
    
    public static Integer add(String kisy, Boolean iot){
        KISY = kisy;
        SyaryoToCompress zip3 = new SyaryoToCompress();
        String objPath = PATH+"\\"+kisy+"\\shuffle\\";
        String syaryoPath = OUTPATH;
        String filename = syaryoPath+"syaryo_obj_"+kisy+".bz2";
        
        //Folder
        if(!(new File(syaryoPath)).exists())
            (new File(syaryoPath)).mkdir();
        
        //Filec Check
        if(!(new File(filename)).exists()){
            System.out.println("not exists file " + filename);
            return -1;
        }
        
        //Syaryo Map
        Map<String, SyaryoObject4> syaryoMap = zip3.read(filename);
        
        //Middle File
        File[] flist = (new File(objPath)).listFiles();
        for(File file : flist){
            //IoTデータは統合しない処理
            if(!file.getName().contains("KOMTRAX"))
                    continue;
            
            //統合処理
            System.out.println(file.getName());
            join(syaryoMap, zip3.read(file.getAbsolutePath()));
        }
        
        zip3.write(filename, syaryoMap);
        
        return syaryoMap.size();
    }
    
    //join map1 <- map2
    public static void join(Map<String, SyaryoObject4> map1, Map<String, SyaryoObject4> map2){
        if(map2.isEmpty())
            System.out.println("Target File is Empty!");
        
        //Join Type
        List<String> check;
        if(JOIN_TYPE.get(KISY) == null)
            check = map2.keySet().stream().map(s -> s.split("-")[1]).distinct().collect(Collectors.toList());
        else
            check = Arrays.asList(JOIN_TYPE.get(KISY));
        System.out.println(KISY + " Join Type:" + check);
        
        //Join
        map2.values().parallelStream().filter(s -> check.contains(s.getName().split("-")[1]))
            .forEach(s ->{
            SyaryoObject4 syaryo = map1.get(s.getName());
            if(syaryo == null)
                syaryo = new SyaryoObject4(s.getName());
            
            //車両情報を結合
            syaryo.putAll(s.getMap());
            
            map1.put(syaryo.getName(), syaryo);
        });
    }
}
