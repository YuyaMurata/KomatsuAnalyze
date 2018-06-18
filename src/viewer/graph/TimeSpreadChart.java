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
import obj.SyaryoObject4;
import param.KomatsuDataParameter;

/**
 *
 * @author ZZ17390
 */
public class TimeSpreadChart extends ChartTemplate{
    private static String py_path = KomatsuDataParameter.GRAPH_PY;
    
    private Map<String, List> smr(SyaryoObject4 syaryo){
        int smridx = 2;
        //System.out.println(syaryo);
        if(syaryo.get("SMR") == null)
            return null;
        
        Map<String, List> data = new TreeMap();
        for(String date : syaryo.get("SMR").keySet()){
            List l = new ArrayList();
            l.add(syaryo.get("SMR").get(date).get(smridx));
            data.put(date.split("#")[0], l);
        }
        
        return data;
    }
    
    private Map<String, List> kmsmr(SyaryoObject4 syaryo){
        int smridx = 0;
        if(syaryo.get("KOMTRAX_SMR") == null)
            return null;
            
        Map<String, List> data = new TreeMap();
        for(String date : syaryo.get("KOMTRAX_SMR").keySet()){
            List l = new ArrayList();
            l.add(syaryo.get("KOMTRAX_SMR").get(date).get(smridx));
            data.put(date.split(" ")[0].replace("/", ""), l);
        }
        
        return data;
    }
        
    @Override
    public Map<String, List> graphFile(String select, String headers, SyaryoObject4 syaryo) {
        setGraphPath(py_path);
        if(select.equals("SMR"))
            return smr(syaryo);
        else if(select.equals("KOMTRAX_SMR"))
            return kmsmr(syaryo);
        else
            return null;
    }
}
