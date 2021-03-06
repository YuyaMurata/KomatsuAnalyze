/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import db.HiveDB;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import obj.LoadSyaryoObject;
import obj.SyaryoLoader;
import obj.SyaryoObject;
import param.KomatsuDataParameter;

/**
 *
 * @author ZZ17390
 */
public class DataAccessTestPC200 {

    private static SyaryoLoader LOADER = SyaryoLoader.getInstance();
    private static String[] testDB = new String[]{"service.受注", "komtrax_CW_SERVICE_METER.KOMTRAX_SMR"};
    
    private static Random rand = new Random();

    public static void main(String[] args) {
        Arrays.asList(testDB).stream().map(k -> k.split("\\.")[1]).forEach(System.out::println);

        LOADER.setFile("PC200_km_form");
        Map<String, SyaryoObject> map = LOADER.getSyaryoMap();
        
        List<String> list = map.values().stream()
                .filter(s -> Arrays.asList(testDB).stream()
                .allMatch(k -> s.get(k.split("\\.")[1]) != null)
                ).map(s -> s.name).collect(Collectors.toList());
        
        //車両オブジェクト Nアクセス
        access(10000, list, map);

        //ランダム抽出
        /*List<String> randomSample = randomSampling(map);
        System.out.println(randomSample);

        //車両オブジェクトアクセス
        System.out.println("車両オブジェクトアクセス");
        accessToSyaryoObject(randomSample);
        
        //DBアクセス
        System.out.println("\nDBアクセス");
        accessToDB(randomSample);*/
    }

    private static List<String> randomSampling(int n, List<String> list) {
        if(n > list.size()){
            System.out.println("n="+list.size());
            //n = list.size();
            return list;
        }else{
            return rand.ints(n, 0, list.size())
                .mapToObj(i -> list.get(i))
                .collect(Collectors.toList());
        }
    }

    private static void accessToSyaryoObject(List<String> samples) {
        Long start = System.currentTimeMillis();

        LOADER.setFile("PC200_km_form");
        Map<String, SyaryoObject> map = LOADER.getSyaryoMap();

        for (String name : samples) {
            System.out.println(name + ":");
            //map.get(name).startHighPerformaceAccess();

            for (String db : testDB) {
                Long dbstart = System.currentTimeMillis();

                String key = db.split("\\.")[1];
                System.out.println(" -" + db + "=" + map.get(name).get(key).size());

                Long dbstop = System.currentTimeMillis();
                System.out.println(" accessTime = " + (dbstop - dbstart) + "ms");
            }
        }

        Long stop = System.currentTimeMillis();
        System.out.println("SyaryoObject_Transaction = " + (stop - start) + "ms");
    }

    private static void accessToDB(List<String> samples) {
        Long start = System.currentTimeMillis();

        for (String name : samples) {
            System.out.println(name + ":");
            String kisy = name.split("-")[0];
            String no = name.split("-")[2];

            for (String db : testDB) {
                Long dbstart = System.currentTimeMillis();

                String key = db.split("\\.")[0];
                try (Connection con = HiveDB.getConnection()) {
                    Statement stmt = con.createStatement();
                    String sql = "select * from " + key + " where kisy='" + kisy + "' and kiban=" + no;
                    //System.out.println("Running: " + sql);
                    ResultSet res = stmt.executeQuery(sql);
                    System.out.println(" -" + db + "=" + res);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }

                Long dbstop = System.currentTimeMillis();
                System.out.println(" accessTime = " + (dbstop - dbstart) + "ms");
            }

            Long stop = System.currentTimeMillis();
            System.out.println("DB_Transaction = " + (stop - start) + "ms");
        }
    }

    private static void access(int n, List syaryo, Map<String, SyaryoObject> map) {
        if (n > syaryo.size()) {
            n = syaryo.size();
        }

        //平均用
        for (String key : testDB) {
            Long total = 0L;
            String k = key.split("\\.")[1];
            for (int i = 0; i < 100; i++) {

                List<String> list = randomSampling(n, syaryo);

                Long start = System.currentTimeMillis();

                list.parallelStream().forEach(s -> {
                    Map m = map.get(s).get(k);
                });

                Long stop = System.currentTimeMillis();
                total += stop - start;
            }
            System.out.println(k + ":" + n + "=" + (total / 100) + "ms");
        }
    }
}
