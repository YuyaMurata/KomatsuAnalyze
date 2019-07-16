/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.time;

import analizer.SyaryoAnalizer;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import obj.SyaryoLoader;

/**
 *
 * @author kaeru
 */
public class TimeVector {

    static SyaryoLoader LOADER = SyaryoLoader.getInstance();

    public static void main(String[] args) {
        SyaryoAnalizer.DISP_COUNT = false;
        
        LOADER.setFile("PC200_form");
        LOADER.getSyaryoMap().values().stream().forEach(s -> {
            try (SyaryoAnalizer a = new SyaryoAnalizer(s, true)) {
                //エラー879AKA
                //TimeSeriesObject t879AKA = new TimeSeriesObject(a, "KOMTRAX_ERROR", "879AKA");

                //エラー879EMC
                //TimeSeriesObject t879EMC = new TimeSeriesObject(a, "KOMTRAX_ERROR", "879EMC");
                
                //エラーCA778
                TimeSeriesObject cause1 = new TimeSeriesObject(a, "KOMTRAX_ERROR", "B@BCNS");
                
                //部品エアコン
                TimeSeriesObject target = new TimeSeriesObject(a, "受注", "カテゴリ1-1");

                if (!target.isEmpty() && !cause1.isEmpty()) {
                    System.out.println(a.name);
                    System.out.println(cause1.series);
                    System.out.println(target.series);
                    toVector(a, cause1, target, 500);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                System.exit(0);
            }
        });
    }

    //ベクタ
    public static void toVector(SyaryoAnalizer a, TimeSeriesObject st, TimeSeriesObject gt, Integer dsmr) {
        Integer vec[][] = new Integer[2][10];
        Arrays.fill(vec[0], 0);
        //Arrays.fill(vec[1], 0);
        
        //原因以降のサービスを調査
        List<String> sv = gt.upperExt(st.firstService());
        System.out.println(sv);
        
        if(sv.isEmpty()){
            System.out.println(gt.target+":"+gt.firstService());
            System.out.println(st.target+":"+st.firstService());
            return ;
        }
            
        
        //サービスの過去原因を調査
        List<String> ca = st.floorExt(sv.get(0));
        List<String> caf = st.upperExt(sv.get(0));
        System.out.println(ca);
        System.out.println(caf);
        
        //最大dSMR
        Integer base = a.getDateToSMR(sv.get(0)).getKey();
        ca.stream().map(d -> (base - a.getDateToSMR(d).getKey()) / dsmr).forEach(y -> vec[0][y+1] += 1);
        if(!caf.isEmpty())
            vec[0][0] = (a.getDateToSMR(caf.get(0)).getKey() - base) < 100 ? 1 : 0;
        else
            vec[0][0] = 0;
        
        System.out.println("        : -100," + IntStream.range(1, vec[0].length).boxed().map(i -> (i-1) * dsmr).collect(Collectors.toList()));
        System.out.println(gt.target + ":" + base + "(" + gt.firstService() + ")");
        //System.out.println(mt.target + ":" + Arrays.asList(vec[1]));
        System.out.println(st.target + ":" + Arrays.asList(vec[0]));
    }
}
