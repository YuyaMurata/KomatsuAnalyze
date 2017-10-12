/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package creator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import json.JsonToSyaryoTemplate;
import json.SyaryoObjToJson;
import obj.SyaryoObject;
import obj.SyaryoTemplate;

/**
 * 車両テンプレートから車両オブジェクトを作成
 * @author ZZ17390
 */
public class SyaryoObjectCreate {

    public static void main(String[] args) throws IOException {
        String path = "車両テンプレート";
        String kisy = "WA470";
        String midtemp = "syaryo_mid_" + kisy + ".zip";
        String FILENAME = "syaryo_obj_" + kisy + ".json";
        File[] flist = (new File(path)).listFiles();

        Map<String, SyaryoTemplate> syaryoTemplates = new JsonToSyaryoTemplate().reader2(midtemp);
        TreeMap<String, SyaryoObject> syaryoMap = new TreeMap();

        int n = 0;
        //車両のオブジェクト化
        for (String syaryoName : syaryoTemplates.keySet()) {
            //
            SyaryoTemplate syaryo = syaryoTemplates.get(syaryoName);

            Map<String, String> template = syaryo.getAll();
            SyaryoObject syaryoObj = new SyaryoObject(syaryo.getName());

            //Summary
            if (syaryoName.equals("_summary")) {
                syaryoMap.put(syaryoName, syaryoObj);
                continue;
            }

            n++;

            syaryoObj.add(template);

            //System.out.println(syaryoObj.dump());
            syaryoMap.put(syaryoObj.getName(), syaryoObj);

            if (n % 10000 == 0) {
                System.out.println(n + " 台");

            }
        }

        SyaryoObjToJson json = new SyaryoObjToJson();
        json.write(FILENAME, syaryoMap);
    }
}
