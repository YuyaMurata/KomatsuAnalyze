/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.analize;

import data.filter.DataFilter;
import file.CSVFileReadWrite;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
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
        
        datafilter(syaryoMap, dataHeader);
        evalArray(syaryoMap, dataHeader, null, null);
    }

    private static void datafilter(Map<String, SyaryoObject4> syaryoMap, SyaryoObject4 dataHeader) {
        //定期メンテ削除
        List<String> reject = new ArrayList();
        List mainte = KomatsuDataParameter.PERIOD_MAINTE.get("受注.SGYO_KTICD");
        int idx = dataHeader.get("受注").get("受注").indexOf("SGYO_KTICD");
        List mainte2 = KomatsuDataParameter.PERIOD_MAINTE.get("作業.SGYOCD");
        int idx2 = dataHeader.get("作業").get("作業").indexOf("SGYOCD");
        syaryoMap.values().stream().forEach(s -> {
            if (s.get("作業") == null) {
                return;
            }

            Map works = s.get("作業").entrySet().stream()
                .filter(w -> !mainte.contains(s.get("受注").get(w.getKey().split("#")[0]).get(idx)))
                .filter(w -> !mainte2.contains(w.getValue().get(idx2)))
                .collect(Collectors.toMap(w -> w.getKey(), w -> w.getValue()));

            if (works != null) {
                s.put("作業", works);
            } else {
                reject.add(s.name);
            }
        });

        System.out.println("削除車両:" + reject);
        for (String name : reject) {
            syaryoMap.remove(name);
        }
    }

    private static void evalArray(Map<String, SyaryoObject4> syaryoMap, SyaryoObject4 dataHeader, List<String> error, List<String> service) {
        try (PrintWriter pw = CSVFileReadWrite.writer(exportFile + "_kmerrcd_evalarray.csv");
            PrintWriter pw2 = CSVFileReadWrite.writer(exportFile + "_sgcd_evalarray.csv")) {
            int err_idx = dataHeader.get("KOMTRAX_ERROR").get("KOMTRAX_ERROR").indexOf("ERROR_CODE");
            int sg_idx = dataHeader.get("作業").get("作業").indexOf("SGYOCD");

            Map<String, Integer[]> errcd = new HashMap();
            Map<String, Integer[]> devcd = new HashMap();

            List<String> names = new ArrayList(syaryoMap.keySet());
            syaryoMap.values().stream().forEach(s -> {
                int idx = names.indexOf(s.name);
                
                //KOMTRAX_ERROR
                s.get("KOMTRAX_ERROR").values().stream().map(err -> err.get(err_idx).toString()).forEach(cd -> {
                    if (errcd.get(cd) == null) {
                        errcd.put(cd, new Integer[syaryoMap.size()]);
                        Arrays.fill(errcd.get(cd), 0);
                    }
                    
                    errcd.get(cd)[idx]++;
                });

                //作業
                s.get("作業").values().stream().map(sg -> sg.get(sg_idx).toString()).forEach(cd -> {
                    try{
                    String dev = cd.substring(0, 4);
                    
                    if (devcd.get(cd) == null) {
                        devcd.put(cd, new Integer[syaryoMap.size()]);
                        Arrays.fill(devcd.get(cd), 0);
                    }
                    devcd.get(cd)[idx]++;
                    }catch(Exception e){
                        e.printStackTrace();
                        System.out.println(s.name);
                        System.out.println(cd);
                        System.exit(0);
                    }
                });
            });

            //出力
            if (error == null) {
                error = new ArrayList(errcd.keySet());
            }
            if (service == null) {
                service = new ArrayList(devcd.keySet());
            }
            
            pw.println("コード,"+String.join(",", error));
            pw2.println("コード,"+String.join(",", service));
            for (String name : syaryoMap.keySet()) {
                List<String> str = error.stream().map(err -> errcd.get(err)==null?"N/A":errcd.get(err)[names.indexOf(name)].toString()).collect(Collectors.toList());
                pw.println(name+","+String.join(",", str));
                
                str = service.stream().map(sv -> devcd.get(sv)==null?"N/A":devcd.get(sv)[names.indexOf(name)].toString()).collect(Collectors.toList());
                pw2.println(name+","+String.join(",", str));
            }
        }
    }
}
