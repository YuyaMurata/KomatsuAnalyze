/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package export;

import data.eval.UseEvaluate;
import file.CSVFileReadWrite;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
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
        
        //System.out.println(data);
        
        String key = "LOADMAP_実エンジン回転VSエンジントルク";
        Map<String, List<String>> rank = getData(key, data);
        rank.entrySet().stream().forEach(r ->{
            matrixPrint(key+"_Rank"+r.getKey()+".csv", "tgt↓Di→", r.getValue(), eval.header(key));
        });
        
        
        String key2 = "LOADMAP_エンジン水温VS作動油温";
        rank = getData(key2, data);
        rank.entrySet().stream().forEach(r ->{
            matrixPrint(key2+"_Rank"+r.getKey()+".csv", "cool↓hyd→", r.getValue(), eval.header(key2));
        });
    }
    
    public static Map getData(String key, Map<String, Map<String, List<String>>> data){
        Map<String, List<String>> rank = new HashMap();
        
        for(String name : data.keySet()){
            List<String> r = data.get(name).get(key);
            r.stream().forEach(ri ->{
                String i = String.valueOf(r.indexOf(ri));
                if(rank.get(i) == null)
                    rank.put(i, new ArrayList<>());
                rank.get(i).add(ri);
            });
        }
        
        return rank;
    }
    
    public static Integer[][] init(List<String> cols, List<String> rows){
        Integer[][] mat = new Integer[cols.size()][rows.size()];
        
        //init
        IntStream.range(0, cols.size()).forEach(i ->{
            Arrays.fill(mat[i], 0);
        });
        
        return mat;
    }
    
    //Matrix
    public static void matrixPrint(String name, String c, List<String> keys, List<String> header){
        List<String> cols = header.stream().map(k -> Integer.valueOf(k.split("_")[0])).distinct().sorted().map(k -> k.toString()).collect(Collectors.toList());
        List<String> rows = header.stream().map(k -> Integer.valueOf(k.split("_")[1])).distinct().sorted().map(k -> k.toString()).collect(Collectors.toList());
        
        Integer[][] mat = init(cols, rows);
        
        keys.stream().forEach(key ->{
            int col = cols.indexOf(key.split("_")[0]);
            int row = rows.indexOf(key.split("_")[1]);
            
            mat[col][row] += 1;
        });
        
        //print
        try(PrintWriter pw = CSVFileReadWrite.writerSJIS(name)){
            //header
            pw.println(c+","+String.join(",", rows));
            
            IntStream.range(0, cols.size()).forEach(i ->{
                pw.println(cols.get(i)+","+Arrays.asList(mat[i]).stream().map(m -> m.toString()).collect(Collectors.joining(",")));
            });
        }
    }
}
