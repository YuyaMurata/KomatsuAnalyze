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
    private static String PATH = KomatsuDataParameter.SYARYOOBJECT_FDPATH;
    
    public static Map<String, SyaryoObject4> load(String filename){
        //車両の読み込み
        return new SyaryoToCompress().read(PATH + "syaryo_obj_" + filename);
    }
}
