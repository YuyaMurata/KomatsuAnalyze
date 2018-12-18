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
import obj.SyaryoObject;
import param.KomatsuDataParameter;

/**
 *
 * @author ZZ17390
 */
public class TimeSpreadChart extends ChartTemplate {
    private Map<String, List> kmgps(SyaryoObject syaryo) {
        int fuelidx = 0;
        if (syaryo.get("KOMTRAX_GPS") == null) {
            return null;
        }
        
        for (String date : syaryo.get("KOMTRAX_GPS").keySet()) {
            List<Double> latlng = (List<Double>) syaryo.get("KOMTRAX_GPS").get(date).stream().map(v -> Double.valueOf(v.toString())).collect(Collectors.toList());
        }

        return null;
    }

    private Map<String, List> kmerror(SyaryoObject syaryo) {
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
    
    private Map<String, String> graphFormatter(Map<String, List> data, Integer idx){
        if(idx < 0)
            return data.entrySet().stream()
                        .collect(Collectors.toMap(e -> e.getKey(), e -> String.join(",", e.getValue())));
        else
            return data.entrySet().stream()
                        .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue().get(idx).toString()));
    }

    @Override
    public Map<String, String> graphFile(String select, int idx, SyaryoObject syaryo) {
        try{
        if (select.equals("SMR") || select.equals("KOMTRAX_SMR") || select.equals("KOMTRAX_FUEL_CONSUME")) {
            Map data = syaryo.get(select);
            Map map = graphFormatter(data, idx);
            map.put("header", "Value");
            return map;
        } else if (select.equals("KOMTRAX_ERROR")) {
            Map data = kmerror(syaryo);
            return graphFormatter(data, -1);
        } else {
            return null;
        }
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
