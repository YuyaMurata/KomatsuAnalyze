/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.analize;

import data.cluster.KMeansPP;
import data.eval.EvaluateSyaryoObject;
import data.eval.MainteEvaluate;
import data.eval.UseEvaluate;
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
    private static SyaryoLoader LOADER = SyaryoLoader.getInstance();
    private static String KISY = "PC200";

    public static void evalSyaryoMap(Map<String, SyaryoObject> map) {
        mainte();
        use();
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
    
    private static void mainte() {
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
        
        fprint(KISY + "_mainte_eval.csv", result);
    }
    
    private static void use() {
        Long start = System.currentTimeMillis();
        UseEvaluate eval = new UseEvaluate();
        eval.evaluate(LOADER.getSyaryoMap());
        Long evalstop = System.currentTimeMillis();
        System.out.println("使われ方評価完了　: "+(evalstop-start)+" [ms]");
        
        //クラスタリング
        Map<String, Integer> result = clustering("使われ方", eval.getClusData());
        Long stop = System.currentTimeMillis();
        System.out.println("使われ方クラスタリング完了　: "+(stop-evalstop)+" [ms]");
        
        fprint(KISY + "_use_eval.csv", result);
    }

    public static void fprint(String filename, Map<String, Integer> result) {
        try(PrintWriter pw = CSVFileReadWrite.writerSJIS(filename)){
            //header
            pw.println("SID,CID");
            
            //data
            result.entrySet().stream().map(r -> r.getKey()+","+r.getValue()).forEach(pw::println);
        }
    }

    public static void main(String[] args) {
        LOADER.setFile(KISY + "_form");
        evalSyaryoMap(LOADER.getSyaryoMap());
    }
}
