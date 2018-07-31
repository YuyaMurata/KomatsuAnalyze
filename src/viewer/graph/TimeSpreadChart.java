/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package viewer.graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import obj.SyaryoObject4;
import param.KomatsuDataParameter;

/**
 *
 * @author ZZ17390
 */
public class TimeSpreadChart extends ChartTemplate {

    private static String py_path = KomatsuDataParameter.GRAPH_PY;

    private Map<String, List> smr(SyaryoObject4 syaryo) {
        int smridx = 2;
        //System.out.println(syaryo);
        if (syaryo.get("SMR") == null) {
            return null;
        }

        Map<String, List> data = new TreeMap();
        data.put("header", Arrays.asList(new String[]{"SMR"}));
        
        for (String date : syaryo.get("SMR").keySet()) {
            List l = new ArrayList();
            l.add(syaryo.get("SMR").get(date).get(smridx));
            data.put(date, l);
        }

        return data;
    }

    private Map<String, List> kmsmr(SyaryoObject4 syaryo) {
        int smridx = 0;
        if (syaryo.get("KOMTRAX_SMR") == null) {
            return null;
        }

        Map<String, List> data = new TreeMap();
        data.put("header", Arrays.asList(new String[]{"KMSMR"}));
        
        for (String date : syaryo.get("KOMTRAX_SMR").keySet()) {
            List l = new ArrayList();
            l.add(syaryo.get("KOMTRAX_SMR").get(date).get(smridx));
            data.put(date, l);
        }

        return data;
    }
    
    private Map<String, List> kmfuel(SyaryoObject4 syaryo) {
        int fuelidx = 0;
        if (syaryo.get("KOMTRAX_FUEL_CONSUME") == null) {
            return null;
        }

        Map<String, List> data = new TreeMap();
        data.put("header", Arrays.asList(new String[]{"FUEL"}));
        
        for (String date : syaryo.get("KOMTRAX_FUEL_CONSUME").keySet()) {
            List l = new ArrayList();
            l.add(syaryo.get("KOMTRAX_FUEL_CONSUME").get(date).get(fuelidx));
            data.put(date, l);
        }

        return data;
    }

    private Map<String, List> kmerror(SyaryoObject4 syaryo) {
        int errorCodeIdx = 0;
        int errorIdx = 1;
        if (syaryo.get("KOMTRAX_ERROR") == null) {
            return null;
        }

        //エラーコード抽出
        List<String> code = syaryo.get("KOMTRAX_ERROR").values().stream()
                .map(s -> s.get(errorCodeIdx).toString())
                .distinct()
                .collect(Collectors.toList());
        
        Map<String, List> data = new TreeMap();
        data.put("header", code);
        
        String temp = "";
        for (String date : syaryo.get("KOMTRAX_ERROR").keySet()) {
            List l = new ArrayList();

            String c = syaryo.get("KOMTRAX_ERROR").get(date).get(errorCodeIdx).toString();
            String cnt = syaryo.get("KOMTRAX_ERROR").get(date).get(errorIdx).toString();
            for (String cd : code) {
                if (c.equals(cd)) {
                    l.add(cnt);
                } else {
                    if(temp.equals(""))
                        l.add("0");
                    else
                        l.add(data.get(temp).get(code.indexOf(cd)));
                }
            }
            data.put(date, l);
            temp = date;
        }

        return data;
    }

    @Override
    public Map<String, List> graphFile(String select, SyaryoObject4 syaryo) {
        setGraphPath(py_path);
        if (select.equals("SMR")) {
            return smr(syaryo);
        } else if (select.equals("KOMTRAX_SMR")) {
            return kmsmr(syaryo);
        } else if (select.equals("KOMTRAX_ERROR")) {
            return kmerror(syaryo);
        } else if (select.equals("KOMTRAX_FUEL_CONSUME")) {
            return kmfuel(syaryo);
        } else {
            return null;
        }
    }
}
