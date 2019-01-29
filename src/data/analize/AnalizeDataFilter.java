/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.analize;

import data.code.CodeRedefine;
import file.UserDefinedFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import obj.SyaryoObject;
import param.KomatsuDataParameter;
import param.KomatsuUserParameter;

/**
 *
 * @author ZZ17390
 */
public class AnalizeDataFilter {
    public static void partsdatafilter(Map<String, SyaryoObject> syaryoMap, SyaryoObject dataHeader) {
        //定期メンテ削除
        List<String> reject = new ArrayList();
        List mainte = KomatsuDataParameter.MAINTE_DEFINE.get("受注.SGYO_KTICD");
        int idx = dataHeader.get("受注").get("受注").indexOf("SGYO_KTICD");
        int order_idx = dataHeader.get("受注").get("受注").indexOf("ODR_KBN");
        int idx2 = dataHeader.get("部品").get("部品").indexOf("HNBN");
        int idx3 = dataHeader.get("部品").get("部品").indexOf("None");
        int idx4 = dataHeader.get("部品").get("部品").indexOf("BHN_NM");
        int idx_q = dataHeader.get("部品").get("部品").indexOf("JISI_SU");
        int idx_p = dataHeader.get("部品").get("部品").indexOf("SKKG");
        
        //PC200 部品フィルタ
        List filter = UserDefinedFile.filter(KomatsuUserParameter.PC200_PARTSFILTER_FILE);
        
        syaryoMap.values().stream().forEach(s -> {
            if (s.get("部品") == null) {
                reject.add(s.name);
                return;
            }
            
            s.startHighPerformaceAccess();

            Map<String, List<String>> parts = s.get("部品").entrySet().stream()
                    .filter(p -> !mainte.contains(s.get("受注").get(p.getKey().split("#")[0]).get(idx))) //定期メンテ削除
                    .filter(p -> s.get("受注").get(p.getKey().split("#")[0]).get(order_idx).equals("2")) //修販
                    .filter(p -> p.getValue().get(idx3).equals("10"))   //コマツ部品のみ
                    .filter(p -> p.getValue().get(idx2).toString().split("-").length > 2) //コマツコード体系
                    .filter(p -> !filter.contains(p.getValue().get(idx2))) //除外部品
                    .filter(p -> KomatsuUserParameter.PARTS_FILTER_PRICE <= 
                            (Integer.valueOf(p.getValue().get(idx_p).toString()) / Integer.valueOf(p.getValue().get(idx_q).toString()))) //金額での除外
                    .collect(Collectors.toMap(p -> p.getKey(), p -> p.getValue()));
            
            
            if (!parts.isEmpty()) {
                s.put("部品", parts);
            } else {
                reject.add(s.name);
            }
            
            s.stopHighPerformaceAccess();
        });
        
        System.out.println("削除車両:"+reject.size()+":"+ reject);
        for (String name : reject) {
            syaryoMap.remove(name);
        }
    }
    
    public static void workdatafilter(Map<String, SyaryoObject> syaryoMap, SyaryoObject dataHeader) {
        //定期メンテ削除
        List<String> reject = new ArrayList();
        List mainte = KomatsuDataParameter.MAINTE_DEFINE.get("受注.SGYO_KTICD");
        int idx = dataHeader.get("受注").get("受注").indexOf("SGYO_KTICD");
        int idx2 = dataHeader.get("作業").get("作業").indexOf("SGYOCD");
        syaryoMap.values().stream().forEach(s -> {
            if (s.get("作業") == null) {
                return;
            }

            Map works = s.get("作業").entrySet().stream()
                    .filter(w -> !mainte.contains(s.get("受注").get(w.getKey().split("#")[0]).get(idx)))
                    .filter(w -> !w.getValue().get(idx2).equals("ZZU"))
                    .filter(w -> !w.getValue().get(idx2).equals("None"))
                    .collect(Collectors.toMap(w -> w.getKey(), w -> w.getValue()));

            if (works != null) {
                s.put("作業", works);
            } else {
                reject.add(s.name);
            }
        });

        System.out.println("削除車両:" + reject);
        for (String name : reject) {
            syaryoMap.remove(name);
        }
    }
}
