/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package export;

import file.CSVFileReadWrite;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import json.JsonToSyaryoObj;
import obj.SyaryoElements;
import obj.SyaryoObject2;

/**
 *
 * @author ZZ17390
 */
public class KomatsuCareReport {
    private static String kisy = "PC200";
    
    public static void main(String[] args) {
        String filename = "json\\syaryo_obj_"+kisy+"_form.json";
        Map<String, SyaryoObject2> syaryoMap = new JsonToSyaryoObj().reader3(filename);
        
        String outputname = "komatsu_care_"+kisy+".csv";
        try(PrintWriter csv = CSVFileReadWrite.writer(outputname)){
            exportKomatsuCare(syaryoMap, csv);
        }
    }
    
    public static void exportKomatsuCare(Map<String, SyaryoObject2> syaryoMap, PrintWriter csv){
        int cnt = 0;
        
        csv.println("Company,ID,Kisy,Type,日付,前受け金,金額");
        for(SyaryoObject2 syaryo : syaryoMap.values()){
            cnt++;
            
            StringBuilder sb = new StringBuilder();
            String comp = ExportTool.extractCompany(syaryo);
            sb.append(comp);
            sb.append(",");
            sb.append(syaryo.getName());
            sb.append(",");
            sb.append(syaryo.getMachine());
            sb.append(",");
            sb.append(syaryo.getType());
            sb.append(",");
            
            if(syaryo.getCarePrice() == null)
                continue;
            sb.append(syaryo.getNew().values().stream()
                                    .findFirst().get().get(SyaryoElements.New.Date.getNo()));
            sb.append(",");
            sb.append(syaryo.getCarePrice().values().stream()
                                    .findFirst().get().get(SyaryoElements.CarePrice.Price.getNo()));
            sb.append(",");
            
            if(syaryo.getCare() != null && syaryo.getOrder() != null){
                for(List care : syaryo.getCare().values()){
                    Optional<List> sbn = syaryo.getOrder().values().stream()
                                                    .filter(list -> list.get(SyaryoElements.Order.ID.getNo()).toString().contains(care.get(SyaryoElements.Care.CID.getNo()).toString()))
                                                    .findFirst();
                    
                    if(sbn.isPresent()){
                        /*sb.append(sbn.get().get(SyaryoElements.Order.Company.getNo()));
                        sb.append("_");
                        sb.append(sbn.get().get(SyaryoElements.Order.ID.getNo()));*/
                        sb.append(sbn.get().get(SyaryoElements.Order.Invoice.getNo()));
                    } else
                        sb.append("XX_"+care.get(SyaryoElements.Care.CID.getNo()));
                    /*sb.append("_");
                    sb.append(care.get(SyaryoElements.Care.KIND.getNo()));
                    sb.append(",");
                    sb.append(care.get(SyaryoElements.Care.Date.getNo()));
                    */sb.append(",");
                }
                sb.deleteCharAt(sb.lastIndexOf(","));
            }
            
            csv.println(sb.toString());
            
            if(cnt % 1000 == 0)
                System.out.println(cnt+"台");
        }
    }
}
