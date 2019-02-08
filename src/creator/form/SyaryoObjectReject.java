/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package creator.form;

import creator.create.AttachedLayoutIndex;
import file.SyaryoToCompress;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import obj.SyaryoLoader;
import obj.SyaryoObject;

/**
 *
 * @author ZZ17390
 */
public class SyaryoObjectReject {
    private static SyaryoLoader LOADER = SyaryoLoader.getInstance();
    private static String KISY = "PC200";
    
    public static void main(String[] args) {
        LOADER.setFile(KISY);
        Map<String, SyaryoObject> syaryoMap = LOADER.getSyaryoMap();
        
        type(syaryoMap, Arrays.asList(new String[]{"8", "8N1", "10"}));
        komtrax(syaryoMap, "KOMTRAX_SMR");
        smr(syaryoMap, "KOMTRAX_SMR");
        noop(syaryoMap, "KOMTRAX_SMR", "2017");
        
        //レイアウトを付加
        AttachedLayoutIndex.attached(syaryoMap);
        
        LOADER.close();
        new SyaryoToCompress().write(LOADER.getFilePath(), syaryoMap);
        
    }

    //型による車両排除
    private static void type(Map<String, SyaryoObject> map, List<String> t) {
        System.out.println(t+"型 車両の処理");
        System.out.println("Before Number of Syaryo : " + map.size());

        Map<String, SyaryoObject> m = new TreeMap<>();
        Map<String, Integer> detail = new HashMap<>();
        map.keySet().stream().forEach(k -> {
            String typ = k.split("-")[1];

            if (t.contains(k)) {
                m.put(k, map.get(k));
            } else {
                if (detail.get(k) == null) {
                    detail.put(k, 0);
                }
                detail.put(k, detail.get(k) + 1);
            }
        });
        
        map.clear();
        map.putAll(m);
        
        System.out.println("Number of Syaryo : " + map.size());
        System.out.println("          Detail : " + detail);
    }
    
    //KOMTRAX対応していない車両の削除
    private static void komtrax(Map<String, SyaryoObject> map, String baseKey){
        System.out.println("KOMTRAX非対応車両の処理 data=["+baseKey+"]");
        System.out.println("Before Number of Syaryo : " + map.size());
        
        map = map.entrySet().stream()
                        .filter(e -> e.getValue().get(baseKey) != null)
                        .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
        
        System.out.println("Number of Syaryo : " + map.size());
    }
    
    //SMR異常車両の削除
    private static void smr(Map<String, SyaryoObject> map, String baseKey){
        System.out.println("SMR異常車両の処理");
        System.out.println("Before Number of Syaryo : " + map.size());
        
        //SMR異常車両の検出
        Map<String, List> ab = AbnomalyDetection.smr(map, baseKey);
        
        //異常車両除去
        map = map.entrySet().stream()
                        .filter(e -> ab.get(e.getKey()) == null)
                        .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
        
        System.out.println("Number of Syaryo : " + map.size());
    }
    
    //非稼動車両の削除
    private static void noop(Map<String, SyaryoObject> map, String baseKey, String date){
        System.out.println("非稼動車両の処理 data=["+baseKey+"] 稼動判定:"+date);
        System.out.println("Before Number of Syaryo : " + map.size());
        
        Map<String, String> ab = AbnomalyDetection.nonActive(map, baseKey, date);
        
        //非稼動車両除去
        map = map.entrySet().stream()
                        .filter(e -> ab.get(e.getKey()) == null)
                        .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
        
        System.out.println("Number of Syaryo : " + map.size());
    }
}
