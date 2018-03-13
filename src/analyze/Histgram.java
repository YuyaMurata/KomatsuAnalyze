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
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import json.JsonToSyaryoObj;
import json.SyaryoToZip;
import obj.SyaryoElements;
import obj.SyaryoObject;
import obj.SyaryoObject2;

/**
 *
 * @author murata
 */
public class Histgram {

    static Boolean flg = true;

    //Test
    public static void main(String[] args) {
        String filename = "json\\syaryo_obj_WA470_form";
        Map<String, SyaryoObject2> syaryoMap = new SyaryoToZip().readObject(filename);
        //orderanalyze(filename, syaryoMap);
        orderpriceanalyze(filename, syaryoMap);
        //smrAnalyze(filename, syaryoMap);
        //komerrAnalyze(filename, syaryoMap);
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
    public static void orderanalyze(String filename, Map<String, SyaryoObject2> syaryoMap) {
        List<String> type = syaryoMap.values().stream()
                .map(s -> s.getType()).distinct().collect(Collectors.toList());

        List allList = new ArrayList();
        Map<String, List<Integer>> analyzData = new HashMap<>();
        for (String typ : type) {
			
			List list = new ArrayList();
			for(SyaryoObject2 syaryo : syaryoMap.values()){
				if(!syaryo.getType().equals(typ))
					continue;
				
				syaryo.decompress();
				if(syaryo.getOrder() == null)
					continue;
				
				list.add(syaryo.getOrder().size());
				syaryo.compress(true);
			}

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
    public static void orderpriceanalyze(String filename, Map<String, SyaryoObject2> syaryoMap) {
        List allList = new ArrayList();
        Map<String, List> analyzData = new HashMap<>();

        List<String> type = syaryoMap.values().stream()
                .map(s -> s.getType()).distinct().collect(Collectors.toList());

        for (String typ : type) {

			List list = new ArrayList();
			for(SyaryoObject2 syaryo : syaryoMap.values()){
				if(!syaryo.getType().equals(typ))
					continue;
				
				syaryo.decompress();
				if(syaryo.getOrder() == null)
					continue;
				
				for(List data : syaryo.getOrder().values()){
					Integer price = Double.valueOf(data.get(SyaryoElements.Order.Invoice.getNo()).toString()).intValue();
					list.add(price);
				}
			}

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
            if (analyzData.get(typ) == null) {
                continue;
            }
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
            for (SyaryoObject syaryo : syaryoMap.values()) {
                if (!syaryo.getType().equals(typ)) {
                    continue;
                }
                if (!syaryo.getKomtrax()) {
                    continue;
                }
                if (syaryo.getSMR() == null) {
                    continue;
                }

                Map<String, List> smr = syaryo.getSMR();
                List valueList = new ArrayList();
                for (List l : smr.values()) {
                    if (l.get(1).toString().contains("komtrax")) {
                        valueList.add(l.get(0).toString().split("\\.")[0]);
                    }
                }
                if (!valueList.isEmpty()) {
                    list.add(valueList.stream().map(s -> Integer.valueOf(s.toString())).max(Comparator.naturalOrder()).get());
                }
            }

            if (list.isEmpty()) {
                continue;
            }

            analyzData.put(typ, list);
            allList.addAll(list);

            System.out.println(list);
            System.out.println("車両数:" + list.size());
        }

        for (String typ : type) {
            if (analyzData.get(typ) == null) {
                continue;
            }
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

    //Error_Komtrax
    public static void komerrAnalyze(String filename, Map<String, SyaryoObject> syaryoMap) {
        Map<String, List<Integer>> analyzData = new HashMap<>();
        List<Integer> allValueList = new ArrayList<>();
        for (SyaryoObject syaryo : syaryoMap.values()) {
            if (!syaryo.getKomtrax()) {
                continue;
            }
            if (syaryo.getError() == null) {
                continue;
            }

            Map<String, List> err = syaryo.getError();
            Map<String, Integer> errCount = new HashMap<>();
            for (List l : err.values()) {
                errCount.put((String) l.get(0), Integer.valueOf((String) l.get(1)));
            }

            for (String code : errCount.keySet()) {
                if (analyzData.get(code) == null) {
                    analyzData.put(code, new ArrayList<>());
                }
                analyzData.get(code).add(errCount.get(code));
                allValueList.add(errCount.get(code));
            }
        }

        System.out.println("車両数:" + allValueList.size());

        List<String> headers = new ArrayList();
        List<Map> outputList = new ArrayList();
        for (String code : analyzData.keySet()) {
            //Histgram
            Map map = new Histgram().create(5, allValueList, analyzData.get(code));
            headers.add(code);
            outputList.add(map);
        }

        //Output
        PrintWriter pw;
        try {
            pw = new PrintWriter(new BufferedWriter(new FileWriter(new File(filename.split("_")[2] + "_komerr.csv"))));
            Set keys = outputList.get(0).keySet();
            pw.println(","+headers.stream().collect(Collectors.joining(",")));
            for(Object key : keys){
                List value = outputList.stream().map(m -> m.get(key).toString()).collect(Collectors.toList());
                pw.println(key+","+value.stream().collect(Collectors.joining(",")));
            }
            
            pw.close();
        } catch (IOException ex) {
        }
    }
}
