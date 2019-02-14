/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.eval;

import analizer.SyaryoAnalizer;
import data.eval.MainteEvaluate;
import data.eval.UseEvaluate;
import java.util.LinkedHashMap;
import java.util.Map;
import obj.SyaryoLoader;
import obj.SyaryoObject;
import param.KomatsuDataParameter;

/**
 * 車両評価
 *
 * @author ZZ17390
 */
public class EvaluateSyaryoObject {
    public Map<String, Double> evalMainte = new LinkedHashMap<>();
    public Map<String, Double> evalAgeSMR = new LinkedHashMap<>();
    public Map<String, Double> evalUsage = new LinkedHashMap<>();
    public String name, company;
    public Integer day, smr, rent;

    public EvaluateSyaryoObject(SyaryoObject syaryo) {
        try (SyaryoAnalizer analize = new SyaryoAnalizer(syaryo, false)) {
            this.name = analize.get().name;
            this.company = analize.mcompany;
            //this.rent = analize.rent;
            this.day = analize.currentAge_day;
            this.smr = analize.maxSMR[3];

            //evalAgeSMR.putAll();
            evalUsage = useData(analize);
            //evalMainte.putAll(mainteData(analize));
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println(syaryo.dump());
            System.exit(0);
        }
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
    private Map agingSMRData(SyaryoObject syaryo) {
        return null;
    }

    /**
     * メンテナンスデータ 定義 : v[エンジンメンテ, 油圧メンテ, 定期メンテ] エンジンメンテ = エンジンOIL交換回数 / (SMR /
     * インターバル) 油圧メンテ = 作動機オイル交換 / (SMR / インターバル) 定期メンテ = 作業形態回数 / (経年 / インターバル)
     */
    private Map mainteIndex = KomatsuDataParameter.PC_PID_DEFNAME;
    private Map<String, Double> mainteData(SyaryoAnalizer syaryo) {
        Map<String, Integer> quantity = MainteEvaluate.aggregate(syaryo, "-1", "-1");
        Map<String, Double> quality = MainteEvaluate.evaluate(quantity, smr, day);
        
        return quality;
    }

    
    public static void main(String[] args) {
        
    }
}
