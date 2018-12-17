/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package obj;

import java.util.Map;
import file.SyaryoToCompress;
import param.KomatsuDataParameter;

/**
 *
 * @author ZZ17390
 */
public class LoadSyaryoObject {
    private static LoadSyaryoObject instance = new LoadSyaryoObject();
    private static String PATH = KomatsuDataParameter.SYARYOOBJECT_FDPATH;
    private static SyaryoObject header;
    private static Map<String, SyaryoObject> syaryoMap;
    
    private LoadSyaryoObject() {
    }
    
    public static LoadSyaryoObject getInstance(){
        return instance;
    }
    
    public void setFile(String file){
        syaryoMap = load(file);
    }
    
    private Map<String, SyaryoObject> load(String filename){
        //車両の読み込み
        Map<String, SyaryoObject> map = new SyaryoToCompress().read(PATH + "syaryo_obj_" + filename);
        
        //ヘッダの読み込み
        header = map.get("_header");
        map.remove("_header");
        
        return map;
    }
    
    public Integer index(String key, String element){        
        header.startHighPerformaceAccess();
        return header.get(key).get(key).indexOf(element);
    }
    
    public Map<String, SyaryoObject> getSyaryoMap(){
        return syaryoMap;
    }
}
