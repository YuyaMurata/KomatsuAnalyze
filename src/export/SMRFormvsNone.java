/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package export;

import analizer.SyaryoAnalizer;
import file.CSVFileReadWrite;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import json.SyaryoToZip3;
import obj.SyaryoObject4;
import param.KomatsuDataParameter;
import program.r.R;

/**
 *
 * @author ZZ17390
 */
public class SMRFormvsNone {
    private static String PATH = KomatsuDataParameter.SYARYOOBJECT_FDPATH;
    private static String KISY = "PC200";
    private static String filename = PATH + "syaryo_obj_" + KISY + "_km_form.bz2";
    private static Map<String, SyaryoObject4> syaryoMap = new SyaryoToZip3().read(filename);
    
    private static String searchname = "user\\PC200SMR調査対象.csv";
    
    public static void main(String[] args) {
        List<String> s = getSyaryo();
        
        for(String id : s){
            SyaryoObject4 syaryo = syaryoMap.get(id);
            syaryo.startHighPerformaceAccess();
            
            Map<String, List> smr = syaryo.get("SMR");
            Map<String, List> kmsmr = syaryo.get("KOMTRAX_SMR");
            Map<String, List> fsmr = formSMR(smr, 2);
            Map<String, List> fkmsmr = formSMR(kmsmr, 0);
            
            try(PrintWriter pw = CSVFileReadWrite.writer("PC200SMR調査\\"+id+"_smr_graph.csv")){
                //header
                pw.println("日付,SMR,KOMTRAX_SMR,整形SMR,整形KOMTRAX_SMR");
                
                TreeMap date = new TreeMap(smr);
                date.putAll(kmsmr);
                
                for(Object d : date.keySet()){
                    String v = smr.get(d) != null ? smr.get(d).get(2).toString() : "NaN";
                    String kmv = kmsmr.get(d) != null ? kmsmr.get(d).get(0).toString() : "NaN";
                    String fv = fsmr.get(d) != null ? fsmr.get(d).get(2).toString() : "NaN";
                    String fkmv = fkmsmr.get(d) != null ? fkmsmr.get(d).get(0).toString() : "NaN";
                    
                    pw.println(d+","+v+","+kmv+","+fv+","+fkmv);
                }
            }
            
            syaryo.stopHighPerformaceAccess();
        }
        
        R.close();
    }
    
    private static List<String> getSyaryo(){
         List s = new ArrayList();
        try(BufferedReader csv = CSVFileReadWrite.reader(searchname)){
            String line;
            while((line = csv.readLine()) != null){
                s.add(line);
            }
        } catch (IOException ex) {
        }
        
        return s;
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
}
