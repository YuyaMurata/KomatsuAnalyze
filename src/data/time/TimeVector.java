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
        LOADER.setFile("PC200_loadmap");
        LOADER.getSyaryoMap().values().stream().forEach(s -> {
            try (SyaryoAnalizer a = new SyaryoAnalizer(s, true)) {
                //エラー879AKA
                TimeSeriesObject t879AKA = new TimeSeriesObject(a, "KOMTRAX_ERROR", "879AKA");

                //エラー879EMC
                TimeSeriesObject t879EMC = new TimeSeriesObject(a, "KOMTRAX_ERROR", "879EMC");

                //部品エアコン
                TimeSeriesObject tair = new TimeSeriesObject(a, "受注", "エアコン");

                if (!tair.isEmpty() && !t879EMC.isEmpty()) {
                    System.out.println(a.name);
                    System.out.println(t879AKA.series);
                    System.out.println(t879EMC.series);
                    System.out.println(tair.series);
                    toVector(a, t879AKA, t879EMC, tair, 500);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                System.exit(0);
            }
        });
    }

    //ベクタ
    public static void toVector(SyaryoAnalizer a, TimeSeriesObject st, TimeSeriesObject mt, TimeSeriesObject gt, Integer dsmr) {
        Integer vec[][] = new Integer[2][10];
        Arrays.fill(vec[0], 0);
        Arrays.fill(vec[1], 0);

        List<String> m = mt.floorExt(gt.firstService());
        List<String> s = st.floorExt(m.get(0));

        //最大dSMR
        Integer base = a.getDateToSMR(gt.firstService()).getKey();
        s.stream().map(d -> (base - a.getDateToSMR(d).getKey()) / dsmr).forEach(y -> vec[0][y] += 1);
        m.stream().map(d -> (base - a.getDateToSMR(d).getKey()) / dsmr).forEach(y -> vec[1][y] += 1);

        System.out.println("        :" + IntStream.range(0, 4).boxed().map(i -> i * dsmr).collect(Collectors.toList()));
        System.out.println(gt.target + ":" + base + "(" + gt.firstService() + ")");
        System.out.println(mt.target + ":" + Arrays.asList(vec[1]));
        System.out.println(st.target + ":" + Arrays.asList(vec[0]));
    }
}
