/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package creator.form;

import file.ListToCSV;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import obj.SyaryoLoader;
import obj.SyaryoObject;
import param.KomatsuDataParameter;
import program.r.R;
import viewer.graph.TimeSpreadChart;

/**
 *
 * @author ZZ17390
 */
public class SMRDataFormalize {

    private static String KISY = "PC200";
    private static SyaryoLoader LOADER = SyaryoLoader.getInstance();

    public static void main(String[] args) {
        LOADER.setFile(KISY + "_km_form");
        Map<String, SyaryoObject> syaryoMap = LOADER.getSyaryoMap();

        SyaryoObject syaryo = syaryoMap.get("PC200-8-306598");
        Map<String, Integer> sv_smr = transSMRData(syaryo.get("SMR"), LOADER.index("SMR", "VALUE"));
        Map<String, Integer> km_smr = transSMRData(syaryo.get("KOMTRAX_SMR"), LOADER.index("KOMTRAX_SMR", "VALUE"));
        //Map<String, Integer> smr = mergeSMR(sv_smr, km_smr);
          
        Map km_ma = movingAverage(km_smr, 3, 3);
        Map km_dif = diff(km_smr, km_ma);
        Map km_out = rejectOuters(km_dif);
        
        Map sv_ma = movingAverage(sv_smr, 3, 3);
        Map sv_dif = diff(sv_smr, sv_ma);
        Map sv_out = rejectOuters(sv_dif);
        
        Map<String, Integer> smr_dif = mergeSMR(sv_dif, km_dif);
        Map<String, Integer> smr_out = mergeSMR(sv_out, km_out);
        
        //Map dm = mergeGraphData(km_smr, sv_smr); //KOMTRAX VS SERVICE SMR
        //Map dm0 = mergeGraphData(m, smr);    //MA(10) VS SMR
        //Map dm1 = mergeGraphData(dif, out); // DIFF VS OUT_DIFF
        //Map dm2 = mergeGraphData(dm1, dm); //SMR VS DIFF VS OUT_DIFF
        Map dmVS = mergeGraphData(smr_dif, smr_out);
        testGraph(syaryo, dmVS);
        //testGraph(syaryo, dm1);
        
        //Map dm = mergeData(smr, m);
        //testGraph(syaryo, dm);
        
        R.close();
    }

    private static Map transSMRData(Map<String, List> smr, int idx) {
        Map map = new TreeMap();

        smr.entrySet().forEach(s -> {
            map.put(s.getKey(), Integer.valueOf(s.getValue().get(idx).toString()));
        });

        return map;
    }

    private static Map movingAverage(Map<String, Integer> smr, int span, int min) {
        if (smr.size() < min) {
            return null;
        }

        Map ma = new TreeMap();
        Queue<Integer> q = new LinkedList();
        for (String date : smr.keySet()) {
            q.offer(smr.get(date));

            if (q.size() % span == 0) {
                Double value = q.stream().mapToInt(v -> v).average().getAsDouble();
                ma.put(date, value.intValue());
                q.poll();
            }
        }

        System.out.println(ma);
        return ma;
    }
    
    private static Map<String, Integer> diff(Map<String, Integer> map1, Map<String, Integer> map2){
        Map<String, Integer> map = new TreeMap<>();
        TreeMap<String, Integer> m1 = new TreeMap<>(map1);
        TreeMap<String, Integer> m2 = new TreeMap<>(map2);
        
        List<String> date = new ArrayList<>();
        date.addAll(map1.keySet());
        date.addAll(map2.keySet());
        
        date.stream().forEach(k -> {
            Integer s1=m1.get(k);
            Integer s2=m2.get(k);
            if(s1 == null){
                s1 = m1.ceilingEntry(k).getValue();
                if(s1 == null)
                    s1 = m1.lowerEntry(k).getValue();
            }
            
            if(s2 == null){
                s2 = m2.ceilingEntry(k).getValue();
                if(s2 == null)
                    s2 = m2.lowerEntry(k).getValue();
            }
                    
            map.put(k, Math.abs(s1-s2));
        });
        
        return map;
    }
    
    private static Map<String, Integer> rejectOuters(Map<String, Integer> map){
        List key = new ArrayList(map.keySet());
        List value = new ArrayList(map.values());
        
        Map<String, Integer> data = R.getInstance().detectOuters(key, value);
        
        return data;
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
            Double v1 = Math.pow(Double.valueOf(reg.get(d)),2);
            Double v2 = Math.pow(Double.valueOf(smrList.get(dates.indexOf(d)).toString()),2);
            Double dif = Math.abs(v1 - v2);
                
            String c = String.valueOf(dif.intValue());
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

    private static Map<String, String> PY_SCRIPT = KomatsuDataParameter.GRAPH_PY;
    private static String PY_CSV_FILE = KomatsuDataParameter.GRAPH_TEMP_FILE;
    private static String PY_PATH = KomatsuDataParameter.PYTHONE_PATH;

    private static void testGraph(SyaryoObject syaryo, Map<String, String> smr) {
        String select = "SMR";
        String script = PY_PATH + PY_SCRIPT.get(select);

        List<String> graphData = new ArrayList<>();
        graphData.add("Syaryo," + syaryo.name + ":" + select);
        
        //header
        String header = IntStream.range(1, smr.values().stream().findFirst().get().split(",").length+1)
                                .boxed().map(i -> "SMR"+i).collect(Collectors.joining(","));
        graphData.add("Date,"+header);

        smr.entrySet().stream().map(e -> e.getKey() + "," + e.getValue()).forEach(s -> graphData.add(s));

        //CSV生成
        ListToCSV.toCSV(PY_CSV_FILE, graphData);

        //Python 実行
        new TimeSpreadChart().exec(syaryo.getName(), script);
    }
    
    private static Map<String, Integer> mergeSMR(Map<String, Integer> map1, Map<String, Integer> map2){
        Map<String, Integer> map = new TreeMap<>();
        
        List<String> date = new ArrayList<>();
        date.addAll(map1.keySet());
        date.addAll(map2.keySet());
        
        date.stream().forEach(k -> {
            if(map1.get(k) != null)
                map.put(k, map1.get(k));
            else
                map.put(k, map2.get(k));
        });
        
        return map;
    }
    
    private static Map<String, String> mergeGraphData(Map map1, Map map2){
        Map<String, String> map = new TreeMap<>();
        
        List date = new ArrayList<>();
        date.addAll(map1.keySet());
        date.addAll(map2.keySet());
        
        date.stream().forEach(k -> {
            map.put(k.toString(), map1.get(k)+","+map2.get(k));
        });
        
        return map;
    }
}
