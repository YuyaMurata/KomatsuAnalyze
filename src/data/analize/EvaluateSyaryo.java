/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.analize;

import data.cluster.WEKAClustering;
import data.eval.AgeSMREvaluate;
import data.eval.MainteEvaluate;
import data.eval.UseEvaluate;
import file.CSVFileReadWrite;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import obj.SyaryoLoader;
import obj.SyaryoObject;
import param.KomatsuUserParameter;

/**
 *
 * @author ZZ17390
 */
public class EvaluateSyaryo {
    private static SyaryoLoader LOADER = SyaryoLoader.getInstance();
    private static String KISY = "PC200";
    private static Integer C = 3;

    public static void evalSyaryoMap(Map<String, SyaryoObject> map) {
        Map<String, Integer> rm = mainte(map);
        Map<String, Integer> ru = use(map);
        
        Map cids = rm.entrySet().stream()
                .collect(Collectors.toMap(
                        r -> r.getKey(), 
                        r -> Arrays.asList(new Integer[]{r.getValue(), ru.get(r.getKey())})
                ));
        
        Map<String, List<String>> results = agesmr(cids);
        fprint(KISY+"_評価結果.csv", AgeSMREvaluate._header.get("Age/SMR"), results);
    }
    
    private static Map<String, Integer> clustering(String str, String file, List<String> sids){
        Long st = System.currentTimeMillis();
        System.out.println("クラスタリング実行 - " + str);
        
        /*KMeansPP km = new KMeansPP();
        km.setEvalSyaryo(C, data);
        Map<String, Integer> result = km.execute();
        */
        
        WEKAClustering weka = new WEKAClustering();
        weka.set(file, C, sids);
        Map<String, Integer> result = weka.clustering();
        
        Long sp = System.currentTimeMillis();
        System.out.println("クラスタリング完了 - " + (sp - st) + "ms");
        
        return result;
    }
    
    private static Map<String, Integer> mainte(Map map) {
        //メンテナンス評価
        String key = "メンテナンス";
        Long start = System.currentTimeMillis();
        MainteEvaluate eval = new MainteEvaluate();
        eval.evaluate(key, map);
        Long evalstop = System.currentTimeMillis();
        System.out.println("メンテナンス 評価完了　: "+(evalstop-start)+" [ms]");
        
        //クラスタリング
        String clusterfile = KomatsuUserParameter.WEKA_PATH+KISY+"_メンテナンス.arff";
        eval.createARFF(clusterfile, key);
        Map<String, Integer> result = clustering(key, clusterfile, eval.getSIDList());
        
        //スコアリング
        result = eval.scoring(result, key, eval.getClusterData(key));
        
        fprint(KISY + "_mainte_eval.csv", eval.header(key), eval.getClusterData(key), result);
        
        return result;
    }
    
    private static Map<String, List<String>> agesmr(Map map) {
        //Age/SMR 評価
        Long start = System.currentTimeMillis();
        AgeSMREvaluate eval = new AgeSMREvaluate();
        eval.evaluate(map, "受注", "");
        Long evalstop = System.currentTimeMillis();
        System.out.println("Age/SMR 評価完了　: "+(evalstop-start)+" [ms]");
        
        return eval._eval;
    }
    
    private static Map<String, Integer> use(Map map) {
        //String key = "LOADMAP_実エンジン回転VSエンジントルク";
        String key = "LOADMAP_エンジン水温VS作動油温";
        Long start = System.currentTimeMillis();
        UseEvaluate eval = new UseEvaluate();
        eval.evaluate(key, map);
        Long evalstop = System.currentTimeMillis();
        System.out.println("使われ方 評価完了　: "+(evalstop-start)+" [ms]");
        
        /*Map enabledata = eval.getClusterData(key).entrySet().stream()
                                .filter(e -> enable.contains(e.getKey()))
                                .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
        */
        
        //クラスタリング
        String clusterfile = KomatsuUserParameter.WEKA_PATH+KISY+"_使われ方.arff";
        eval.createARFF(clusterfile, key);
        Map<String, Integer> result = clustering("使われ方", clusterfile, eval.getSIDList());
        
        //スコアリング
        result = eval.scoring(result, key, eval.getClusterData(key));
        
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
    
    public static void fprint(String filename, List<String> header, Map<String, List<String>> results) {
        try(PrintWriter pw = CSVFileReadWrite.writerSJIS(filename)){
            //header
            pw.println("SID,"+String.join(",", header));
            
            //data
            results.entrySet().stream()
                    .map(r -> r.getKey()+","+String.join(",", r.getValue()))
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
        
        //フィルタリング
        KomatsuUserParameter.PC200_REJECT.reject(LOADER.getSyaryoMap());
        
        //評価
        //evalSyaryoMap(map);
        evalSyaryoMap(LOADER.getSyaryoMap());
    }
}
