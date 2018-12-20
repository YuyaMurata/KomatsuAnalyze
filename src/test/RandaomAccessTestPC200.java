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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import obj.LoadSyaryoObject;
import obj.SyaryoObject;
import param.KomatsuDataParameter;

/**
 *
 * @author ZZ17390
 */
public class RandaomAccessTestPC200 {

    private static LoadSyaryoObject LOADER = KomatsuDataParameter.LOADER;
    private static String[] testDB = new String[]{"service.受注", "komtrax_CW_SERVICE_METER.KOMTRAX_SMR"};

    public static void main(String[] args) {
        Random rand = new Random();

        LOADER.setFile("PC200_km_form");
        Map<String, SyaryoObject> map = LOADER.getSyaryoMap();

        //ランダム抽出
        Arrays.asList(testDB).stream().map(k -> k.split("\\.")[1]).forEach(System.out::println);
        List<String> list = map.values().stream()
            .filter(s -> Arrays.asList(testDB).stream()
            .allMatch(k -> s.get(k.split("\\.")[1]) != null)
            ).map(s -> s.name).collect(Collectors.toList());

        List<String> randomSample = rand.ints(10, 0, list.size())
            .mapToObj(i -> list.get(i))
            .collect(Collectors.toList());

        System.out.println(randomSample);

        //車両オブジェクトアクセス
        System.out.println("車両オブジェクトアクセス");
        accessToSyaryoObject(randomSample);

        //DBアクセス
        System.out.println("\nDBアクセス");
        accessToDB(randomSample);
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
}
