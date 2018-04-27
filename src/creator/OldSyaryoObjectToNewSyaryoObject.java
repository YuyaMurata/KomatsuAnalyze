/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package creator;

import java.io.File;
import java.util.Map;
import java.util.TreeMap;
import jdk.nashorn.internal.ir.debug.ObjectSizeCalculator;
import json.SyaryoToZip;
import json.SyaryoToZip2;
import obj.SyaryoObject2;
import obj.SyaryoObject3;

/**
 *
 * @author ZZ17390
 */
public class OldSyaryoObjectToNewSyaryoObject {

    private static String kisy = "PC200";
    private static String path = "..\\KomatsuData\\中間データ\\" + kisy + "\\obj\\";

    public static void main(String[] args) {
        File[] flist = (new File(path)).listFiles();
        for (File file : flist) {
            Map<String, SyaryoObject2> oldobj = new SyaryoToZip2().readObject(file);
            Map<String, SyaryoObject3> newobj = new TreeMap<>();
            
            System.out.println(file);
            System.out.println("origin:"+ObjectSizeCalculator.getObjectSize(oldobj));
            
            int n = oldobj.size();
            System.out.println(n+"台");
            for(SyaryoObject2 oldsyaryo : oldobj.values()){
                oldsyaryo.decompress();
                SyaryoObject3 syaryo = new SyaryoObject3(oldsyaryo.name);
                syaryo.map.putAll(oldsyaryo.map);
                syaryo.compress(true);
                oldsyaryo.compress(false);
                newobj.put(syaryo.name, syaryo);
                
                if(--n % 1000 == 0)
                    System.out.println("残り"+n+"台");
            }
            
            new SyaryoToZip().write(file.getAbsolutePath(), newobj);
            
            System.out.println("finish:"+ObjectSizeCalculator.getObjectSize(newobj));
        }
    }
}
