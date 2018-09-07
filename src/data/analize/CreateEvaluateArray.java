/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.analize;

import file.CSVFileReadWrite;
import java.io.BufferedReader;
import java.io.IOException;
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
 * 評価行列の作成
 *  /  |code.A  code.B|
 *sid.A|   0      1   |
 *sid.B|   1      0   |
 * 現状 KOMTRAX_ERROR 作業のみから作成
 * @author ZZ17390
 */
public class CreateEvaluateArray {
    private static final String KISY = "PC200";
    private static final String exportFile = "ExportData_"+KISY+"_ALL.json";

    public static void main(String[] args) {
        Map<String, SyaryoObject4> syaryoMap = new SyaryoToZip3().readJSON(exportFile);

        SyaryoObject4 dataHeader = syaryoMap.get("_headers");
        syaryoMap.remove("_headers");
        //List usererrors = UserDefinedFile.filter(KomatsuUserParameter.PC200_ERRFILTER_FILE);
        //List kmerrors = new ArrayList(KomatsuDataParameter.PC_ERROR.keySet());
        
        datafilter(syaryoMap, dataHeader);
        
        //evalArray(syaryoMap, dataHeader, kmerrors, kmerrors);
        evalArray(syaryoMap, dataHeader, null, null);
        //testEvalArray();
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
        try (PrintWriter pw = CSVFileReadWrite.writer(KISY + "_kmerrcd_evalarray.csv");
            PrintWriter pw2 = CSVFileReadWrite.writer(KISY + "_sgcd_evalarray.csv")) {
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
                    String dev = cd;//.substring(0, 4);
                    
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
            service = service.stream().map(sv -> sv/*.substring(0, 4)*/).sorted().collect(Collectors.toList());
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
    
    private static void testEvalArray(){
        try(BufferedReader csv1 = CSVFileReadWrite.reader(KISY + "_kmerrcd_evalarray.csv");
            BufferedReader csv2 = CSVFileReadWrite.reader(KISY + "_sgcd_evalarray.csv")){
            
            String[] header1 = csv1.readLine().split(",");
            String[] header2 = csv2.readLine().split(",");
            
            //Header
            System.out.println("kmerr:"+(header1.length-1));
            System.out.println("sgdev:"+(header2.length-1));
            System.out.println("--------");
            
            String line;
            Map<String, List<Integer>> map1 = new HashMap();
            while((line = csv1.readLine()) != null){
                String[] data = line.split(",");
                for(int i=1; i < data.length ; i++){
                    if(map1.get(header1[i]) == null)
                        map1.put(header1[i], new ArrayList());
                    map1.get(header1[i]).add(Integer.valueOf(data[i]));
                }
            }
            //Map1 Sum
            map1.entrySet().stream()
                            .map(d -> d.getKey()+":"+d.getValue().stream().mapToInt(l -> l).sum())
                            .forEach(System.out::println);
            System.out.println("Total1 = "+map1.values().stream().mapToInt(l -> l.stream().mapToInt(d -> d).sum()).sum());
            System.out.println("--------");
            
            Map<String, List<Integer>> map2 = new HashMap();
            while((line = csv2.readLine()) != null){
                String[] data = line.split(",");
                for(int i=1; i < data.length ; i++){
                    if(map2.get(header2[i]) == null)
                        map2.put(header2[i], new ArrayList());
                    map2.get(header2[i]).add(Integer.valueOf(data[i]));
                }
            }
            //Map2 Sum
            map2.entrySet().stream()
                            .map(d -> d.getKey()+":"+d.getValue().stream().mapToInt(l -> l).sum())
                            .forEach(System.out::println);
            System.out.println("Total2 = "+map2.values().stream().mapToInt(l -> l.stream().mapToInt(d -> d).sum()).sum());
            System.out.println("--------");
            
            //map1 ∧ map2
            List<String> m1andm2 = map1.keySet().stream().filter(d -> map2.get(d.substring(0,4))!=null).collect(Collectors.toList());
            System.out.println("kmerr:"+m1andm2.size());
            System.out.println("sgdev:"+m1andm2.stream().map(s -> s.substring(0,4)).distinct().count());
            m1andm2.stream().map(k -> k+":"+map1.get(k).stream().mapToInt(d->d).sum()+","+map2.get(k.substring(0, 4)).stream().mapToInt(d->d).sum()).forEach(System.out::println);
            System.out.println("1&2Total = "
                                    + m1andm2.stream().mapToInt(k -> map1.get(k).stream().mapToInt(d->d).sum()).sum()+","
                                    + m1andm2.stream().map(k ->k.substring(0,4)).distinct().mapToInt(dev -> map2.get(dev).stream().mapToInt(d->d).sum()).sum());
            System.out.println("--------");
            
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
