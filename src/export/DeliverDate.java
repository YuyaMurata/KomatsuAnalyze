/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package export;

import file.CSVFileReadWrite;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import json.JsonToSyaryoObj;
import obj.SyaryoObject2;

/**
 *
 * @author ZZ17390
 */
public class DeliverDate {
    private static String kisy = "PC200";
    
    public static void main(String[] args) {
        String filename = "json\\syaryo_obj_"+kisy+"_form.json";
        Map<String, SyaryoObject2> syaryoMap = new JsonToSyaryoObj().reader3(filename);
        
        String outputname = "deliver_date_"+kisy+".csv";
        try(PrintWriter csv = CSVFileReadWrite.writer(outputname)){
            extractDate(syaryoMap, csv);
        }
        
    }
    
    public static void extractDate(Map<String, SyaryoObject2> syaryoMap, PrintWriter csv){
        int cnt = 0;
        
        csv.println("ID,Type,New,Used");
        for(SyaryoObject2 syaryo : syaryoMap.values()){
            cnt++;
            
            StringBuilder sb = new StringBuilder();
            sb.append(syaryo.getName());
            sb.append(",");
            sb.append(syaryo.getType());
            sb.append(",");
            try{
                String newDate = syaryo.getNew().keySet().stream().findFirst().get();
                sb.append(newDate);
                sb.append(",");
            }catch(NullPointerException e){
                System.out.println(syaryo.getName());
                sb.append(" ");
                sb.append(",");
            }
            
            List<String> usedDate = new ArrayList<>();
            if(syaryo.getUsed() != null){
                for(String date : syaryo.getUsed().keySet()){
                    sb.append(date);
                    sb.append(",");
                }
            }
            sb.deleteCharAt(sb.lastIndexOf(","));
            
            csv.write(sb.toString());
            
            if(cnt % 1000 == 0)
                System.out.println(cnt+"台");
        }
    }
}
