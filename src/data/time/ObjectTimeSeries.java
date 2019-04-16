/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.time;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import obj.SyaryoLoader;
import obj.SyaryoObject;
import param.KomatsuUserParameter;
import param.obj.UserPartsObject;

/**
 *
 * @author ZZ17807
 */
public class ObjectTimeSeries {
    private static SyaryoLoader LOADER = SyaryoLoader.getInstance();
    private static UserPartsObject PARTS = KomatsuUserParameter.PC200_USERPARTS_DEF;
    
    public static List<String> toSeries(SyaryoObject s, String key, String target) {
        List<String> t;
        if(key.equals("受注"))
            t = partsToSeries(s, target);
        else
            t = komtraxErrorToSeries(s, key, target);
        
        if(!t.isEmpty())
            return t;
        else
            return null;
    }
    
    private static List<String> partsToSeries(SyaryoObject s, String target){
        List<String> t = new ArrayList<>();
        if(PARTS.check(s.name))
            t = PARTS.index.get(s.name).entrySet().stream() //ユーザー定義の部品
                            .filter(e -> e.getValue().equals(target)) //ターゲット以外の部品は除去
                            .map(e -> e.getKey()[1])                 //ユーザー定義部品の作番情報
                            .map(sbn -> s.get("受注").get(sbn).get(LOADER.index("受注", "SGYO_KRDAY"))) //作業完了日
                            .collect(Collectors.toList());
        
        return t;
    }
    
    private static List<String> komtraxErrorToSeries(SyaryoObject s, String key, String target){
        List<String> t = new ArrayList<>();
        s.startHighPerformaceAccess();
        if(s.get(key) != null){
            t = s.get(key).entrySet().stream()
                        .filter(e -> e.getValue().get(LOADER.index(key, "ERROR_CODE")).equals(target))
                        .map(e -> e.getKey())
                        .distinct()
                        .collect(Collectors.toList());
        }
        s.stopHighPerformaceAccess();
        
        return t;
    }
    
    //test
    public static void main(String[] args) {
        LOADER.setFile("PC200_form");
        
        Map<String, List<String>> test = new HashMap();
        LOADER.getSyaryoMap().values().stream().forEach(s ->{
            List t = ObjectTimeSeries.toSeries(s, "受注", "オルタネータ");
            if(t != null)
                test.put(s.name, t);
        });
        System.out.println("オルタネータ　時系列");
        test.entrySet().stream().map(e -> e.getKey()+","+e.getValue()).forEach(System.out::println);
        
        Map<String, List<String>> test2 = new HashMap();
        LOADER.getSyaryoMap().values().stream().forEach(s ->{
            List t = ObjectTimeSeries.toSeries(s, "KOMTRAX_ERROR", "AB00KE");
            if(t!= null)
                test2.put(s.name, t);
        });
        
        System.out.println("\nKOMTRAX_ERR[AB00KE]　時系列");
        test2.entrySet().stream().map(e -> e.getKey()+","+e.getValue()).forEach(System.out::println);
    }
}
