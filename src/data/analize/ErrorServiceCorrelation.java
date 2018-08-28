/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.analize;

import data.filter.DataFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import json.SyaryoToZip3;
import obj.SyaryoObject4;
import param.KomatsuDataParameter;

/**
 *
 * @author ZZ17390
 */
public class ErrorServiceCorrelation {
    private static final String exportFile = "ExportData_PC200_ALL.json";
    public static void main(String[] args) {
        Map<String, SyaryoObject4> syaryoMap = new SyaryoToZip3().readJSON(exportFile);
        
        SyaryoObject4 dataHeader = syaryoMap.get("_headers");
        syaryoMap.remove("_headers");
        
        //KM ErrorFilter
        //赤,黄,オレンジ
        List errorcd = new ArrayList(KomatsuDataParameter.PC_ERROR.keySet());
        syaryoMap.values().stream().forEach(s -> {
            Map temp = new HashMap();
            temp = DataFilter.simpleFilter(s.get("KOMTRAX_ERROR"), dataHeader.get("KOMTRAX_ERROR").get("KOMTRAX_ERROR").indexOf("ERROR_CODE"), errorcd, true);
            if(temp != null)
                s.put("KOMTRAX_ERROR", temp);
        });
        
        //ServiceFilter
        //定期メンテ<作業形態>
        List sgkt = KomatsuDataParameter.PERIOD_MAINTE.get("受注.SGYO_KTICD");
        //金額
        Integer price = 100_000;
        syaryoMap.values().stream().forEach(s -> {
            Map temp = new HashMap();
            temp = DataFilter.simpleFilter(s.get("受注"), dataHeader.get("受注").get("受注").indexOf("SGYO_KTICD"), sgkt, false);
            temp = 
            if(temp != null)
                s.put("KOMTRAX_ERROR", temp);
        });
        
        //WorkFilter
        //定期メンテ<作業コード>
        List sgcd = KomatsuDataParameter.PERIOD_MAINTE.get("作業.SGYOCD");
        
        
    }
}
