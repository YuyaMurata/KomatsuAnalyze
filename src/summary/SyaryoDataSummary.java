/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package summary;

import file.CSVFileReadWrite;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import json.SyaryoObjToJson;
import json.SyaryoToZip0;
import json.SyaryoToZip3;
import obj.SyaryoObject2;
import obj.SyaryoObject3;

/**
 *
 * @author ZZ17390
 */
public class SyaryoDataSummary {

    private static String kisy = "PC200";
    private static String path = "..\\KomatsuData\\車両テンプレート\\" + kisy + "\\";

    public static void main(String[] args) {

        SyaryoObject3 sample = null;
        int size = 0;

        String filename = "json\\syaryo_obj_" + kisy + "_form.bz2";
        Map<String, SyaryoObject3> syaryoMap = new SyaryoToZip3().read(filename);
        
        //Syaryo Data Check
        int cnt = 0;
        Map<Object, Integer> dataSizeMap = new HashMap();
        Map<Object, Integer> numSyaryoMap = new HashMap();
        for(SyaryoObject3 syaryo : syaryoMap.values()){
            syaryo.decompress();
            int total = 0;
            for(Object key : syaryo.getAll().keySet()){
                int s = syaryo.getAll().get(key).size();
                if(dataSizeMap.get(key) == null){
                    dataSizeMap.put(key, s);
                    numSyaryoMap.put(key, 1);
                }else{
                    dataSizeMap.put(key, dataSizeMap.get(key)+s);
                    numSyaryoMap.put(key, numSyaryoMap.get(key)+1);
                }
                total = total + s;
            }
            if(total > size){
                if(sample != null)
                    sample.compress(false);
                sample = syaryo;
            }else
                syaryo.compress(false);
            
            if((++cnt)%1000 == 0)
                System.out.println(kisy+":"+cnt+"件処理");
        }
        
        //Summary Output
        try (PrintWriter pw = CSVFileReadWrite.writer(path + kisy + "_summary.csv")) {
            pw.println(kisy+",:,"+syaryoMap.size());
            
            List head = new ArrayList();head.add("ヘッダ");
            List data = new ArrayList();data.add("データ数");
            List num = new ArrayList();num.add("車両数");
            for(Object header : dataSizeMap.keySet()){
                head.add(header);
                data.add(dataSizeMap.get(header).toString());
                num.add(numSyaryoMap.get(header).toString());
            }
            
            pw.println(String.join(",", head));
            pw.println(String.join(",", data));
            pw.println(String.join(",", num));
        }
        
        //1Sample SyaryoObject
        Map map = new HashMap();
        map.put(sample.name, sample);
        new SyaryoObjToJson().write(path + kisy + "_sample.json", map);
    }
}
