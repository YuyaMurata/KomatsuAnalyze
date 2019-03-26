/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.detect;

import static data.detect.AbnomalyDection.detectChiSquare;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import obj.SyaryoLoader;
import param.KomatsuDataParameter;

/**
 *
 * @author ZZ17390
 */
public class AccidentDetect {

    private static List<String> ACCIDENT_WORDS = KomatsuDataParameter.ACCIDENT_WORDS;
    private static SyaryoLoader LOADER = SyaryoLoader.getInstance();
    
    public static List<String> wordsDetect(Map<String, List<String>> service, Map<String, List<String>> work) {
        int sv_txt = LOADER.index("受注", "GAIYO_1");
        int sv_txt2 = LOADER.index("受注", "GAIYO_2");
        int wk_txt = LOADER.index("作業", "SGYO_NM");
        
        List<String> sbns = new ArrayList<>();
        
        //サービス概要から検出
        if(service != null)
            sbns.addAll(
                service.entrySet().stream()
                    .filter(e -> stopwords(e.getValue().get(sv_txt)+e.getValue().get(sv_txt2)))
                    .map(e -> e.getKey())
                    .collect(Collectors.toList())
            );
        
        //作業名から検出
        if(work != null)
            sbns.addAll(
                work.entrySet().stream()
                    .filter(e -> stopwords(e.getValue().get(wk_txt)))
                    .map(e -> e.getKey().split("#")[0])
                    .collect(Collectors.toList())
            );
        
        sbns = sbns.stream().distinct().collect(Collectors.toList());
        
        return sbns;
    }
    
    private static List<String> priceDetect(Map<String, String> allservice){
        Map<String, String> map = detectChiSquare(allservice, 0.01, true);
        
        return new ArrayList<>(map.keySet());
    }

    public static Boolean stopwords(String str) {
        return ACCIDENT_WORDS.parallelStream().filter(w -> str.contains(w)).findFirst().isPresent();
    }
}
