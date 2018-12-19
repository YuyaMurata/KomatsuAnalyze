/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package agent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import obj.LoadSyaryoObject;
import obj.SyaryoObject;
import param.KomatsuDataParameter;

/**
 *
 * @author ZZ17390
 */
public class AgentSettingTools {
    private static LoadSyaryoObject LOADER = KomatsuDataParameter.LOADER;
    
    public static Map<String, List<String>> setDateToSBNs(SyaryoObject syaryo){
        Map<String, List<String>> dateToSBN = new HashMap();
        String key = "受注";
        int idx = LOADER.index(key, "ODDAY");
        
        syaryo.get(key).entrySet().stream().forEach(e -> {
            String date = e.getValue().get(idx).toString();
            if (dateToSBN.get(date) == null) {
                dateToSBN.put(date, new ArrayList<>());
            }

            //受注の変換
            dateToSBN.get(date).add(key + "." + e.getKey());

            //作業の変換
            dateToSBN.get(date).addAll(
                syaryo.get("作業").keySet().stream()
                    .filter(wsbn -> wsbn.contains(e.getKey()))
                    .collect(Collectors.toList())
            );
            
            //部品の変換
            dateToSBN.get(date).addAll(
                syaryo.get("部品").keySet().stream()
                    .filter(psbn -> psbn.contains(e.getKey()))
                    .collect(Collectors.toList())
            );
        });
        
        return dateToSBN;
    }
}
