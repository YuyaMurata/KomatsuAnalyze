/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.time;

import analizer.SyaryoAnalizer;
import obj.SyaryoLoader;

/**
 *
 * @author kaeru
 */
public class TimeVector {

    static SyaryoLoader LOADER = SyaryoLoader.getInstance();

    public static void main(String[] args) throws Exception {
        LOADER.setFile("PC200_form");
        try (SyaryoAnalizer a = new SyaryoAnalizer(LOADER.getSyaryoMap().get("PC200-10-452525"), true)) {
            //エラー879AKA
            TimeSeriesObject t879AKA = new TimeSeriesObject(a, "KOMTRAX_ERROR", "879AKA");
            System.out.println(t879AKA.series);
            //エラー879EMC
            TimeSeriesObject t879EMC = new TimeSeriesObject(a, "KOMTRAX_ERROR", "879EMC");
            System.out.println(t879EMC.series);
            //部品エアコン
            TimeSeriesObject tair = new TimeSeriesObject(a, "受注", "エアコン");
            System.out.println(tair.series);
        }
    }
    
    //ベクタ
    public static void toVector(SyaryoAnalizer a, TimeSeriesObject st, TimeSeriesObject mt, TimeSeriesObject gt, Integer dsmr){
        
    }
}
