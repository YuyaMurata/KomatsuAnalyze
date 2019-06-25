/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.eval;

import analizer.SyaryoAnalizer;
import data.time.TimeSeriesObject;
import file.CSVFileReadWrite;
import java.io.PrintWriter;
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
                        List<Integer> smr = t.series.keySet().stream()
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
                        //len == 0 SMRがインターバル時間に届いていない場合、無条件で1と評価
                        List<String> series = len != 0 ? IntStream.range(0, len).boxed().map(i -> "0").collect(Collectors.toList()) : Arrays.asList(new String[]{"1"});

                        check = e.getKey() + " - " + t.sid + ":" + s.maxSMR[4] + ":" + t.series + smr;

                        smr.stream()
                                .map(v -> v == 0 ? 1 : v) //0h交換での例外処理
                                .map(v -> (v % Integer.valueOf(e.getValue())) == 0 ? v - 1 : v) //インターバル時間で割り切れる場合の例外処理
                                .forEach(v -> {
                                    int i = v / Integer.valueOf(e.getValue());
                                    if (i < len) {
                                        //if(series.get(i).equals("0"))
                                        series.set(i, v.toString());
                                        //else
                                        //    series.set(i, series.get(i)+"_"+v.toString());
                                    }
                                });

                        //各車両のメンテナンス状況を記録
                        data.put(e.getKey(), series);

                        //System.out.println(t.sid+","+(s.age(s.getSMRToDate(max))/365)+","+max+","+t.target+","+Arrays.asList(series).stream().map(v -> v.toString()).collect(Collectors.joining(",")));
                    } catch (IndexOutOfBoundsException ie) {
                        System.err.println(check);
                        ie.printStackTrace();
                    }

                });

        return data;
    }
    
    @Override
    public Map<String, Integer> scoring(Map<String, Integer> cluster, Map<String, List<Double>> data) {
        int maxCluster = cluster.values().stream().distinct().mapToInt(c -> c).max().getAsInt();
        
        //Average
        Map<Integer, Double> avg = IntStream.range(0, maxCluster+1).boxed()
                                        .collect(Collectors.toMap(
                                                i -> i, 
                                                i -> cluster.entrySet().stream()
                                                            .filter(c -> c.getValue().equals(i))
                                                            .flatMap(c -> data.get(c.getKey()).stream())
                                                            .mapToDouble(d -> d).average().getAsDouble()
                                        ));
        
        //Sort
        List<Integer> sort = avg.entrySet().stream()
                                .sorted(Map.Entry.comparingByValue())
                                .map(a -> a.getKey())
                                .collect(Collectors.toList());
        
        //Scoring
        Map score = cluster.entrySet().stream()
                            .collect(Collectors.toMap(c -> c.getKey(), c -> sort.indexOf(c.getValue())+1));
        
        return score;
    }
    
    @Override
    public void createARFF(String file, String key) {
        Map<String, List<Double>> data = getClusterData(key);
        List<String> header = header(key);
        
        try(PrintWriter pw = CSVFileReadWrite.writerSJIS(file)){
            pw.println("@RELATION "+key+"\n");
            header.stream().map( h-> "@ATTRIBUTE "+h+" REAL").forEach(pw::println);
            pw.println("\n@DATA");
            data.values().stream()
                    .map(d -> d.stream().map(di -> di.toString()).collect(Collectors.joining(",")))
                    .forEach(pw::println);
        }
    }
    
    //Test
    public static void main(String[] args) {
        LOADER.setFile("PC200_loadmap");
        SyaryoAnalizer.rejectSettings(false, false, false);
        SyaryoAnalizer s = new SyaryoAnalizer(LOADER.getSyaryoMap().get("PC200-10-452525"), true);

        MainteEvaluate mainte = new MainteEvaluate();

        Map<String, List<String>> data = mainte.getdata(s);
        Map<String, Double> result = mainte.evaluate("メンテナンス", s);

        data.entrySet().stream().forEach(d -> {
            System.out.println(d.getKey());
            System.out.println("  " + d.getValue());
            System.out.println("  " + result.get(d.getKey()));
        });

        //クラスタ用データ
        System.out.println("\n" + mainte.header("メンテナンス"));
        System.out.println(mainte.getClusterData("メンテナンス"));
    }
}
