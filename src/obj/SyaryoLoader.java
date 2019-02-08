/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package obj;

import java.io.File;
import java.util.List;
import java.util.Map;
import rmi.SyaryoObjectClient;

/**
 * 車両オブジェクト読み込み統合管理
 * @author ZZ17390
 */
public class SyaryoLoader {
    private static SyaryoLoader instance = new SyaryoLoader();
    private static SyaryoObjectClient CLIENT = SyaryoObjectClient.getInstance();
    private static LoadSyaryoObject LOADER;
    
    private SyaryoLoader(){
    }
    
    public static SyaryoLoader getInstance(){
        return instance;
    }
    
    public void setFile(String file){
        if(CLIENT.isRunnable){
            CLIENT.setLoadFile(file);
            LOADER = CLIENT.getLoader();
        }else{
            LOADER = LoadSyaryoObject.getInstance();
            LOADER.setName("Local Data");
            LOADER.setFile(file);
        }
        
        System.out.println(LOADER.getName());
    }
    
    public void setFile(File file){
        if(CLIENT.isRunnable){
            CLIENT.setLoadFile(file);
            LOADER = CLIENT.getLoader();
        }else{
            LOADER = LoadSyaryoObject.getInstance();
            LOADER.setName("Local Data");
            LOADER.setFile(file);
        }
        
        System.out.println(LOADER.getName());
    }
    
    public Integer index(String key, String element){        
        return LOADER._header.get(key).get(key).indexOf(element);
    }
    
    public List<String> indexes(String key){
        return LOADER._header.get(key).get(key);
    }
    
    public Map<String, SyaryoObject> getSyaryoMap(){
        LOADER._syaryoMap.remove("_header");
        return LOADER._syaryoMap;
    }
    
    public Map<String, SyaryoObject> getSyaryoMapWithHeader(){
        return LOADER._syaryoMap;
    }
    
    public SyaryoObject getHeader(){
        return LOADER._header;
    }
    
    public Boolean isClosable(){
        return LOADER.isClosable;
    }
    
    public Boolean isServerRun(){
        return CLIENT.isRunnable;
    }
    
    public String getFilePath(){
        return LOADER.getFilePath();
    }
    
    public void close(){
        LOADER.isClosable = true;
        if(LOADER._header != null){
            LOADER._syaryoMap.put("_header", LOADER._header);
        }else
            System.out.println("header is null!");
    }
}
