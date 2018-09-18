/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.analize;

import analizer.SyaryoAnalizer;
import file.CSVFileReadWrite;
import java.io.PrintWriter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import json.MapIndexToJSON;
import json.SyaryoToZip3;
import obj.SyaryoObject4;
import program.r.R;

/**
 *
 * @author ZZ17390
 */
public class SIDSMRTrasition {

    //レイアウト
    private static final String KISY = "PC200";
    //private static final String exportFile = "ExportData_SMR_"+KISY+"_ALL.json";
    private static final String exportFile = "FORM_ExportData_SMR_" + KISY + "_ALL.json";

    public static void main(String[] args) {
        Map<String, SyaryoObject4> syaryoMap = new SyaryoToZip3().readJSON(exportFile);

        SyaryoObject4 dataHeader = syaryoMap.get("_headers");
        syaryoMap.remove("_headers");

        //formalize(syaryoMap, dataHeader);
        transition(syaryoMap, dataHeader);
    }

    private static void formalize(Map<String, SyaryoObject4> map, SyaryoObject4 header) {
        Map<String, Map> outMap = new TreeMap();

        map.values().stream().forEach(s -> {
            s.startHighPerformaceAccess();

            System.out.println(s.name);

            Map<String, Map> expMap = new TreeMap();
            expMap.put("SMR", formSMR(s.get("SMR"), header.get("SMR").get("SMR").indexOf("SVC_MTR")));
            expMap.put("KOMTRAX_SMR", formSMR(s.get("KOMTRAX_SMR"), header.get("KOMTRAX_SMR").get("KOMTRAX_SMR").indexOf("SMR_VALUE")));
            expMap.put("KOMTRAX_FUEL_CONSUME", formfuelConsume(s.get("KOMTRAX_FUEL_CONSUME"), header.get("KOMTRAX_FUEL_CONSUME").get("KOMTRAX_FUEL_CONSUME").indexOf("CONSUME_COUNT")));
            for (String key : s.getMap().keySet()) {
                if (expMap.get(key) != null) {
                    continue;
                }
                expMap.put(key, s.get(key));
            }

            outMap.put(s.name, expMap);

            s.stopHighPerformaceAccess();
        });

        Map<String, Map> expMap = new TreeMap();
        for (String key : header.getMap().keySet()) {
            expMap.put(key, header.get(key));
        }

        outMap.put("_headers", expMap);

        new MapIndexToJSON().write("FORM_" + exportFile, outMap);

        R.close();
    }

    //サービスのSMRを整形
    private static Map formSMR(Map<String, List> smr, int smridx) {
        if (smr == null) {
            //System.out.println("Not found Work!");
            return null;
        }

        //日付重複除去
        List<String> dateList = smr.keySet().stream()
            .filter(s -> !s.contains("None")) //日付が存在しない
            .map(s -> s.split("#")[0])
            .distinct()
            .collect(Collectors.toList());

        Map<String, List<String>> map = new TreeMap();
        Boolean zeroflg = false;
        for (String date : dateList) {
            String stdate = date;
            //重複日付を取り出す
            List<String> dateGroup = smr.keySet().stream()
                .filter(s -> s.contains(stdate))
                .filter(s -> !smr.get(s).get(smridx).equals("None")) //SMRが存在しない
                .collect(Collectors.toList());

            //欠損データのみのため
            if (dateGroup.isEmpty()) {
                continue;
            }

            if (date.length() > 8) {
                date = date.substring(0, 8);
            }

            for (String dg : dateGroup) {
                List list = smr.get(dg);

                if (map.get(date) == null) {
                    map.put(date, list);
                } else {
                    if (Float.valueOf(map.get(date).get(smridx)) < Float.valueOf(list.get(smridx).toString())) {
                        map.put(date, list);
                    }
                }
            }

            //整形処理
            map.get(date).set(smridx, map.get(date).get(smridx).split("\\.")[0]);
            if (map.get(date).get(smridx).equals("0")) {
                if (zeroflg) {
                    map.remove(date);
                } else {
                    zeroflg = true;
                }
            }
        }

        if (map.isEmpty()) {
            return null;
        }

        //異常データの排除
        map = rejectSMRData(map, smridx);

        return map;
    }

    //SMRの異常値を除去　出来が悪いのでつくり直し
    private static Map rejectSMRData(Map<String, List<String>> smr, int idx) {

        //MA
        List smrList = smr.values().stream()
            .map(s -> s.get(idx)).collect(Collectors.toList());
        List dates = smr.keySet().stream().collect(Collectors.toList());
        //List ma = MovingAverage.avg(smr, 5);

        //Regression
        Map<String, String> reg = R.getInstance().residuals(dates, smrList);
        List res = new ArrayList();
        for (String d : reg.keySet()) {
            String c = String.valueOf(Double.valueOf(Double.valueOf(reg.get(d)) - Double.valueOf(smrList.get(dates.indexOf(d)).toString())).intValue());
            res.add(c);
        }
        Map<String, String> sgtest = R.getInstance().detectOuters(dates, res);
        for (String date : sgtest.keySet()) {
            if (!sgtest.get(date).equals("NaN")) {
                sgtest.put(date, smr.get(date).get(idx));
            }
        }

        //異常データの排除
        Map<String, Integer> sortMap = sgtest.entrySet().stream()
            .filter(e -> !e.getValue().equals("NaN"))
            .sorted(Map.Entry.comparingByKey())
            .collect(Collectors.toMap(e -> e.getKey(), e -> Integer.valueOf(e.getValue()), (e, e2) -> e, LinkedHashMap::new));
        //List list = R.getInstance().detectOuters(sortMap.keySet().stream().collect(Collectors.toList()));
        //System.out.println(sortMap);
        List<String> sortList = sortMap.entrySet().stream()
            .sorted(Map.Entry.comparingByValue())
            .map(e -> e.getKey())
            .collect(Collectors.toList());
        //System.out.println(sortList);
        Deque<String> q = new ArrayDeque<String>();
        for (String date : sortList) {
            if (!q.isEmpty()) {
                while (Integer.valueOf(q.getLast()) > Integer.valueOf(date)) {
                    q.removeLast();
                    if (q.isEmpty()) {
                        break;
                    }
                }
            }
            q.addLast(date);
        }

        //System.out.println(q);
        Map<String, String> resultMap = new TreeMap<>();
        for (String date : sgtest.keySet()) {
            if (q.contains(date)) {
                resultMap.put(date, sortMap.get(date).toString());
            } else {
                resultMap.put(date, "NaN");
            }
        }

        for (String date : resultMap.keySet()) {
            if (resultMap.get(date).equals("NaN")) {
                smr.remove(date);
            }
        }

        return smr;
    }

