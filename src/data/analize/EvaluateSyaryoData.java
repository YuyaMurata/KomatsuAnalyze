/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.analize;

/**
 *
 * @author ZZ17390
 */
public class EvaluateSyaryoData {
    public static void main(String[] args) {
        
    }
    
    
    /**
     * 使われ方のデータ
     * 定義 : 累積負荷 / SMR
     * 
    */
    public static void useData(){
        
        
        
    }
    
    /**
     * 経年/SMRのデータ
     * 定義 : v[経年, ACT_SMR]
     */
    public static void agingSMRData(){
        
    }
    
    /**
     * メンテナンスデータ
     * 定義 : v[エンジンメンテ, 油圧メンテ, 定期メンテ]
     * エンジンメンテ = エンジンOIL交換回数 / (SMR / インターバル)
     * 油圧メンテ = 作動機オイル交換 / (SMR / インターバル)
     * 定期メンテ = 作業形態 / (経年 / インターバル)
     */
    public static void mainteData(){
        
    }
}
