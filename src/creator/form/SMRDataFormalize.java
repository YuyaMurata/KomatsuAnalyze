/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package creator.form;

import analizer.SyaryoAnalizer;
import file.CSVFileReadWrite;
import file.ListToCSV;
import file.UserDefinedFile;
import java.io.PrintWriter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.TreeMap;
import java.util.stream.Collectors;
import obj.LoadSyaryoObject;
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
        LOADER.setFile(KISY + "_sv_form");
        Map<String, SyaryoObject> syaryoMap = LOADER.getSyaryoMap();

        SyaryoObject syaryo = syaryoMap.get("PC200-8N1-315586");
        Map<String, Integer> smr = transSMRData(syaryo.get("SMR"), LOADER.index("SMR", "VALUE"));
        Map m = movingAverage(smr, 5, 5);

        testGraph(syaryo, smr);
        testGraph(syaryo, m);
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

    private static Map<String, String> PY_SCRIPT = KomatsuDataParameter.GRAPH_PY;
    private static String PY_CSV_FILE = KomatsuDataParameter.GRAPH_TEMP_FILE;
    private static String PY_PATH = KomatsuDataParameter.PYTHONE_PATH;

    private static void testGraph(SyaryoObject syaryo, Map<String, Integer> smr) {
        String select = "SMR";
        String script = PY_PATH + PY_SCRIPT.get(select);

        List<String> graphData = new ArrayList<>();
        graphData.add("Syaryo," + syaryo.name + ":" + select);
        //header
        graphData.add("Date,SMR");

        smr.entrySet().stream().map(e -> e.getKey() + "," + e.getValue()).forEach(s -> graphData.add(s));

        //CSV生成
        ListToCSV.toCSV(PY_CSV_FILE, graphData);

        //Python 実行
        new TimeSpreadChart().exec(syaryo.getName(), script);
    }
}