    private static Map formfuelConsume(Map<String, List> fuel, int idx) {
        Double total = 0d;
        for (String date : fuel.keySet()) {
            Double data = Double.valueOf(fuel.get(date).get(idx).toString());
            total += data;
            fuel.get(date).set(idx, total.toString());
        }

        return fuel;
    }

    private static void transition(Map<String, SyaryoObject4> map, SyaryoObject4 header) {

        List<String> list = new ArrayList(map.keySet());
        Map<Integer, String[]> smrData = new TreeMap();
        Map<Integer, String[]> fuelData = new TreeMap();

        int smrIdx = header.get("SMR").get("SMR").indexOf("SVC_MTR");
        int kmsmrIdx = header.get("KOMTRAX_SMR").get("KOMTRAX_SMR").indexOf("SMR_VALUE");
        int fuelIdx = header.get("KOMTRAX_FUEL_CONSUME").get("KOMTRAX_FUEL_CONSUME").indexOf("CONSUME_COUNT");

        map.values().stream().forEach(s -> {
            SyaryoAnalizer analizer = new SyaryoAnalizer(s);
            int i = list.indexOf(s.name);
            analizer.get().get("KOMTRAX_SMR").entrySet().stream().forEach(smr -> {
                String date = smr.getKey().split("#")[0];
                String value = smr.getValue().get(kmsmrIdx).toString();

                Integer age = analizer.age(date);
                if (smrData.get(age) == null) {
                    smrData.put(age, new String[list.size()]);
                }

                smrData.get(age)[i] = value;
            });

            analizer.get().get("KOMTRAX_FUEL_CONSUME").entrySet().stream().forEach(fuel -> {
                String date = fuel.getKey().split("#")[0];
                String value = fuel.getValue().get(fuelIdx).toString();

                Integer age = analizer.age(date);
                if (fuelData.get(age) == null) {
                    fuelData.put(age, new String[list.size()]);
                }

                fuelData.get(age)[i] = value;
            });
        });

        try (PrintWriter pw = CSVFileReadWrite.writer("syaryo_smr_data.csv")) {
            //header +String.join(",", list)
            pw.println("Age,Cnt,Avg,Med,Max,Min");

            for (int age : smrData.keySet()) {
                if (age < 0) {
                    continue;
                }

                //if(age % 365 != 0)
                //    continue;
                String[] values = smrData.get(age);
                List<Integer> data = Arrays.asList(values).stream().filter(s -> s != null).map(s -> Integer.valueOf(s)).sorted().collect(Collectors.toList());

                Double avg = data.stream().mapToInt(s -> s).average().getAsDouble();
                Integer max = data.stream().mapToInt(s -> s).max().getAsInt();
                Integer min = data.stream().mapToInt(s -> s).min().getAsInt();
                Integer med = data.get(data.size() / 2);
                long cnt = data.stream().count();
                
                pw.println(age + "," + cnt + "," + avg + "," + med + "," + max + "," + min);
            }
        }
        
        try (PrintWriter pw = CSVFileReadWrite.writer("syaryo_fuel_data.csv")) {
            //header
            pw.println("Age,"+String.join(",", list));
            fuelData.entrySet().stream().map(f -> f.getKey()+","+String.join(",", f.getValue())).forEach(pw::println);
            
            /*
            pw.println("Age,Cnt,Avg,Med,Max,Min");

            for (int age : fuelData.keySet()) {
                if (age < 0) {
                    continue;
                }

                //if(age % 365 != 0)
                //    continue;

                String[] fvalues = fuelData.get(age);
                List<Double> fdata = Arrays.asList(fvalues).stream().filter(s -> s != null).map(s -> Double.valueOf(s)).sorted().collect(Collectors.toList());

                Double favg = fdata.stream().mapToDouble(s -> s).average().getAsDouble();
                Double fmax = fdata.stream().mapToDouble(s -> s).max().getAsDouble();
                Double fmin = fdata.stream().mapToDouble(s -> s).min().getAsDouble();
                Double fmed = fdata.get(fdata.size() / 2);
                long fcnt = fdata.stream().count();
                
                pw.println(age + "," + fcnt + "," + favg + "," + fmed + "," + fmax + "," + fmin);
            }*/
        }
    }
}
