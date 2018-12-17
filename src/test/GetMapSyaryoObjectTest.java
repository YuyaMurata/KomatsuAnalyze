/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.util.List;
import java.util.Map;
import file.SyaryoToCompress;
import obj.SyaryoObject;
import param.KomatsuDataParameter;

/**
 *
 * @author ZZ17390
 */
public class GetMapSyaryoObjectTest {
    private static String PATH = KomatsuDataParameter.SYARYOOBJECT_FDPATH;
    private static String KISY = "PC200";
    private static Map<String, SyaryoObject> map;
    
    public static void main(String[] args) {
        map = new SyaryoToCompress().read(PATH + "syaryo_obj_" + KISY + "_sv_form.bz2");
        
        SyaryoObject syaryo = map.get("PC200-8N1-351668");
        for(Object list : syaryo.getMap().values()){
            Map test = (Map) list;
            System.out.println(test);
        }
    }
}
