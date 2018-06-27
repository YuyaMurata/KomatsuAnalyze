/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package export;

import creator.template.SimpleTemplate;
import file.CSVFileReadWrite;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
        Map<String, List<Integer>> total = new HashMap();
        int cnt = 0;
        StringBuilder header = new StringBuilder();
        
        for(String errsource : KomatsuDataParameter.ERR_SOURCE){
            header.append(errsource);
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
                    total.put(kisy, new ArrayList<>());
                    for(int i=0; i < KomatsuDataParameter.ERR_SOURCE.length; i++)
                        total.get(kisy).add(0);
                }
                
                int errcnt = Integer.valueOf(err.get(name).name.get(0));
                total.get(kisy).set(cnt, total.get(kisy).get(0)+errcnt);
            }
            
            cnt++;
        }
        
        try(PrintWriter csv = CSVFileReadWrite.writer("err_procSoruceCount.csv")){
            csv.println("KISY,"+String.join(",", header)+",Total");
            
            for(String kisy : total.keySet()){
                String row = total.get(kisy).stream().map(s -> s.toString()).collect(Collectors.joining(","));
                Integer t = total.get(kisy).stream().mapToInt(s -> s).sum();
                csv.println(kisy+","+row+","+t);
            }
        }
    }
}
