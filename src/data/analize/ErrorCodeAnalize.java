/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.analize;

import file.CSVFileReadWrite;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import obj.SyaryoLoader;
import obj.SyaryoObject;

/**
 *
 * @author ZZ17390
 */
public class ErrorCodeAnalize {
    private static String KISY = "PC200";
    private static SyaryoLoader LOADER = SyaryoLoader.getInstance();    
    
    public static void main(String[] args) {
        LOADER.setFile(KISY+"_form");
        acmErrorCount(LOADER.getSyaryoMap());
    }
    
    /**
     * エラーコードを発生台数と総カウント数で表記
     */
    private static void acmErrorCount(Map<String, SyaryoObject> map){
        int idx_cd = LOADER.index("KOMTRAX_ERROR", "ERROR_CODE");
        int idx_cnt = LOADER.index("KOMTRAX_ERROR", "VALUE");
        Map<String, Integer[]> acm = new TreeMap();
        
        for(SyaryoObject s : map.values()){
            Map<String, Integer> max = new HashMap<>();
            Map<String, List<String>> err = s.get("KOMTRAX_ERROR");
            
            if(err == null)
                continue;
            
            err.values().stream().forEach(e ->{
                String cd = e.get(idx_cd);
                Integer cnt = Integer.valueOf(e.get(idx_cnt));
                
                if(max.get(cd) == null)
                    max.put(cd, 0);
                
                if(max.get(cd) < cnt)
                    max.put(cd, cnt);
            });
            
            //集計
            for(String cd : max.keySet()){
                if(acm.get(cd) == null)
                    acm.put(cd, new Integer[]{0, 0});
                
                acm.get(cd)[0] += 1;
                acm.get(cd)[1] += max.get(cd);
            }
        }
        
        try(PrintWriter pw = CSVFileReadWrite.writerSJIS(KISY+"_エラーコードの発生台数と総カウント数.csv")){
            pw.println("エラーコード,発生台数,総発生数");
            acm.entrySet().stream().map(e -> e.getKey()+","+e.getValue()[0]+","+e.getValue()[1]).forEach(pw::println);
        }
    }
}
