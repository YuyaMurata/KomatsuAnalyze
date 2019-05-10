/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.analize;

import data.cluster.KMeansPP;
import data.eval.MainteEvaluate;
import data.eval.UseEvaluate;
import file.CSVFileReadWrite;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
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
    private static List<String> enable;

    public static void evalSyaryoMap(Map<String, SyaryoObject> map) {
        enable = new ArrayList<>();
        Map<String, Integer> rm = mainte();
        Map<String, Integer> ru = use();
        
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
        km.setEvalSyaryo(3, data);
        Map<String, Integer> result = km.execute();
        
        Long sp = System.currentTimeMillis();
        System.out.println("クラスタリング完了 - " + (sp - st) + "ms");
        
        return result;
    }
    
    private static Map<String, Integer> mainte() {
        //メンテナンス評価
        Long start = System.currentTimeMillis();
        MainteEvaluate eval = new MainteEvaluate();
        eval.evaluate(LOADER.getSyaryoMap());
        Long evalstop = System.currentTimeMillis();
        System.out.println("メンテナンス評価完了　: "+(evalstop-start)+" [ms]");
        
        //クラスタリング
        Map<String, Integer> result = clustering("メンテナンス", eval.getClusData());
        Long stop = System.currentTimeMillis();
        System.out.println("メンテナンスクラスタリング完了　: "+(stop-evalstop)+" [ms]");
        
        enable = new ArrayList<>(result.keySet());
        
        fprint(KISY + "_mainte_eval.csv", eval.header(), eval.getClusData(), result);
        
        return result;
    }
    
    private static Map<String, Integer> use() {
        Long start = System.currentTimeMillis();
        UseEvaluate eval = new UseEvaluate();
        eval.evaluate(LOADER.getSyaryoMap());
        Long evalstop = System.currentTimeMillis();
        System.out.println("使われ方評価完了　: "+(evalstop-start)+" [ms]");
        
        Map enabledata = eval.getClusData().entrySet().stream()
                                .filter(e -> enable.contains(e.getKey()))
                                .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
        
        //クラスタリング
        Map<String, Integer> result = clustering("使われ方", enabledata);
        Long stop = System.currentTimeMillis();
        System.out.println("使われ方クラスタリング完了　: "+(stop-evalstop)+" [ms]");
        
        fprint(KISY + "_use_eval.csv",eval.longheader(), eval.getClusData(), result);
        
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

    public static void main(String[] args) {
        LOADER.setFile(KISY + "_loadmap");
        evalSyaryoMap(LOADER.getSyaryoMap());
    }
}
