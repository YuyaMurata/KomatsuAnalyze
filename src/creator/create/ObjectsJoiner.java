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
import obj.SyaryoObject2;

/**
 *
 * @author ZZ17390
 */
public class ObjectsJoiner {
    public static void main(String[] args) {
        join("PC138US", "allsupport");
    }
    
    public static void join(String kisy, String filename){
        String file = "json\\"+kisy+"\\syaryo_obj_" + kisy + "_" + filename;
        String objFile = "json\\syaryo_obj_" + kisy;
        
        //exists Syaryo Object
        File f = new File(objFile+".gz");
        if(!f.exists()){
            System.out.println("syaryo_obj_"+kisy+" not found!");
            System.exit(0);
        }
        
        Map<String, SyaryoObject2> obj = new SyaryoToZip().readObject(objFile);
        Map<String, SyaryoObject2> joinobj = new SyaryoToZip().readObject(file);
        
        obj.values().stream().filter(syaryo -> joinobj.get(syaryo.getName()) != null).forEach(syaryo ->{
            syaryo.decompress();
            joinobj.get(syaryo.getName()).decompress();
            syaryo.map.putAll(joinobj.get(syaryo.getName()).getAll());
            syaryo.compress(true);
            joinobj.get(syaryo.getName()).compress(false);
        });
        
        new SyaryoToZip().write(objFile, obj);
    }
}
