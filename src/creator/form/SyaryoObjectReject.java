/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package creator.form;

import file.SyaryoToCompress;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import obj.SyaryoLoader;
import obj.SyaryoObject;
import param.KomatsuUserParameter;

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
        service(syaryoMap, "20170501");
        syaryo(syaryoMap);
        //komtrax(syaryoMap, "KOMTRAX_ACT_DATA");
        //smr(syaryoMap, "KOMTRAX_SMR");
        //noop(syaryoMap, "KOMTRAX_SMR", "2017");
        
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

            if (t.contains(typ)) {
                m.put(k, map.get(k));
            } else {
                if (detail.get(typ) == null) {
                    detail.put(typ, 0);
                }
                detail.put(typ, detail.get(typ) + 1);
            }
        });
        
        map.clear();
        map.putAll(m);
        
        System.out.println("Number of Syaryo (型) : " + map.size());
        System.out.println("          Detail : " + detail);
    }
    
    //車両マスタの仕様情報から車両を削除
    private static void syaryo(Map<String, SyaryoObject> map){
        System.out.println("仕様情報から車両の削除処理 data=[サブディーラ担当ポイントコード]");
        System.out.println("Before Number of Syaryo : " + map.size());
        
        List<String> DEALER_LIST = KomatsuUserParameter.DEALER_REJECT_LIST;
        System.out.println("リスト_"+DEALER_LIST);
        
        String specNo = "0";
        final int comp_idx = 0;
        final int dealer_idx = 7;
        
        Map<String, SyaryoObject> m = new TreeMap<>();
        Map<String, Integer> rej = new TreeMap<>();
        map.values().parallelStream().forEach(s ->{
            String comp = s.get("仕様").get(specNo).get(comp_idx);
            String dealer = s.get("仕様").get(specNo).get(dealer_idx);
            
            if(DEALER_LIST.contains(comp+","+dealer)){
                if(rej.get(comp+","+dealer) == null)
                    rej.put(comp+","+dealer, 0);
                rej.put(comp+","+dealer, rej.get(comp+","+dealer)+1);
            }else
                m.put(s.name, s);
        });
        
        map.clear();
        map.putAll(m);
        
        System.out.println("Number of Syaryo (SPEC) : " + map.size());
        System.out.println("          Detail : " + rej);
    }
    
    //KOMTRAX対応していない車両の削除
    private static void komtrax(Map<String, SyaryoObject> map, String baseKey){
        System.out.println("KOMTRAX非対応車両の処理 data=["+baseKey+"]");
        System.out.println("Before Number of Syaryo : " + map.size());
        
        map = map.entrySet().stream()
                        .filter(e -> e.getValue().get(baseKey) != null)
                        .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
        
        System.out.println("Number of Syaryo (KOMTRAX) : " + map.size());
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
        
        System.out.println("Number of Syaryo (SMR正常) : " + map.size());
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
        
        System.out.println("Number of Syaryo (稼働) : " + map.size());
    }
    
    //サービス情報から車両を削除
    private static void service(Map<String, SyaryoObject> map, String date){
        System.out.println("サービス情報による車両の処理 data=[カンパニ UR, GC] 新車納入 : "+date+" 以降の車両の削除");
        System.out.println("Before Number of Syaryo : " + map.size());
        
        //カンパニ車両除去
        Map companyMap = map.entrySet().parallelStream().filter(e -> !e.getValue().get("最終更新日").values().stream()
                                                            .filter(c -> c.contains("UR") || c.contains("GC"))
                                                            .findFirst().isPresent()
                                                ).collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
        
        map.clear();
        map.putAll(companyMap);
        System.out.println("Number of Syaryo (会社) : " + map.size());
        
        ///新車除去
        Map newMap = map.entrySet().parallelStream().filter(e -> !(e.getValue().get("新車")==null ? 
                                                        e.getValue().get("生産").keySet().stream().filter(d -> Integer.valueOf(date) <= Integer.valueOf(d.split("#")[0])).findFirst().isPresent() : 
                                                        e.getValue().get("新車").keySet().stream().filter(d -> Integer.valueOf(date) <= Integer.valueOf(d.split("#")[0])).findFirst().isPresent() )
                                                ).collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
        
        map.clear();
        map.putAll(newMap);
        System.out.println("Number of Syaryo (新車) : " + map.size());
    }
}
