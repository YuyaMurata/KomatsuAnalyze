/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package creator.index;

import file.CSVFileReadWrite;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import json.MapIndexToJSON;
import static param.KomatsuDataParameter.PC_PID_SYSDEFNAME_INDEX_PATH;

/**
 *
 * @author ZZ17390
 */
public class FormPartsSystemDefine {

    public static void main(String[] args) throws IOException {
        Map<String, String> cpjson = new HashMap();
        Map<String, String> json = new HashMap();
        try (BufferedReader csv = CSVFileReadWrite.reader(PC_PID_SYSDEFNAME_INDEX_PATH)) {
            String line;
            while ((line = csv.readLine()) != null) {
                String pid = line.split(",")[0];
                
                String pn = "";
                if(line.split(",").length > 1)
                    pn = line.split(",")[1];

                if (!pn.equals("") && !pn.equals("－")) {
                    cpjson.put(pid, pn);
                } else {
                    json.put(pid, pn);
                }
            }
        }
        
        //整形
        for(String pid : json.keySet()){
            if(pid.length() < 6){
                cpjson.put(pid, json.get(pid));
                continue;
            }
            
            Optional<String> simular = cpjson.keySet().stream().filter(cp -> cp.length() > 5).filter(p -> pid.contains(p)).findFirst();
            if(simular.isPresent()){
                cpjson.put(pid, cpjson.get(simular.get()));
                System.out.println("form:"+pid+"->"+simular.get()+" ("+cpjson.get(simular.get())+")");
            }
        }
        
        new MapIndexToJSON().write(PC_PID_SYSDEFNAME_INDEX_PATH+".json", cpjson);
    }
}
