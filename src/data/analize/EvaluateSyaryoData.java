/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.analize;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import obj.SyaryoObject4;

/**
 *
 * @author ZZ17390
 */
public class EvaluateSyaryoData {
    public void evaluate(SyaryoObject4 syaryo) {
        
        Map evalMap = new HashMap();
        evalMap.putAll(useData(syaryo));
        evalMap.putAll(agingSMRData(syaryo));
        evalMap.putAll(mainteData(syaryo));
        
    }
    
    
    /**
     * 使われ方のデータ
     * 定義 : 累積負荷 / SMR
     * 
    */
    private Map useData(SyaryoObject4 syaryo){
        return null;
    }
    
    /**
     * 経年/SMRのデータ
     * 定義 : v[経年, ACT_SMR]
     */
    private Map agingSMRData(SyaryoObject4 syaryo){
        Map agesmr = new TreeMap();
        return null; 
    }
    
    /**
     * メンテナンスデータ
     * 定義 : v[エンジンメンテ, 油圧メンテ, 定期メンテ]
     * エンジンメンテ = エンジンOIL交換回数 / (SMR / インターバル)
     * 油圧メンテ = 作動機オイル交換 / (SMR / インターバル)
     * 定期メンテ = 作業形態回数 / (経年 / インターバル)
     */
    private Map mainteData(SyaryoObject4 syaryo){
        return null;
    }
}
