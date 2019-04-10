/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package viewer.graph;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import obj.SyaryoObject;
import param.KomatsuUserParameter;

/**
 *
 * @author ZZ17390
 */
public class TimeSpreadChart extends ChartTemplate {
    
    
    private Map<String, List> kmerror(SyaryoObject syaryo) {
        int errorCodeIdx = 0;
        int errorIdx = 1;
        if (syaryo.get("KOMTRAX_ERROR") == null) {
            return null;
        }

        //エラーコード抽出
        List<String> code = syaryo.get("KOMTRAX_ERROR").values().stream()
            .map(s -> s.get(errorCodeIdx))
            .distinct()
            .collect(Collectors.toList());

        Map<String, List> data = new TreeMap();
        data.put("header", code);

        String temp = "";
        for (String date : syaryo.get("KOMTRAX_ERROR").keySet()) {
            List l = new ArrayList();

            String c = syaryo.get("KOMTRAX_ERROR").get(date).get(errorCodeIdx);
            String cnt = syaryo.get("KOMTRAX_ERROR").get(date).get(errorIdx);
            
            date = date.split("#")[0];
            
            for (String cd : code) {
                if (c.equals(cd)) {
                    l.add(cnt);
                } else {
                    if (temp.equals("")) {
                        l.add("0");
                    } else {
                        l.add(data.get(temp).get(code.indexOf(cd)));
                    }
                }
            }
            data.put(date, l);
            temp = date;
        }

        return data;
    }

    private void graphFormatter(List<String> graphData, Map<String, List> data, Integer idx) {
        List gdata;

        switch (idx) {
            case -1:
                gdata = (List) data.entrySet().stream()
                    .map(e -> e.getKey() + "," + String.join(",", e.getValue()))
                    .collect(Collectors.toList());
                break;
            case -2:
                gdata = (List) data.entrySet().stream()
                    .map(e -> String.join(",", e.getKey().replace("～", "~").split("_")) + "," + String.join(",", e.getValue()))
                    .collect(Collectors.toList());
                break;
            default:
                gdata = data.entrySet().stream()
                    .map(e -> e.getKey() + "," + e.getValue().get(idx))
                    .collect(Collectors.toList());
        }

        graphData.addAll(gdata);
    }

    @Override
    public List<String> graphFile(String select, int idx, SyaryoObject syaryo) {
        try {
            List<String> graphData = new ArrayList<>();
            graphData.add("Syaryo,"+syaryo.name+":"+select);
            
            if (select.equals("SMR") || select.equals("KOMTRAX_SMR") || select.equals("KOMTRAX_FUEL_CONSUME") || select.equals("KOMTRAX_ACT_DATA") ) {
                //header
                graphData.add("Date,SMR");
                
                Map data = syaryo.get(select);
                graphFormatter(graphData, data, idx);
            } else if (select.equals("KOMTRAX_ERROR")) {
                Map<String, List> data = kmerror(syaryo);
                
                //header
                graphData.add("Date,"+String.join(",", data.get("header")));
                data.remove("header");
                
                graphFormatter(graphData, data, -1);
            } else if (select.contains("LOADMAP")) {
                Map data = syaryo.get(select);
                if(KomatsuUserParameter.GRAPH_PY.get(select).contains("bar")){
                    //header
                    graphData.add("label,v");
                    graphFormatter(graphData, data, -1);
                }else{
                    //header
                    graphData.add("x,y,v");
                    graphFormatter(graphData, data, -2);
                }
            }
            
            System.out.println(graphData);
            return graphData;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
