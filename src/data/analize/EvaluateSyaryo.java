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
import java.util.HashMap;
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
    private static Map<String, Map<String, Integer>> RESULTS = new HashMap<>();

    private static SyaryoLoader LOADER = SyaryoLoader.getInstance();
    private static String KISY = "PC200";

    public static void evalSyaryoMap(Map<String, SyaryoObject> map) {
        //Test
        String data = "mainte";

        //評価
        evaluate(map, data);

        //クラスタリング
        clustering(data);

        fprint(KISY + "_evaluation.csv", data, true);
    }

    private static void evaluate(Map<String, SyaryoObject> map, String data) {
        Long st = System.currentTimeMillis();
        System.out.println("車両評価プログラム実行");
        map.values().stream().forEach(syaryo -> {
            EvaluateSyaryoObject evalobj = new EvaluateSyaryoObject(syaryo);

            if (!evalobj.isNULL(data)) {
                EVAL.put(syaryo.name, evalobj);
            }
        });
        Long sp = System.currentTimeMillis();
        System.out.println("車両評価プログラム完了 - " + (sp - st) + "ms");
    }

    private static void clustering(String data) {
        Long st = System.currentTimeMillis();
        System.out.println("クラスタリング実行 - " + data);
        KMeansPP km = new KMeansPP();
        km.setEvalSyaryo(3, EVAL.entrySet().stream().collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue().getData(data))));
        RESULTS.put(data, km.execute());
        Long sp = System.currentTimeMillis();
        System.out.println("クラスタリング完了 - " + (sp - st) + "ms");
    }

    public static void fprint(String filename, String data, Boolean flg) {
        try (PrintWriter pw = CSVFileReadWrite.writerSJIS(filename)) {
            if (flg) {
                pw.println("SID,Y,SMR," + EvaluateSyaryoObject.printh(data) + ",AVG,CID");
                EVAL.entrySet().stream()
                    .map(e -> e.getKey() + "," + (e.getValue().day/365d) + "," + e.getValue().smr + "," +  //基本情報
                        e.getValue().print(data) + "," +                                                      //データ
                        e.getValue().getData(data).stream().mapToDouble(d -> d).average().getAsDouble() + "," + //平均
                        (RESULTS.get(data).get(e.getKey()) != null ? RESULTS.get(data).get(e.getKey()) : 0))  //クラスタ
                    .forEach(pw::println);
            } else {
                pw.println("SID,CID");
                RESULTS.get(data).entrySet().stream()
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
