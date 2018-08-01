/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package summary;

import file.CSVFileReadWrite;
import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import json.SyaryoObjToJson;
import json.SyaryoToZip3;
import obj.SyaryoObject4;
import param.KomatsuDataParameter;

/**
 *
 * @author ZZ17390
 */
public class SyaryoDataSummary {

    private static String kisy = "PC138US";
    private static String path = KomatsuDataParameter.OBJECT_PATH;
    private static String outpath = KomatsuDataParameter.SUMMARY_PATH+kisy+"\\";
    
    
    public static void main(String[] args) {
        SyaryoObject4 sample = null;
        int size = 0;
        
        //Create Folder
        if(!(new File(outpath)).exists())
            (new File(outpath)).mkdirs();
        
        String filename = path + "syaryo_obj_" + kisy + ".bz2";
        Map<String, SyaryoObject4> syaryoMap = new SyaryoToZip3().read(filename);
        
        //Syaryo Data Check
        int cnt = 0;
        Map<Object, Integer> dataSizeMap = new HashMap();
        Map<Object, Integer> numSyaryoMap = new HashMap();
        for(SyaryoObject4 syaryo : syaryoMap.values()){
            int total = 0;
            for(Object key : syaryo.getMap().keySet()){
                if(syaryo.get(key.toString()) == null){
                    System.out.println(syaryo.name+":"+key.toString());
                    continue;
                }
                
                int s = syaryo.get(key.toString()).size();
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
                sample = syaryo;
                size = total;
            }
            
            if((++cnt)%1000 == 0)
                System.out.println(kisy+":"+cnt+"件処理");
        }
        
        //Summary Output
        try (PrintWriter pw = CSVFileReadWrite.writer(outpath + kisy + "_summary_raw.csv")) {
            pw.println(kisy+",:,"+syaryoMap.size());
            
            List head = new ArrayList();head.add("ヘッダ");
            List data = new ArrayList();data.add("データ数");
            List num = new ArrayList();num.add("車両数");
            for(String header : KomatsuDataParameter.DATA_ORDER){
                if(dataSizeMap.get(header) == null)
                    continue;
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
        new SyaryoObjToJson().write(outpath + kisy + "_sample_raw.json", map);
    }
}
