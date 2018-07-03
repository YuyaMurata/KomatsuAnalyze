/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package check;

import file.CSVFileReadWrite;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import json.SyaryoToZip3;
import obj.SyaryoObject4;
import param.KomatsuDataParameter;

/**
 *
 * @author ZZ17390
 */
public class SyaryoDataExistsCheck {
    private static String syaryoPath = KomatsuDataParameter.SYARYOOBJECT_FDPATH+"syaryo_obj_PC138US_form.bz2";
    private static String filename = "PC138US_syaryoDataCheck.csv";
    
    public static void main(String[] args) {
        //Check Data
        //String[] ckey = new String[]{"顧客","受注", "作業", "部品"};
        String[] ckey = new String[]{"KOMTRAX_SMR","KOMTRAX_GPS", "KOMTRAX_ACT_DATA","KOMTRAX_FUEL_CONSUME", "KOMTRAX_ERROR"};
        Map<String, SyaryoObject4> syaryoMap = new SyaryoToZip3().read(syaryoPath);
        
        existsCheck(syaryoMap, ckey);
    }
    
    private static void existsCheck(Map<String, SyaryoObject4> syaryoMap, String[] ckey){
        try(PrintWriter csv = CSVFileReadWrite.writer(filename)){
            //Header
            Boolean kmflg = Arrays.asList(ckey).stream().filter(key -> key.contains("KOMTRAX")).findFirst().isPresent();
            if(kmflg)
                csv.println("SID,KOMTRAX_FLG"+String.join(",", ckey));
            else
                csv.println("SID,"+String.join(",", ckey));
            
            for(String name : syaryoMap.keySet()){
                SyaryoObject4 syaryo = syaryoMap.get(name);
                List<String> check = new ArrayList();
                
                syaryo.decompress();
                check.add(syaryo.name);
                
                if(kmflg)
                    check.add(komtraxCheck(syaryo.get("仕様")));
                
                for(String key : ckey){
                    if(syaryo.get(key) != null)
                        check.add("○");
                    else
                        check.add("×");
                }
                
                csv.println(String.join(",", check));
            }
        }
    }
    
    private static String komtraxCheck(Map<String, List> spec){
        if(spec.get("1").get(0).equals("1"))
            return "○";
        else
            return "×";
    }
}
