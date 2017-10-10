/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package creator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import json.JsonToSyaryoTemplate;
import json.SyaryoObjToJson;
import obj.SyaryoObject;
import obj.SyaryoTemplate;

/**
 *
 * @author ZZ17390
 */
public class SyaryoObjectCreate {

    public static void main(String[] args) throws IOException {
        String path = "車両テンプレート";
        String kisy = "HM300";
        String midtemp = "syaryo_mid_"+kisy+".json";
        String FILENAME = "syaryo_obj_"+kisy+".json";
        File[] flist = (new File(path)).listFiles();

        Map<String, SyaryoTemplate> syaryoTemplates = new JsonToSyaryoTemplate().reader(midtemp);
        TreeMap<String, SyaryoObject> syaryoMap = new TreeMap();

        int n = 0;
        //車両のオブジェクト化
        for (String syaryoName : syaryoTemplates.keySet()) {
            //
            SyaryoTemplate syaryo = syaryoTemplates.get(syaryoName);

            Map<String, String> temp = syaryo.getAll();
            SyaryoObject syaryoObj = new SyaryoObject(syaryo.getName());
            
            //Summary
            if(syaryoName.equals("_summary")){
                syaryoMap.put(syaryoName, syaryoObj);
                continue;
            }
            
            n++;

            for (String key : temp.keySet()) {
                String str = temp.get(key);
                String[] line = str.split("\n");
                for (int i = 0; i < line.length; i++) {

                    if (line[i].contains("会社コード")) {
                        continue;
                    }

                    String[] arr = line[i].trim().split(",");

                    if (key.equals("国")) {
                        syaryoObj.add(key, arr[2], arr[3]);
                    } else if (key.equals("仕様")) {
                        syaryoObj.addSpec(arr[0], arr[1], "syaryo");
                    } else if (key.equals("最終更新日")) {
                        syaryoObj.add(key, arr[2], (arr[0] + "_" + arr[1]));
                    } else if (key.equals("生産")) {
                        syaryoObj.add(key, arr[0]);
                    } else if (key.equals("出荷")) {
                        syaryoObj.add(key, arr[0]);
                    } else if (key.equals("廃車")) {
                        syaryoObj.add(key, arr[2]);
                    } else if (key.equals("顧客")) {
                        List ownl = new ArrayList();
                        ownl.add(arr[3]);
                        ownl.add(arr[5]);
                        syaryoObj.addOwner(arr[2], arr[4], ownl, (arr[0] + "_" + arr[1]));
                    } else if (arr.length == 4) {
                        syaryoObj.add(key, arr[2], arr[3], (arr[0] + "_" + arr[1]));
                    } else if (arr.length == 5) {
                        syaryoObj.add(key, arr[2], arr[3], arr[4], (arr[0] + "_" + arr[1]));
                    } else {
                        List list = new ArrayList();
                        //System.out.println(arr.length+":"+str);
                        for (int j = 4; j < arr.length; j++) {
                            list.add(arr[j]);
                        }
                        syaryoObj.add(key, arr[2], arr[3], list, (arr[0] + "_" + arr[1]));
                    }
                }
            }

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
