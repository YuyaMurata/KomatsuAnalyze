/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.analize;

import data.cluster.KMeansPP;
import data.eval.EvaluateSyaryoObject;
import file.CSVFileReadWrite;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import obj.SyaryoLoader;
import obj.SyaryoObject;

/**
 *
 * @author ZZ17390
 */
public class EvaluateSyaryo {
    private static Map<String, EvaluateSyaryoObject> EVAL = new HashMap();
    private static Map<String, List<Double>> DATA = new HashMap();
    private static Map<String, Integer> RESULTS = new HashMap<>();
    
    private static SyaryoLoader LOADER = SyaryoLoader.getInstance();
    private static String KISY = "PC200";
    
    public static void evalSyaryoMap(Map<String, SyaryoObject> map){
        //評価
        Long st = System.currentTimeMillis();
        System.out.println("車両評価プログラム実行");
        for (SyaryoObject syaryo : map.values()){
            EvaluateSyaryoObject evalobj = new EvaluateSyaryoObject(syaryo);
            if(evalobj.evalUsage == null)
                continue;
            
            EVAL.put(syaryo.name, evalobj);
            DATA.put(syaryo.name, new ArrayList<>(evalobj.evalUsage.values())); //Useのみ
        }
        Long sp = System.currentTimeMillis();
        System.out.println("車両評価プログラム完了 - "+(sp-st)+"ms");

        //クラスタリング
        System.out.println("クラスタリング実行");
        KMeansPP km = new KMeansPP();
        km.setEvalSyaryo(3, DATA);
        RESULTS = km.execute();
        st = System.currentTimeMillis();
        System.out.println("クラスタリング完了 - "+(st-sp)+"ms");
    }
    
    public static void fprint(String filename){
        try(PrintWriter pw = CSVFileReadWrite.writerSJIS(filename)){
            pw.println("SID,CID");
            RESULTS.entrySet().stream().map(r -> r.getKey()+","+r.getValue()).forEach(pw::println);
        }
    }
    
    public static void main(String[] args) {
        LOADER.setFile(KISY+"_form");
        evalSyaryoMap(LOADER.getSyaryoMap());
        fprint(KISY+"_culster_use.csv");
    }
}
