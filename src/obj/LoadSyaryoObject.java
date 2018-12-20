/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package obj;

import java.util.Map;
import file.SyaryoToCompress;
import java.io.File;
import java.util.List;
import param.KomatsuDataParameter;

/**
 *
 * @author ZZ17390
 */
public class LoadSyaryoObject {
    private static LoadSyaryoObject instance = new LoadSyaryoObject();
    private String PATH;
    public Boolean isClosable = true; //ファイル書き込み時のチェック用
    public SyaryoObject _header; //直接アクセスしない
    private Map<String, SyaryoObject> syaryoMap;
    
    private LoadSyaryoObject() {
    }
    
    public static LoadSyaryoObject getInstance(){
        return instance;
    }
    
    public String getFilePath(){
        return PATH;
    }
    
    public void setFile(String file){
        PATH = KomatsuDataParameter.SYARYOOBJECT_FDPATH;
        syaryoMap = load(file);
    }
    
    public void setFile(File file){
        PATH = file.getAbsolutePath();
        syaryoMap = load(file.getAbsolutePath());
    }
    
    private Map<String, SyaryoObject> load(String filename){
        if(!filename.contains("\\"))
            PATH = PATH + "syaryo_obj_" + filename + ".bz2";
        
        //Open
        isClosable = false;
        
        //車両の読み込み
        Map<String, SyaryoObject> map = new SyaryoToCompress().read(PATH);
        
        //ヘッダの読み込み
        _header = map.get("_header");
        map.remove("_header");
        
        return map;
    }
    
    public Integer index(String key, String element){        
        return _header.get(key).get(key).indexOf(element);
    }
    
    public List<String> indexes(String key){
        return _header.get(key).get(key);
    }
    
    public Map<String, SyaryoObject> getSyaryoMap(){
        return syaryoMap;
    }
    
    public void close(){
        isClosable = true;
        if(_header != null){
            System.out.println("header is null!");
            syaryoMap.put("_header", _header);
        }
    }
}
