/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package creator;

import creator.template.SyaryoTemplate;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;
import json.JsonToSyaryoTemplate;
import json.SyaryoObjToBson;
import json.SyaryoTemplateToJson;
import org.apache.commons.codec.binary.Hex;

/**
 *
 * @author ZZ17390
 */
public class TranslationSyaryoToBSON {

    public static void main(String[] args) throws IOException {

        String kisy = "PC200";
        String path = "..\\KomatsuData\\車両テンプレート\\" + kisy + "系\\";
        //String path = "template\\"+kisy+"\\";
        String outpath = "..\\KomatsuData\\中間データ\\";
        //String outpath = "middle\\";
        File[] flist = (new File(path)).listFiles();

        SyaryoObjToBson bson = new SyaryoObjToBson();
        for (File f : flist) {
            TreeMap<String, String> syaryoMap = new TreeMap();

            if (f.getName().contains("error.csv")) {
                continue;
            }
            System.out.println(f.getName() + ",");

            Map<String, SyaryoTemplate> syaryoTemplates = new JsonToSyaryoTemplate().reader(f.getPath());
            for (String name : syaryoTemplates.keySet()) {
                String result = new String(Hex.encodeHex(bson.toBson(syaryoTemplates.get(name))));
                syaryoMap.put(name, result);
            }
            
            new SyaryoTemplateToJson().write(path+f.getName().replace("json", "bson"), syaryoMap);
        }
    }
}
