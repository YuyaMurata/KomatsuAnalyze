/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package export;

import file.CSVFileReadWrite;
import java.io.PrintWriter;
import java.util.Map;
import obj.LoadSyaryoObject;
import obj.SyaryoObject4;

/**
 *
 * @author ZZ17390
 */
public class ExportToCSV {
    private static Map<String, SyaryoObject4> syaryoMap;
    static String KISY = "PC200";
    
    public static void main(String[] args) {
        syaryoMap = LoadSyaryoObject.load(KISY + "_sv_form.bz2");
        
        //単一データのみ
        String data = "受注";
        String csv = KISY+"_"+data+"_cnt_dump.csv";
        
        //toCSV(csv, data);
        toCntCSV(csv, data);
    }
    
    public static void toCSV(String name, String data){
        try(PrintWriter pw = CSVFileReadWrite.writer(name)){
            syaryoMap.values().forEach(s ->{
                s.startHighPerformaceAccess();
                
                
                if(s.get(data)!= null)
                    s.get(data).entrySet().stream()
                                    .map(d -> s.name+","+d.getKey()+","+String.join(",", d.getValue()))
                                    .forEach(pw::println);
                
                s.stopHighPerformaceAccess();
            });
        }
    }
    
    public static void toCntCSV(String name, String data){
        try(PrintWriter pw = CSVFileReadWrite.writer(name)){
            syaryoMap.values().forEach(s ->{
                s.startHighPerformaceAccess();
                
                
                pw.println(s.name+","+(s.get(data)!=null?s.get(data).size():0));
                
                s.stopHighPerformaceAccess();
            });
        }
    }
}
