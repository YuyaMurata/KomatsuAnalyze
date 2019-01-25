/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package obj;

import java.util.Map;
import file.SyaryoToCompress;
import java.io.File;
import java.io.Serializable;
import param.KomatsuDataParameter;

/**
 *
 * @author ZZ17390
 */
public class LoadSyaryoObject implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    
    private static LoadSyaryoObject instance = new LoadSyaryoObject();
    
    private String PATH = "Do not load file";
    public Boolean isClosable = true; //ファイル書き込み時のチェック用
    public SyaryoObject _header; //直接アクセスしない
    public Map<String, SyaryoObject> _syaryoMap; //直接アクセスしない
    
    private LoadSyaryoObject() {
    }
    
    public static LoadSyaryoObject getInstance(){
        return instance;
    }
    
    public void setName(String n){
        name = n;
    }
    
    public String getName(){
        return name;
    }
    
    public String getFilePath(){
        return PATH;
    }
    
    public void setFile(String file){
        PATH = KomatsuDataParameter.SYARYOOBJECT_FDPATH;
        _syaryoMap = load(file);
    }
    
    public void setFile(File file){
        PATH = file.getAbsolutePath();
        _syaryoMap = load(file.getAbsolutePath());
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
}
