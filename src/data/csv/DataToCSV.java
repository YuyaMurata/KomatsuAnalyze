/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.csv;

import analizer.SyaryoAnalizer;
import data.filter.MainteFilter;
import file.CSVFileReadWrite;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import json.SyaryoToZip3;
import obj.SyaryoObject4;

/**
 *
 * @author ZZ17390
 */
public class DataToCSV {

    private static final String exportFile = "ExportData_PC200_ALL.json";

    public static void main(String[] args) {
        Map<String, SyaryoObject4> syaryoMap = new SyaryoToZip3().readJSON(exportFile);

        SyaryoObject4 dataHeader = syaryoMap.get("_headers");
        syaryoMap.remove("_headers");
        System.out.println(dataHeader.dump());

        mainte("PC200_parts_data.csv", dataHeader, syaryoMap);
    }

    private static void mainte(String name, SyaryoObject4 header, Map<String, SyaryoObject4> map) {
        int sgkt = header.get("受注").get("受注").indexOf("SGYO_KTICD");
        int hnbn = header.get("部品").get("部品").indexOf("HNBN");
        int price = header.get("部品").get("部品").indexOf("SKKG");
        int quant = header.get("部品").get("部品").indexOf("JISI_SU");

        try (PrintWriter pw = CSVFileReadWrite.writer(name)) {
            //Header
            pw.println("SBN,SGKT," + String.join(",", header.get("部品").get("部品")) + ",単価,m_detect,d_sgkt,d_hnbn,d_egoil,d_pwoil,d_kes,d_price");

            map.values().stream().forEach(s -> {
                s.startHighPerformaceAccess();
                
                System.out.println(s.name);
                
                Map<String, List> p = s.get("部品");
                for (String id : p.keySet()) {
                    String sg = (String) s.get("受注").get(id.split("#")[0]).get(sgkt);
                    String hn = (String) p.get(id).get(hnbn);
                    
                    Integer pr = -1, q = -1, u = -1;
                    try{
                        pr = Integer.valueOf((String) p.get(id).get(price));
                        q = Integer.valueOf((String) p.get(id).get(quant));
                        u = pr / q;
                    }catch(NumberFormatException e){
                        System.err.println(s.name+":"+id+"["+p.get(id).get(price)+","+p.get(id).get(quant)+"]");
                        continue;
                    }
                    List<String> csv = new ArrayList();
                    csv.add(id);
                    csv.add(sg);
                    csv.addAll(p.get(id));
                    
                    csv.add(String.valueOf(pr/q));
                    
                    csv.add(MainteFilter.allDetect(sg, hn, u) ? "1" : "0");
                    csv.add(MainteFilter.skDetect(sg, hn, u) ? "1" : "0");
                    csv.add(MainteFilter.partsDetect(sg, hn, u) ? "1" : "0");
                    csv.add(MainteFilter.egoilDetect(sg, hn, u) ? "1" : "0");
                    csv.add(MainteFilter.pwoilDetect(sg, hn, u) ? "1" : "0");
                    csv.add(MainteFilter.kesDetect(sg, hn, u) ? "1" : "0");
                    csv.add(MainteFilter.priceDetect(sg, hn, u) ? "1" : "0");

                    pw.println(String.join(",", csv));
                }
                
                s.stopHighPerformaceAccess();
            });
        }
    }
}
