/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package export;

import creator.template.SimpleTemplate;
import file.CSVFileReadWrite;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import json.SyaryoTemplateToJson;
import param.KomatsuDataParameter;

/**
 *
 * @author ZZ17390
 */
public class ErrProcDataCount {
    public static void main(String[] args) {
        Map<String, Integer[]> total = new HashMap();
        int cnt = 0;
        StringBuilder header = new StringBuilder();
        
        for(String errsource : KomatsuDataParameter.ERR_SOURCE){
            header.append(errsource+",");
            Map<String, SimpleTemplate> err = new SyaryoTemplateToJson().reader("error_proc\\"+errsource+"_error.json");
            for(String name : err.keySet()){
                System.out.println(name);
                String kisy = "";
                try{
                    kisy = name.split("-")[0];
                }catch(ArrayIndexOutOfBoundsException e){
                    kisy = "NaN";
                }
                    
                if(total.get(kisy) == null){
                    total.put(kisy, new Integer[4]);
                    for(int i=0; i < KomatsuDataParameter.ERR_SOURCE.length; i++)
                        total.get(kisy)[i] = 0;
                }
                
                int errcnt = Integer.valueOf(err.get(name).name.get(0));
                total.get(kisy)[cnt] = total.get(kisy)[cnt]+errcnt;
            }
            
            cnt++;
        }
        
        try(PrintWriter csv = CSVFileReadWrite.writer("err_procSoruceCount.csv")){
            csv.println("KISY,"+header.toString()+"Total");
            
            for(String kisy : total.keySet()){
                String row = Arrays.asList(total.get(kisy)).stream().map(s -> s.toString()).collect(Collectors.joining(","));
                Integer t = Arrays.asList(total.get(kisy)).stream().mapToInt(s -> s).sum();
                csv.println(kisy+","+row+","+t);
            }
        }
    }
}
