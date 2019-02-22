/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.analize;

import data.cluster.KMeansPP;
import data.eval.EvaluateSyaryoObject;
import file.CSVFileReadWrite;
import file.MapToJSON;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
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
    private static Map<String, EvaluateSyaryoObject> EVAL = new HashMap();

    private static SyaryoLoader LOADER = SyaryoLoader.getInstance();
    private static String KISY = "PC200";

    public static void evalSyaryoMap(Map<String, SyaryoObject> map) {
        //評価
        evaluate(map);

        //クラスタリング
        mainte();
        use();

    }

    private static void evaluate(Map<String, SyaryoObject> map) {
        Long st = System.currentTimeMillis();
        System.out.println("車両評価プログラム実行");
        map.values().stream().forEach(syaryo -> {
            EvaluateSyaryoObject evalobj = new EvaluateSyaryoObject(syaryo);
            EVAL.put(syaryo.name, evalobj);
        });
        
        //Test Output
        new MapToJSON().toJSON("eval_map.json", EVAL);
        
        Long sp = System.currentTimeMillis();
        System.out.println("車両評価プログラム完了 - "+ EVAL.size()+ " <" + (sp - st) + "ms>");
    }
    
    private static Map<String, Integer> clustering(String data, String kind){
        Long st = System.currentTimeMillis();
        System.out.println("クラスタリング実行 - " + data+" - "+kind);
        
        KMeansPP km = new KMeansPP();
        km.setEvalSyaryo(3, EVAL.entrySet().stream().collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue().getData(data, kind))));
        Map<String, Integer> result = km.execute();
        
        Long sp = System.currentTimeMillis();
        System.out.println("クラスタリング完了 - " + (sp - st) + "ms");
        
        return result;
    }
    
    private static void mainte() {
        Map<String, Integer> result = clustering("mainte", "quality");
        fprint(KISY + "_mainte_eval.csv", "mainte", "quality", true, result);
    }
    
    private static void use() {
        List<Map<String, Integer>> results = new ArrayList<>();
        for(String load : EvaluateSyaryoObject.getDataList("use")){
            Map<String, Integer> result = clustering("use", load);
            results.add(result);
            
            fprint(KISY + "_"+load+"_use_eval.csv", "use", load, true, result);
        }
        
        //クラスタリング結果をEvalObjに戻す処理
        EVAL.entrySet().stream().forEach(e ->{
            Map<String, Double> map = new HashMap();
            for(String load : EvaluateSyaryoObject.getDataList("use")){
                int idx = EvaluateSyaryoObject.getDataList("use").indexOf(load);
                Double cid = results.get(idx).get(e.getKey()) != null?Double.valueOf(results.get(idx).get(e.getKey())):0d;
                map.put(load, cid);
            }
            e.getValue().addEval("use", "clusters", map);
        });
        
        Map<String, Integer> result = clustering("use", "clusters");
        
        fprint(KISY + "_use_eval.csv", "use", "clusters", true, result);
    }

    public static void fprint(String filename, String data, String kind, Boolean flg, Map<String, Integer> result) {
        try (PrintWriter pw = CSVFileReadWrite.writerSJIS(filename)) {
            if (flg) {
                pw.println("SID,Y,SMR," + EvaluateSyaryoObject.printh(data, kind) + ",AVG,CID");
                EVAL.entrySet().stream()
                    .map(e -> e.getKey() + "," + (e.getValue().day/365d) + "," + e.getValue().smr + "," +  //基本情報
                        e.getValue().print(data, kind) + "," +                                                      //データ
                        e.getValue().getData(data, kind).stream().mapToDouble(d -> d).average().getAsDouble() + "," + //平均
                        (result.get(e.getKey()) != null ? result.get(e.getKey()) : 0))  //クラスタ
                    .forEach(pw::println);
            } else {
                pw.println("SID,CID");
                result.entrySet().stream()
                    .map(r -> r.getKey() + "," + (r.getValue() != null ? r.getValue() : 0))
                    .forEach(pw::println);
            }
        }
    }

    public static void main(String[] args) {
        LOADER.setFile(KISY + "_form");
        evalSyaryoMap(LOADER.getSyaryoMap());
    }
}
