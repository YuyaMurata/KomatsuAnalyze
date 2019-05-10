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
import java.util.TreeMap;
import java.util.stream.Collectors;
import obj.SyaryoLoader;
import obj.SyaryoObject;
import param.KomatsuUserParameter;

/**
 *
 * @author ZZ17390
 */
public class MainteEvaluate {

    private static Map<String, String> INTERVAL = KomatsuUserParameter.PC200_MAINTEPARTS_INTERVAL;
    private static SyaryoLoader LOADER = SyaryoLoader.getInstance();
    private static List<String> _header;
    private Map<String, Map<String, Double>> _eval;

    public MainteEvaluate() {
        _header = new ArrayList<>(INTERVAL.keySet());
        _eval = new HashMap<>();
    }

    //ヘッダ取得
    public List<String> header() {
        return _header;
    }

    //評価値取得
    public Map<String, Map<String, Double>> evaluate(Map<String, SyaryoObject> map) {
        map.values().stream().forEach(s -> {
            try (SyaryoAnalizer a = new SyaryoAnalizer(s, true)) {
                //集約
                Map<String, Integer[]> data = aggregate(a);

                //正規化
                _eval.put(s.name, normalize(data));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        return _eval;
    }

    public Map<String, Double> evaluate(SyaryoObject s) {
        Map<String, Double> eval = new HashMap();

        try (SyaryoAnalizer a = new SyaryoAnalizer(s, true)) {
            //集約
            Map<String, Integer[]> data = aggregate(a);

            //正規化
            eval = normalize(data);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        return eval;
    }

    //データ取得
    public Map<String, Map<String, Integer[]>> getdata(Map<String, SyaryoObject> map) {
        Map<String, Map<String, Integer[]>> eval = new TreeMap<>();

        map.values().stream().forEach(s -> {
            try (SyaryoAnalizer a = new SyaryoAnalizer(s, true)) {
                //集約
                eval.put(s.name, aggregate(a));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        return eval;
    }

    public Map<String, Integer[]> getdata(SyaryoObject s) {
        Map<String, Integer[]> eval = new HashMap<>();
        
        try (SyaryoAnalizer a = new SyaryoAnalizer(s, true)) {
            //集約
            eval = aggregate(a);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        return eval;
    }
    
    public Map<String, List<Double>> getClusData(){
        Map<String, List<Double>> data = _eval.entrySet().stream()
                    .collect(Collectors.toMap(
                            e -> e.getKey(), 
                            e -> _header.stream().map(h -> e.getValue().get(h)).collect(Collectors.toList())
                    ));
        
        return data;
    }

    //正規化
    private Map<String, Double> normalize(Map<String, Integer[]> data) {
        Map norm = _header.stream()
                .collect(Collectors.toMap(
                        h -> h, 
                        h -> Arrays.asList(data.get(h)).stream()
                                .map(ti -> ti > 0 ? 1 : 0)
                                .mapToDouble(ti -> ti.doubleValue())
                                .average().getAsDouble(),
                        (h1, h2) -> h1,
                        LinkedHashMap::new
                ));
        
        return norm;
    }

    //時系列のメンテナンスデータ取得
    private Map<String, Integer[]> aggregate(SyaryoAnalizer s) {
        //System.out.println(s.name);
        Map<String, Integer[]> data = new HashMap<>();

        //時系列情報の取得
        INTERVAL.entrySet().stream()
                .forEach(e -> {
                    String check = "";
                    try {
                        TimeSeriesObject t = new TimeSeriesObject(s, "受注", e.getKey());
                        List<Integer> smr = t.series.stream()
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
                        Integer[] series = new Integer[len + 1];
                        Arrays.fill(series, 0);
                        
                        check = t.sid + ":" + s.maxSMR[4] + ":" + t.series + smr;
                        
                        smr.stream()
                                .map(v -> v == 0 ? 1 : v) //0h交換での例外処理
                                .map(v -> (v % Integer.valueOf(e.getValue())) == 0 ? v - 1 : v) //インターバル時間で割り切れる場合の例外処理
                                .forEach(v -> {
                                    int i = v / Integer.valueOf(e.getValue());
                                    series[i] = v;
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
        LOADER.setFile("PC200_loadmap");
        SyaryoObject s = LOADER.getSyaryoMap().get("PC200-10-454701");
        
        MainteEvaluate mainte = new MainteEvaluate();
        
        Map<String, Integer[]> data = mainte.getdata(s);
        Map<String, Double> result = mainte.evaluate(s);
        
        data.entrySet().stream().forEach(d ->{
            System.out.println(d.getKey());
            System.out.println("  "+Arrays.asList(d.getValue()));
            System.out.println("  "+result.get(d.getKey()));
        });
    }
}
