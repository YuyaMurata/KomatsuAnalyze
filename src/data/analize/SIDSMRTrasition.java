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
    private static final String exportFile = "ExportData_SMR_"+KISY+"_ALL.json";
    //private static final String exportFile = "FORM_ExportData_SMR_"+KISY+"_ALL.json.bz2";
    
    
    public static void main(String[] args) {
        Map<String, SyaryoObject4> syaryoMap = new SyaryoToZip3().readJSON(exportFile);
        
        SyaryoObject4 dataHeader = syaryoMap.get("_headers");
        syaryoMap.remove("_headers");
        
        formalize(syaryoMap, dataHeader);
        
        //transition(syaryoMap, dataHeader);
    }
    
    private static void formalize(Map<String, SyaryoObject4> map, SyaryoObject4 header){
        Map<String, Map> outMap = new TreeMap();
        
        map.values().stream().forEach(s -> {
            s.startHighPerformaceAccess();
            
            System.out.println(s.name);
            
            Map<String, Map> expMap = new TreeMap();
            expMap.put("SMR", formSMR(s.get("SMR"), header.get("SMR").get("SMR").indexOf("SVC_MTR")));
            expMap.put("KOMTRAX_SMR", formSMR(s.get("KOMTRAX_SMR"), header.get("KOMTRAX_SMR").get("KOMTRAX_SMR").indexOf("SMR_VALUE")));
            
            outMap.put(s.name, expMap);
            
            s.stopHighPerformaceAccess();
        });
        
        Map<String, Map> expMap = new TreeMap();
        expMap.put("SMR", header.get("SMR"));
        expMap.put("KOMTRAX_SMR", header.get("KOMTRAX_SMR"));
        
        outMap.put("_header", expMap);
        
        new MapIndexToJSON().write("FORM_"+exportFile, outMap);
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
    
    private static void transition(Map<String, SyaryoObject4> map, SyaryoObject4 header){
        try(PrintWriter pw = CSVFileReadWrite.writer("syaryo_smr_data.csv")){
            List<String> list = new ArrayList(map.keySet());
            Map<Integer, String[]> smrData = new TreeMap();
            
            int smrIdx = header.get("SMR").get("SMR").indexOf("SVC_MTR");
            int kmsmrIdx = header.get("KOMTRAX_SMR").get("KOMTRAX_SMR").indexOf("SMR_VALUE");
            
            map.values().stream().forEach(s ->{
                SyaryoAnalizer analizer = new SyaryoAnalizer(s);
                int i = list.indexOf(s.name);
                analizer.get().get("KOMTRAX_SMR").entrySet().stream().forEach(smr ->{
                    String date = smr.getKey().split("#")[0];
                    String value = smr.getValue().get(kmsmrIdx).toString();
                    
                    Integer age = analizer.age(date);
                    if(smrData.get(age) == null)
                        smrData.put(age, new String[list.size()]);
                    
                    smrData.get(age)[i] = value;
                });
            });
            
            //header
            pw.println("Age,"+String.join(",", list));
            smrData.entrySet().stream()
                    .map(data -> data.getKey()+","+String.join(",", data.getValue()))
                    .forEach(pw::println);
        }
    }
}
