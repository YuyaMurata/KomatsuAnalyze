/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.eval;

import analizer.SyaryoAnalizer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import obj.SyaryoObject;

/**
 * 車両評価
 *
 * @author ZZ17390
 */
public class EvaluateSyaryoObject {
    public Map<String, Map<String, Double>> eval = new LinkedHashMap<>();
    public String name, company;
    public Integer day, smr, rent;

    public EvaluateSyaryoObject(SyaryoObject syaryo) {
        try (SyaryoAnalizer analize = new SyaryoAnalizer(syaryo, false)) {
            this.name = analize.get().name;
            this.company = analize.mcompany;
            this.day = analize.currentAge_day;
            this.smr = analize.maxSMR[3];
            
            //eval.put("use", useData(analize));
            //eval.put("age/smr", agingSMRData(analize));
            eval.put("mainte",mainteData(analize));
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println(syaryo.dump());
            System.exit(0);
        }
    }
    
    public Boolean isNULL(String select){
        return eval.get(select) == null;
    }

    /**
     * 使われ方のデータ 定義 : 累積負荷 / SMR
     *
     */
    private Map<String, Double> useData(SyaryoAnalizer syaryo) {
        //負荷マップの一部を正規化
        Map<String, Double> engine = UseEvaluate.nomalize(syaryo, "LOADMAP_実エンジン回転VSエンジントルク");
        
        return engine;
    }

    /**
     * 経年/SMRのデータ 定義 : v[経年, ACT_SMR]
     */
    private Map agingSMRData(SyaryoAnalizer syaryo) {
        return null;
    }

    /**
     * メンテナンスデータ 定義 : v[エンジンメンテ, 油圧メンテ, 定期メンテ] エンジンメンテ = エンジンOIL交換回数 / (SMR /
     * インターバル) 油圧メンテ = 作動機オイル交換 / (SMR / インターバル) 定期メンテ = 作業形態回数 / (経年 / インターバル)
     */
    private Map<String, Double> mainteData(SyaryoAnalizer syaryo) {
        Map<String, Double> quantity = MainteEvaluate.aggregate(syaryo, "-1", "-1");
        Map<String, Double> quality = MainteEvaluate.nomalize(quantity, smr, day);
        
        //return quantity;
        return quality;
    }
    
    public List<Double> getData(String select){
        //System.out.println(eval.get(select).values());
        return new ArrayList(eval.get(select).values());
    }
    
    public static String printh(String select){
        switch(select){
            case "mainte" :
                return String.join(",", MainteEvaluate.header());
            case "use" :
                return String.join(",", UseEvaluate.header());
        }
        
        return "none";
    }
    
    public String print(String select){
        if(eval.get(select) != null)
            return eval.get(select).values().stream().map(e -> e.toString()).collect(Collectors.joining(","));
        
        return "none";
    }
}
