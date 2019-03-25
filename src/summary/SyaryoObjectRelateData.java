/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package summary;

import file.CSVFileReadWrite;
import file.ListToCSV;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import obj.SyaryoLoader;
import obj.SyaryoObject;
import param.KomatsuDataParameter;

/**
 *
 * @author ZZ17807
 */
public class SyaryoObjectRelateData {
    private static String KISY ="PC200";
    private static SyaryoLoader LOADER = SyaryoLoader.getInstance();
    
    public static void main(String[] args) {
        LOADER.setFile(KISY);
        
        KOMTRAX_LIST(LOADER.getSyaryoMap());
    }
    
    private static void KOMTRAX_LIST(Map<String, SyaryoObject> map){
        List<String> headers = LOADER.getHeader().getMap().keySet().stream()
                                        .filter(key -> key.contains("KOMTRAX"))
                                        .collect(Collectors.toList());
        
        Map<String, List<String>> out = new TreeMap();
        
        map.values().stream().forEach(s ->{
            s.startHighPerformaceAccess();
            
            String name = s.name;
            List<String> kmlist = headers.stream()
                                        .map(h -> s.get(h)==null?"0":"1")
                                        .collect(Collectors.toList());
            out.put(name, kmlist);
            
            s.stopHighPerformaceAccess();
        });
        
        Map<String, String> komtrax_file = ListToCSV.toMap(KomatsuDataParameter.KOMTRAX_FILE_DEFINE, 2, 3);
        
        try(PrintWriter pw = CSVFileReadWrite.writerSJIS(KISY+"_komtrax_データ取得.csv")){
            pw.println("SID,"+String.join(",", headers));
            pw.println(","+headers.stream().map(h -> komtrax_file.get(h)).collect(Collectors.joining(",")));
            out.entrySet().stream()
                        .map(o -> o.getKey()+","+String.join(",", o.getValue()))
                        .forEach(pw::println);
        }
    }
}
