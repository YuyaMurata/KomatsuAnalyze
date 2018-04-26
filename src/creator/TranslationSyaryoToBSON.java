/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package creator;

import creator.template.SyaryoTemplate;
import creator.template.SyaryoTemplate1;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;
import jdk.nashorn.internal.ir.debug.ObjectSizeCalculator;
import json.JsonToSyaryoTemplate;
import json.SyaryoToZip;

/**
 *
 * @author ZZ17390
 */
public class TranslationSyaryoToBSON {

    public static void main(String[] args) throws IOException {

        String kisy = "PC200";
        String path = "..\\KomatsuData\\車両テンプレート\\" + kisy + "\\template\\";
        String outpath = "..\\KomatsuData\\車両テンプレート\\" + kisy + "\\gz\\";
        //String path = "template\\"+kisy+"\\template\\";
        //String outpath = "template\\"+kisy+"\\gz\\";
        File[] flist = (new File(path)).listFiles();

        for (File f : flist) {
            TreeMap<String, SyaryoTemplate> syaryoMap = new TreeMap();
            
            System.out.println(f.getName() + ",");

            Map<String, SyaryoTemplate1> syaryoTemplates = new JsonToSyaryoTemplate().reader3(f.getPath());
            System.out.println("origin:"+ObjectSizeCalculator.getObjectSize(syaryoTemplates));
            for (String name : syaryoTemplates.keySet()) {
                SyaryoTemplate syaryo = new SyaryoTemplate(syaryoTemplates.get(name));
                syaryo.compress(true);
                syaryoMap.put(name, syaryo);
            }
            System.out.println("compress:"+ObjectSizeCalculator.getObjectSize(syaryoMap));
            new SyaryoToZip().write(outpath+f.getName(), syaryoMap);
            
            syaryoTemplates = null;
        }
    }
}
