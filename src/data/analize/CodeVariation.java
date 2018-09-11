/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.analize;

import file.CSVFileReadWrite;
import java.io.PrintWriter;
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
public class CodeVariation {
    private static final String exportFile = "ExportData_PC200_ALL.json";
    
    public static void main(String[] args) {
        Map<String, SyaryoObject4> syaryoMap = new SyaryoToZip3().readJSON(exportFile);
        
        SyaryoObject4 dataHeader = syaryoMap.get("_headers");
        syaryoMap.remove("_headers");
        System.out.println(dataHeader.dump());
        
        partscd(syaryoMap, dataHeader);
    }
    
    //KOMTRAX エラーコード
    private static void kmerrcode(Map<String, SyaryoObject4> syaryoMap, SyaryoObject4 dataHeader){
        if(dataHeader.get("KOMTRAX_ERROR") == null){
            System.err.println("Do not exported KOMTRAX ERROR DATA!");
            return ;
        }

        //発生日数と発生台数
        Map<String, Integer> occErrDays = new HashMap();
        Map<String, Integer> occErrNum = new HashMap();
        
        //ERROR Code 抽出
        syaryoMap.values().stream().forEach(s -> {
            if(s.get("KOMTRAX_ERROR") == null)
                return ;
            
            List<String> errs = s.get("KOMTRAX_ERROR").values().stream()
                                        .map(d -> d.get(dataHeader.get("KOMTRAX_ERROR").get("KOMTRAX_ERROR").indexOf("ERROR_CODE")).toString())
                                        .collect(Collectors.toList());
            
            for(String e : errs){
                if(occErrDays.get(e) == null)
                    occErrDays.put(e, 0);
                
                occErrDays.put(e, occErrDays.get(e)+1);
            }
            
            for(String e : errs.stream().distinct().collect(Collectors.toList())){
                if(occErrNum.get(e) == null)
                    occErrNum.put(e, 0);
                
                occErrNum.put(e, occErrNum.get(e)+1);
            }
        });
        
        try(PrintWriter csv = CSVFileReadWrite.writer(exportFile+"errcode.csv")){
            csv.println("KOMTRAXエラーコード,発生台数,発生日数");
            for(String er : occErrNum.keySet()){
                csv.println(er+","+occErrNum.get(er)+","+occErrDays.get(er));
            }
        }
    }
    
    //作業コード
    private static void workcd(Map<String, SyaryoObject4> syaryoMap, SyaryoObject4 dataHeader){
        if(dataHeader.get("作業") == null){
            System.err.println("Do not exported KOMPAS WORKING DATA!");
            return ;
        }
        
        //発生日数と発生台数
        Map<String, Integer> occSgDays = new HashMap();
        Map<String, Integer> occSgNum = new HashMap();
        
        //作業コード 抽出
        List mainte = KomatsuDataParameter.PERIOD_MAINTE.get("受注.SGYO_KTICD");
        syaryoMap.values().stream().forEach(s -> {
            if(s.get("作業") == null)
                return ;
            
            List<String> sgs = s.get("作業").entrySet().stream()
                                            .filter(d -> !mainte.contains(s.get("受注").get(d.getKey().split("#")[0]).get(dataHeader.get("受注").get("受注").indexOf("SGYO_KTICD"))))
                                            .map(d -> "'"+d.getValue().get(dataHeader.get("作業").get("作業").indexOf("SGYOCD")).toString())
                                            .collect(Collectors.toList());
            
            for(String sg : sgs){
                if(occSgDays.get(sg) == null)
                    occSgDays.put(sg, 0);
                
                occSgDays.put(sg, occSgDays.get(sg)+1);
            }
            
            for(String sg : sgs.stream().distinct().collect(Collectors.toList())){
                if(occSgNum.get(sg) == null)
                    occSgNum.put(sg, 0);
                
                occSgNum.put(sg, occSgNum.get(sg)+1);
            }
        });
        
        try(PrintWriter csv = CSVFileReadWrite.writer(exportFile+"sgcode.csv")){
            csv.println("作業コード,発生台数,発生日数");
            for(String sg : occSgNum.keySet()){
                csv.println(sg+","+occSgNum.get(sg)+","+occSgDays.get(sg));
            }
        }
    }
    
    //品番
    private static void partscd(Map<String, SyaryoObject4> syaryoMap, SyaryoObject4 dataHeader){
        if(dataHeader.get("部品") == null){
            System.err.println("Do not exported KOMPAS PARTS DATA!");
            return ;
        }
        
        //発生日数と発生台数
        Map<String, Integer> occPDays = new HashMap();
        Map<String, Integer> occPNum = new HashMap();
        
        //品番 抽出
        syaryoMap.values().stream().forEach(s -> {
            if(s.get("部品") == null)
                return ;
            
            List<String> parts = s.get("部品").entrySet().stream()
                                            .filter(d -> d.getValue().get(dataHeader.get("部品").get("部品").indexOf("None")).equals("10") ||
                                                            d.getValue().get(dataHeader.get("部品").get("部品").indexOf("None")).equals("20"))
                                            .map(d -> d.getValue().get(dataHeader.get("部品").get("部品").indexOf("HNBN"))
                                                        +"_"+d.getValue().get(dataHeader.get("部品").get("部品").indexOf("None"))
                                                        +"_"+d.getValue().get(dataHeader.get("部品").get("部品").indexOf("BHN_NM")))
                                            .collect(Collectors.toList());
            
            for(String pa : parts){
                if(occPDays.get(pa) == null)
                    occPDays.put(pa, 0);
                
                occPDays.put(pa, occPDays.get(pa)+1);
            }
            
            for(String pa : parts.stream().distinct().collect(Collectors.toList())){
                if(occPNum.get(pa) == null)
                    occPNum.put(pa, 0);
                
                occPNum.put(pa, occPNum.get(pa)+1);
            }
        });
        
        try(PrintWriter csv = CSVFileReadWrite.writer(exportFile+"partsno.csv")){
            csv.println("品番,メーカ,品名,発生台数,発生日数");
            for(String pa : occPNum.keySet()){
                if(pa.contains("600-319-3610")){
                    System.out.println(pa);
                    System.out.println(pa.hashCode());
                }
                
                csv.println(pa.split("_")[0]+","+pa.split("_")[1]+","+pa.split("_")[2]+","+occPNum.get(pa)+","+occPDays.get(pa));
            }
        }
    }
}
