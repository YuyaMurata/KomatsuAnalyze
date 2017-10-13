/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analyze;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import json.JsonToSyaryoObj;
import obj.SyaryoObject;

/**
 *
 * @author murata
 */
public class Histgram {

    static Boolean flg = true;

    public Map create(List<Integer> kaikyu, List<Integer> data) {

        TreeMap<Long, Integer> map = new TreeMap();

        //Sturges'rule
        double k = Math.round(Math.log(kaikyu.size()) / Math.log(2));
        Integer min = kaikyu.stream().min(Comparator.naturalOrder()).get();
        if (min < 0) {
            min = 0;
        }
        Integer max = kaikyu.stream().max(Comparator.naturalOrder()).get();
        System.out.println(max);
        if (flg) {
            kaikyu.remove(max);
            max = kaikyu.stream().max(Comparator.naturalOrder()).get();
            flg = false;
        }
        k = k * 3;
        double width = Math.ceil((max - min) / k);
        for (int i = 0; i < k; i++) {
            map.put((long) (min + ((double) i * width)), 0);
        }

        System.out.println("k=" + k + " min=" + min + " max=" + max + " k-width=" + width);

        for (Integer d : data) {
            if (d < 0) {
                System.out.println(d);
                continue;
            }
            Long key = map.floorKey(d.longValue());
            map.put(key, map.get(key) + 1);
        }
        return map;
    }

    //Test
    public static void main(String[] args) {
        String filename = "syaryo_obj_WA500_form.json";
        Map<String, SyaryoObject> syaryoMap = new JsonToSyaryoObj().reader(filename);
        orderanalyze(filename, syaryoMap);
        //orderpriceanalyze(filename, syaryoMap);
    }

    public static void orderanalyze(String filename, Map<String, SyaryoObject> syaryoMap) {
        List<String> type = syaryoMap.values().stream()
                .map(s -> s.getType()).distinct().collect(Collectors.toList());
        
        List allList = new ArrayList();
        Map<String, List<Integer>> analyzData = new HashMap<>();
        for (String typ : type) {

            List list = syaryoMap.values().stream()
                    .filter(s -> s.getType().equals(typ))
                    .filter(s -> s.getOrder() != null)
                    .map(s -> s.getOrder().size())
                    .collect(Collectors.toList());

            analyzData.put(typ, list);
            allList.addAll(list);

            System.out.println(list);
        }

        for (String typ : type) {
            Map map = new Histgram().create(allList, analyzData.get(typ));
            PrintWriter pw;
            try {
                pw = new PrintWriter(new BufferedWriter(new FileWriter(new File(filename.split("_")[2] +"_t"+ typ + "_order_num.csv"))));
                for (Object key : map.keySet()) {
                    pw.println(key + "," + map.get(key));
                }
                pw.close();
            } catch (IOException ex) {
            }
        }
    }

    public static void orderpriceanalyze(String filename, Map<String, SyaryoObject> syaryoMap) {
        List allList = new ArrayList();
        Map<String, List> analyzData = new HashMap<>();
        
        List<String> type = syaryoMap.values().stream()
                .map(s -> s.getType()).distinct().collect(Collectors.toList());
        
        for (String typ : type) {

            List data = syaryoMap.values().stream()
                    .filter(s -> s.getType().equals(typ))
                    .filter(s -> s.getOrder() != null)
                    .map(s -> s.getOrder("19"))  //列の指定
                    .collect(Collectors.toList());
            List list = new ArrayList();
            for (Object l : data) {
                list.addAll((List) l);
            }
            list = (List) list.stream()
                    .map(s -> Integer.valueOf(s.toString().replace("+", "").replace(".", "")))
                    .collect(Collectors.toList());

            allList.addAll(list);
            analyzData.put(typ, list);

            System.out.println(list);

        }

        for (String typ : type) {
            Map map = new Histgram().create(allList, analyzData.get(typ));

            PrintWriter pw;
            try {
                pw = new PrintWriter(new BufferedWriter(new FileWriter(new File(filename.split("_")[2]+"_t"+ typ +  "_order_price_histgram_社内.csv"))));
                for (Object key : map.keySet()) {
                    pw.println(key + "," + map.get(key));
                }
                pw.close();
            } catch (IOException ex) {
            }
        }
    }
}
