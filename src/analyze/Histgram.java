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
import java.util.NoSuchElementException;
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

    //Test
    public static void main(String[] args) {
        String filename = "syaryo_obj_PC200_form.json";
        Map<String, SyaryoObject> syaryoMap = new JsonToSyaryoObj().reader(filename);
        //orderanalyze(filename, syaryoMap);
        //orderpriceanalyze(filename, syaryoMap);
        smrAnalyze(filename, syaryoMap);
    }

    //階級幅の整形
    public List<Integer> pretty(Double min, Double max, Double k) {
        Double cell = (max - min) / k;
        Double base = Math.pow(10, Math.floor(Math.log10(cell)));
        System.out.println(base);
        Double unit;
        if (cell <= 1.4 * base) {
            unit = base;
        } else if (cell < 2.8 * base) {
            unit = 2 * base;
        } else if (cell < 7 * base) {
            unit = 5 * base;
        } else {
            unit = 10 * base;
        }

        Double min_unit = Math.floor(min / unit);
        Double max_unit = Math.ceil(max / unit);

        min = min_unit * unit;
        max = max_unit * unit;
        Double ndiv = max_unit - min_unit;

        List<Integer> seq = new ArrayList();
        for (int i = 0; i < ndiv + 1; i++) {
            seq.add((int) (i * unit));
        }

        System.out.println("min=" + min + " max=" + max + " n=" + (ndiv + 1));
        System.out.println(seq);
        return seq;
    }

    public Map create(int m, List<Integer> kaikyu, List<Integer> data) {

        TreeMap<Integer, Integer> map = new TreeMap();

        //Sturges'rule
        double k = Math.round(Math.log(kaikyu.size()) / Math.log(2));
        double min = kaikyu.stream().min(Comparator.naturalOrder()).get();
        double max = kaikyu.stream().max(Comparator.naturalOrder()).get();
        System.out.println(max);
        if (flg) { //最大値を除去
            kaikyu.remove(max);
            max = kaikyu.stream().max(Comparator.naturalOrder()).get();
            flg = false;
        }
        List<Integer> seq = pretty(min, max, k * m);
        for (Integer value : seq) {
            map.put(value, 0);
        }

        System.out.println("k=" + (k * m) + " min=" + min + " max=" + max);

        for (Integer d : data) {
            if (d < 0) {
                System.out.println(d);
                continue;
            }
            Integer key = map.floorKey(d);
            map.put(key, map.get(key) + 1);
        }
        return map;
    }

    //受注数
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
            //Histgram
            Map map = new Histgram().create(2, allList, analyzData.get(typ));
            PrintWriter pw;
            try {
                pw = new PrintWriter(new BufferedWriter(new FileWriter(new File(filename.split("_")[2] + "_order_num_t" + typ + ".csv"))));
                for (Object key : map.keySet()) {
                    pw.println(key + "," + map.get(key));
                }
                pw.close();
            } catch (IOException ex) {
            }
        }
    }

    //受注請求額
    public static void orderpriceanalyze(String filename, Map<String, SyaryoObject> syaryoMap) {
        List allList = new ArrayList();
        Map<String, List> analyzData = new HashMap<>();

        List<String> type = syaryoMap.values().stream()
                .map(s -> s.getType()).distinct().collect(Collectors.toList());

        for (String typ : type) {

            List data = syaryoMap.values().stream()
                    .filter(s -> s.getType().equals(typ))
                    .filter(s -> s.getOrder() != null)
                    .map(s -> s.getOrder("17")) //列の指定
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
            Map map = new Histgram().create(3, allList, analyzData.get(typ));

            PrintWriter pw;
            try {
                pw = new PrintWriter(new BufferedWriter(new FileWriter(new File(filename.split("_")[2] + "_order_price_histgram_一般_t" + typ + ".csv"))));
                for (Object key : map.keySet()) {
                    pw.println(key + "," + map.get(key));
                }
                pw.close();
            } catch (IOException ex) {
            }
        }
    }

    //SMR
    public static void smrAnalyze(String filename, Map<String, SyaryoObject> syaryoMap) {
        List<String> type = syaryoMap.values().stream()
                .map(s -> s.getType()).distinct().collect(Collectors.toList());

        List allList = new ArrayList();
        Map<String, List<Integer>> analyzData = new HashMap<>();
        for (String typ : type) {
            List list = syaryoMap.values().stream()
                                    .filter(s -> s.getType().equals(typ))
                                    .filter(s -> s.getSMR() != null)
                                    .map(smr -> smr.getSMR().values().stream()
                                            .map(s -> Integer.valueOf(s.get(0).toString().split("\\.")[0]))
                                            .max(Comparator.naturalOrder()).get())
                                    .collect(Collectors.toList());
            
            analyzData.put(typ, list);
            allList.addAll(list);

            System.out.println(list);
            System.out.println("車両数:" + list.size());
        }

        for (String typ : type) {
            if(analyzData.get(typ) == null) continue;
            //Histgram
            Map map = new Histgram().create(5, allList, analyzData.get(typ));
            PrintWriter pw;
            try {
                pw = new PrintWriter(new BufferedWriter(new FileWriter(new File(filename.split("_")[2] + "_smr_t" + typ + ".csv"))));
                for (Object key : map.keySet()) {
                    pw.println(key + "," + map.get(key));
                }
                pw.close();
            } catch (IOException ex) {
            }
        }
    }
    
    //SMR_Komtrax
    public static void komsmrAnalyze(String filename, Map<String, SyaryoObject> syaryoMap) {
        List<String> type = syaryoMap.values().stream()
                .map(s -> s.getType()).distinct().collect(Collectors.toList());

        List allList = new ArrayList();
        Map<String, List<Integer>> analyzData = new HashMap<>();
        for (String typ : type) {
            List list = new ArrayList();
            for(SyaryoObject syaryo : syaryoMap.values()){
                if(!syaryo.getType().equals(typ)) continue;
                if(!syaryo.getKomtrax()) continue;
                if(syaryo.getSMR() == null) continue;
                
                Map<String, List> smr = syaryo.getSMR();
                List valueList = new ArrayList();
                for(List l : smr.values()){
                    if(l.get(1).toString().contains("komtrax"))
                        valueList.add(l.get(0).toString().split("\\.")[0]);
                }
                if(!valueList.isEmpty())
                    list.add(valueList.stream().map(s -> Integer.valueOf(s.toString())).max(Comparator.naturalOrder()).get());
            }
            
            if(list.isEmpty())
                continue;
            
            analyzData.put(typ, list);
            allList.addAll(list);

            System.out.println(list);
            System.out.println("車両数:" + list.size());
        }

        for (String typ : type) {
            if(analyzData.get(typ) == null) continue;
            //Histgram
            Map map = new Histgram().create(5, allList, analyzData.get(typ));
            PrintWriter pw;
            try {
                pw = new PrintWriter(new BufferedWriter(new FileWriter(new File(filename.split("_")[2] + "_komsmr_t" + typ + ".csv"))));
                for (Object key : map.keySet()) {
                    pw.println(key + "," + map.get(key));
                }
                pw.close();
            } catch (IOException ex) {
            }
        }
    }
}
