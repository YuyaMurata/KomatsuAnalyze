/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.eval;

import analizer.SyaryoAnalizer;
import data.time.TimeSeriesObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import param.KomatsuUserParameter;

/**
 *
 * @author ZZ17390
 */
public class MainteEvaluate extends EvaluateTemplate {

    private static Map<String, String> INTERVAL = KomatsuUserParameter.PC200_MAINTEPARTS_INTERVAL;

    public MainteEvaluate() {
        setHeader("メンテナンス", new ArrayList<>(INTERVAL.keySet()));
    }

    //正規化
    @Override
    public Map<String, Double> normalize(SyaryoAnalizer s, String key, Map<String, List<String>> data) {
        Map norm = header(key).stream()
                .collect(Collectors.toMap(
                        h -> h,
                        h -> data.get(h).stream()
                                .map(ti -> Integer.valueOf(ti) > 0 ? 1 : 0)
                                .mapToDouble(ti -> ti.doubleValue())
                                .average().getAsDouble(),
                        (h1, h2) -> h1,
                        LinkedHashMap::new
                ));

        return norm;
    }

    //時系列のメンテナンスデータ取得
    @Override
    public Map<String, List<String>> aggregate(SyaryoAnalizer s) {
        //System.out.println(s.name);
        Map<String, List<String>> data = new HashMap<>();

        //時系列情報の取得
        INTERVAL.entrySet().stream()
                .forEach(e -> {
                    String check = "";
                    try {
                        TimeSeriesObject t = new TimeSeriesObject(s, "受注", e.getKey());
                        List<Integer> smr = t.series.values().stream()
                                .map(ti -> ti.split("#")[0])
                                .map(ti -> s.getDateToSMR(ti).getValue())
                                .collect(Collectors.toList());

                        //計算上の最大SMRを取得
                        Integer max = -1;
                        if (!smr.isEmpty()) {
                            max = smr.stream().mapToInt(v -> v).max().getAsInt();
                        }
                        max = s.maxSMR[4] > max ? s.maxSMR[4] : max;

                        Integer len = max / Integer.valueOf(e.getValue());
                        List<String> series = IntStream.range(0, len + 1).boxed().map(i -> "0").collect(Collectors.toList());
                        
                        check = t.sid + ":" + s.maxSMR[4] + ":" + t.series + smr;

                        smr.stream()
                                .map(v -> v == 0 ? 1 : v) //0h交換での例外処理
                                .map(v -> (v % Integer.valueOf(e.getValue())) == 0 ? v - 1 : v) //インターバル時間で割り切れる場合の例外処理
                                .forEach(v -> {
                                    int i = v / Integer.valueOf(e.getValue());
                                    series.set(i, v.toString());
                                });

                        //各車両のメンテナンス状況を記録
                        data.put(e.getKey(), series);

                        //System.out.println(t.sid+","+(s.age(s.getSMRToDate(max))/365)+","+max+","+t.target+","+Arrays.asList(series).stream().map(v -> v.toString()).collect(Collectors.joining(",")));
                    } catch (IndexOutOfBoundsException ie) {
                        System.err.println(check);
                    }

                });

        return data;
    }

    //Test
    public static void main(String[] args) {
        LOADER.setFile("PC200_form");
        SyaryoAnalizer.rejectSettings(false, false, false);
        SyaryoAnalizer s =  new SyaryoAnalizer(LOADER.getSyaryoMap().get("PC200-10-450635"), true);

        MainteEvaluate mainte = new MainteEvaluate();

        Map<String, List<String>> data = mainte.getdata(s);
        Map<String, Double> result = mainte.evaluate("メンテナンス", s);

        data.entrySet().stream().forEach(d -> {
            System.out.println(d.getKey());
            System.out.println("  " + d.getValue());
            System.out.println("  " + result.get(d.getKey()));
        });
        
        //クラスタ用データ
        System.out.println("\n"+mainte.header("メンテナンス"));
        System.out.println(mainte.getClusterData("メンテナンス"));
    }
}
