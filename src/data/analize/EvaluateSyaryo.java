/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.analize;

import analizer.SyaryoAnalizer;
import data.cluster.KMeansPP;
import data.eval.MainteEvaluate;
import data.eval.UseEvaluate;
import file.CSVFileReadWrite;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import obj.SyaryoLoader;
import obj.SyaryoObject;

/**
 *
 * @author ZZ17390
 */
public class EvaluateSyaryo {
    private static SyaryoLoader LOADER = SyaryoLoader.getInstance();
    private static String KISY = "PC200";
    private static Integer C = 5; 
    private static List<String> enable;

    public static void evalSyaryoMap(Map<String, SyaryoObject> map) {
        enable = new ArrayList<>();
        Map<String, Integer> rm = mainte(map);
        Map<String, Integer> ru = use(map);
        
        Map results = rm.entrySet().stream()
                .collect(Collectors.toMap(
                        r -> r.getKey(), 
                        r -> Arrays.asList(new Integer[]{r.getValue(), ru.get(r.getKey())})
                ));
        
        fprint(KISY+"_クラスタリング結果.csv", results);
    }
    
    private static Map<String, Integer> clustering(String str, Map<String, List<Double>> data){
        Long st = System.currentTimeMillis();
        System.out.println("クラスタリング実行 - " + str);
        
        KMeansPP km = new KMeansPP();
        km.setEvalSyaryo(C, data);
        Map<String, Integer> result = km.execute();
        
        Long sp = System.currentTimeMillis();
        System.out.println("クラスタリング完了 - " + (sp - st) + "ms");
        
        return result;
    }
    
    private static Map<String, Integer> mainte(Map map) {
        //メンテナンス評価
        Long start = System.currentTimeMillis();
        MainteEvaluate eval = new MainteEvaluate();
        eval.evaluate("メンテナンス", map);
        Long evalstop = System.currentTimeMillis();
        System.out.println("メンテナンス評価完了　: "+(evalstop-start)+" [ms]");
        
        //クラスタリング
        Map<String, Integer> result = clustering("メンテナンス", eval.getClusterData("メンテナンス"));
        Long stop = System.currentTimeMillis();
        System.out.println("メンテナンスクラスタリング完了　: "+(stop-evalstop)+" [ms]");
        
        //スコアリング
        result = eval.scoring(result, eval.getClusterData("メンテナンス"));
        
        enable = new ArrayList<>(result.keySet());
        
        //除外車両
        //LOADER.getSyaryoMap().keySet().stream().filter(sid -> result.get(sid) == null).forEach(System.out::println);
        
        fprint(KISY + "_mainte_eval.csv", eval.header("メンテナンス"), eval.getClusterData("メンテナンス"), result);
        
        return result;
    }
    
    private static Map<String, Integer> use(Map map) {
        String key = "LOADMAP_実エンジン回転VSエンジントルク";
        Long start = System.currentTimeMillis();
        UseEvaluate eval = new UseEvaluate();
        eval.evaluate(key, map);
        Long evalstop = System.currentTimeMillis();
        System.out.println("使われ方評価完了　: "+(evalstop-start)+" [ms]");
        
        Map enabledata = eval.getClusterData(key).entrySet().stream()
                                .filter(e -> enable.contains(e.getKey()))
                                .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
        
        //クラスタリング
        Map<String, Integer> result = clustering("使われ方", enabledata);
        Long stop = System.currentTimeMillis();
        System.out.println("使われ方クラスタリング完了　: "+(stop-evalstop)+" [ms]");
        
        //スコアリング
        result = eval.scoring(result, eval.getClusterData(key));
        
        fprint(KISY + "_use_eval.csv",eval.header(key), eval.getClusterData(key), result);
        
        return result;
    }
    
    public static void fprint(String filename, Map<String, List<Integer>> results) {
        try(PrintWriter pw = CSVFileReadWrite.writerSJIS(filename)){
            //header
            pw.println("SID,MAINTE_CID,USE_CID");
            
            //data
            results.entrySet().stream().map(r -> r.getKey()+","+
                            r.getValue().stream().map(c -> c.toString()).collect(Collectors.joining(",")))
                    .forEach(pw::println);
        }
    }
    
    public static void fprint(String filename, List<String> header, Map<String, List<Double>> data, Map<String, Integer> result) {
        try(PrintWriter pw = CSVFileReadWrite.writerSJIS(filename)){
            //header
            pw.println("SID,"+String.join(",", header)+",CID");
            
            //data
            result.entrySet().stream().map(r -> r.getKey()+","+
                            data.get(r.getKey()).stream().map(d -> d.toString()).collect(Collectors.joining(","))+","+
                            r.getValue())
                    .forEach(pw::println);
        }
    }
    
    //分析用のデータフィルタリング
    public static Map rejectSyaryo(Map<String, SyaryoObject> map){
        Map enable = new TreeMap();
        map.values().stream().forEach(s ->{
            try(SyaryoAnalizer a = new SyaryoAnalizer(s, true)){
                //コマツレンタルの除外
                if(a.rent != 2)
                    enable.put(s.name, s);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        
        System.out.println("除外(KR車両):"+map.keySet().stream().filter(n -> enable.get(n)==null).count());
        
        return enable;
    }

    public static void main(String[] args) {
        LOADER.setFile(KISY + "_loadmap");
        
        //フィルタリング
        Map map = rejectSyaryo(LOADER.getSyaryoMap());
        
        //評価
        evalSyaryoMap(map);
        //evalSyaryoMap(LOADER.getSyaryoMap());
    }
}
