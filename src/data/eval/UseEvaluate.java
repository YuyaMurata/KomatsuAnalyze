/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.eval;

import analizer.SyaryoAnalizer;
import file.CSVFileReadWrite;
import java.io.PrintWriter;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import obj.SyaryoLoader;
import obj.SyaryoObject;

/**
 *
 * @author ZZ17390
 */
public class UseEvaluate {

    private static SyaryoLoader LOADER = SyaryoLoader.getInstance();
    private static Map<String, List<String>> _header = new HashMap<>();
    private static Integer R = 3;

    public static List<String> header(String load) {
        return _header.get(load);
    }

    public static void addHeader(String kind, List<String> load) {
        if (_header.get(kind) == null) {
            _header.put(kind, load);
            System.out.println(_header);
        }
    }

    public static Map<String, Map<String, Double>> nomalize(SyaryoAnalizer s, List<String> keys) {
        Map<String, Map<String, Double>> map = new LinkedHashMap<>();

        for (String key : keys) {
            if (key.contains("実エンジン回転")) {
                map.put(key, evalEngine(s.get().get(key), LOADER.index(key, "VALUE")));
            }
        }

        return map;
    }

    private static Map<String, Double> evalEngine(Map<String, List<String>> loadmap, int idx) {
        //トルク10の値を削除
        Map<String, Double> data = loadmap.entrySet().stream()
                .collect(Collectors.toMap(e -> e.getKey(), e -> Double.valueOf(e.getValue().get(idx))));

        //PEEK DESC RANK
        List<String> peekRank = data.entrySet().stream()
                .filter(e -> !e.getKey().split("_")[1].contains("10"))
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).limit(R)
                .map(e -> e.getKey()).collect(Collectors.toList());

        
        //PEEK ASC RANK  →　閾値を定めて表示
        List<String> peekARank = data.entrySet().stream()
                .filter(e -> !e.getKey().split("_")[1].contains("10"))
                .filter(e -> e.getValue() != 0d)
                .sorted(Map.Entry.comparingByValue()).limit(R)
                .map(e -> e.getKey()).collect(Collectors.toList());

        
        //正規化
        Map<String, Double> result1 = data.keySet().stream()
                        .collect(Collectors.toMap(k -> k, k -> peekRank.contains(k)?1d:0d, (e1, e2) -> e1, TreeMap::new));
        
        Map<String, Double> result2 = data.keySet().stream()
                        .collect(Collectors.toMap(k -> k, k -> peekARank.contains(k)?1d:0d, (e1, e2) -> e1, TreeMap::new));
        
        
        //テスト出力
        print(data, "test_loadmap_origin.csv");
        print(result1, "test_loadmap_r1.csv");
        print(result2, "test_loadmap_r2.csv");
        
        
        return null;
    }
    
    private static void print(Map<String, Double> data, String name){
        Map.Entry<List<String>, List<String>> head = getHeader(new ArrayList<>(data.keySet()));
        try(PrintWriter pw = CSVFileReadWrite.writerSJIS(name)){
            pw.println(","+String.join(",", head.getValue()));
            Double[][] mat = getValue(data);
            for(int i=0; i < head.getKey().size(); i++){
                pw.println(head.getKey().get(i)+","+Arrays.asList(mat[i]).stream().map(d -> d.toString()).collect(Collectors.joining(",")));
            }
        }
    }
    
    private static Map.Entry<List<String>, List<String>> getHeader(List<String> d){
        List<String> row = d.stream().map(k -> Integer.valueOf(k.split("_")[1])).distinct().sorted().map(k -> k.toString()).collect(Collectors.toList());
        List<String> col = d.stream().map(k -> Integer.valueOf(k.split("_")[0])).distinct().sorted().map(k -> k.toString()).collect(Collectors.toList());
        
        return new AbstractMap.SimpleEntry(row, col);
    }
    
    private static Double[][] getValue(Map<String, Double> d){
        Map.Entry<List<String>, List<String>> head = getHeader(new ArrayList<>(d.keySet()));
        List<String> row = head.getKey();
        List<String> col = head.getValue();
        
        Double[][] mat = new Double[row.size()][col.size()];
        d.entrySet().stream().forEach(v ->{
            int i = row.indexOf(v.getKey().split("_")[1]);
            int j = col.indexOf(v.getKey().split("_")[0]);
            
            mat[i][j] = v.getValue();
        });
        
        return mat;
    }
    
    public static void main(String[] args) {
        LOADER.setFile("PC200_form");
        SyaryoObject s = LOADER.getSyaryoMap().get("PC200-10-450635");
        
        try(SyaryoAnalizer a = new SyaryoAnalizer(s, Boolean.FALSE)){
            evalEngine(a.get("LOADMAP_実エンジン回転VSエンジントルク"), 0);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
