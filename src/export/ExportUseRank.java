/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package export;

import data.eval.UseEvaluate;
import file.CSVFileReadWrite;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import obj.SyaryoLoader;

/**
 *
 * @author ZZ17807
 */
public class ExportUseRank {
    private static SyaryoLoader LOADER = SyaryoLoader.getInstance();
    public static void main(String[] args) {
        LOADER.setFile("PC200_loadmap");
        
        UseEvaluate eval = new UseEvaluate();
        Map<String, Map<String, List<String>>> data = eval.getdata(LOADER.getSyaryoMap());
        
        System.out.println(data);
        
        String key = "LOADMAP_実エンジン回転VSエンジントルク";
        try(PrintWriter pw = CSVFileReadWrite.writerSJIS(key+"_Rank.csv")){
            for(String name : data.keySet()){
                List<String> rank = data.get(name).get(key);
                pw.println(name+","+String.join(",", rank));
            }
        }
        
        key = "LOADMAP_エンジン水温VS作動油温";
        try(PrintWriter pw = CSVFileReadWrite.writerSJIS(key+"_Rank.csv")){
            for(String name : data.keySet()){
                List<String> rank = data.get(name).get(key);
                pw.println(name+","+String.join(",", rank));
            }
        }
    }
}
