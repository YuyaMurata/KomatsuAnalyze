/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.analize;

import file.CSVFileReadWrite;
import file.UserDefinedFile;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import json.MapIndexToJSON;
import json.SyaryoToZip3;
import obj.SyaryoObject4;
import param.KomatsuDataParameter;
import param.KomatsuUserParameter;

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
        List usererrors = UserDefinedFile.filter(KomatsuUserParameter.PC200_ERRFILTER_FILE);
        List kmerrors = new ArrayList(KomatsuDataParameter.PC_ERROR.keySet());
        
        datafilter(syaryoMap, dataHeader);
        evalArray(syaryoMap, dataHeader, kmerrors, kmerrors);
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
                .filter(w -> !w.getValue().get(idx2).equals("ZZU"))
                .filter(w -> !w.getValue().get(idx2).equals("None"))
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
                s.startHighPerformaceAccess();
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
                    String dev = cd.substring(0, 4);
                    
                    if (devcd.get(dev) == null) {
                        devcd.put(dev, new Integer[syaryoMap.size()]);
                        Arrays.fill(devcd.get(dev), 0);
                    }
                    devcd.get(dev)[idx]++;
                });
                
                s.stopHighPerformaceAccess();
            });

            //出力
            if (error == null) {
                error = new ArrayList(errcd.keySet());
            }
            if (service == null) {
                service = new ArrayList(devcd.keySet());
            }
            
            error = error.stream().sorted().collect(Collectors.toList());
            service = service.stream().map(sv -> sv.substring(0, 4)).sorted().collect(Collectors.toList());
            pw.println("コード,"+String.join(",", error));
            pw2.println("コード,"+String.join(",", service));
            for (String name : syaryoMap.keySet()) {
                List<String> str = error.stream().map(err -> errcd.get(err)==null?"N/A":errcd.get(err)[names.indexOf(name)].toString()).collect(Collectors.toList());
                pw.println(name+","+String.join(",", str));
                
                str = service.stream().map(dev -> devcd.get(dev)==null?"N/A":devcd.get(dev)[names.indexOf(name)].toString()).collect(Collectors.toList());
                pw2.println(name+","+String.join(",", str));
            }
        }
    }
}
