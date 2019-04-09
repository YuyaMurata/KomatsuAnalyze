/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.analize;

import analizer.SyaryoAnalizer;
import file.CSVFileReadWrite;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import obj.SyaryoLoader;

/**
 *　経年SMRの台数行列
 * @author ZZ17807
 */
public class AgeSMRMatrix {
    private static SyaryoLoader LOADER = SyaryoLoader.getInstance();
    
    public static void main(String[] args) {
        LOADER.setFile("PC200_form");
        
        //
        Map<String, Integer> map = new TreeMap();
        LOADER.getSyaryoMap().values().stream().forEach(syaryo ->{
            try(SyaryoAnalizer s = new SyaryoAnalizer(syaryo, true)){
                String date;
                int smr = 0;
                Map<Integer, Integer> ysmr = new TreeMap();
                
                while((date = s.getSMRToDate(smr))!= null){
                    int y = s.age(date) / 365;

                    ysmr.put(y, smr);
                    
                    smr += 500;
                }
                
                ysmr.entrySet().stream().map(k -> k.getValue()+"_"+k.getKey()).forEach(key ->{
                    if(map.get(key) == null)
                        map.put(key, 0);
                    
                    map.put(key, map.get(key)+1);
                });
                
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        
        //Matrix
        List<Integer> hsmr = map.keySet().stream().map(k -> Integer.valueOf(k.split("_")[0])).distinct().sorted().collect(Collectors.toList());
        List<Integer> hy = map.keySet().stream().map(k -> Integer.valueOf(k.split("_")[1])).distinct().sorted().collect(Collectors.toList());
        Integer[][] mat = new Integer[hsmr.size()][hy.size()];
        IntStream.range(0, mat.length).forEach(i -> Arrays.fill(mat[i], 0));
        
        map.entrySet().stream().forEach(e ->{
            Integer smr = Integer.valueOf(e.getKey().split("_")[0]);
            Integer y = Integer.valueOf(e.getKey().split("_")[1]);
            mat[hsmr.indexOf(smr)][hy.indexOf(y)] = e.getValue();
            
        });
        
        //print
        try(PrintWriter pw = CSVFileReadWrite.writerSJIS("age_smr_all_matrix.csv")){
            //header
            pw.println("SMR/Y,"+hy.stream().map(y -> y.toString()).collect(Collectors.joining(",")));
            
            //data
            for(int i=0; i < mat.length; i++){
                System.out.println(hsmr.get(i)+","+Arrays.asList(mat[i]).stream().map(m -> m.toString()).collect(Collectors.joining(",")));
                pw.println(hsmr.get(i)+","+Arrays.asList(mat[i]).stream().map(m -> m.toString()).collect(Collectors.joining(",")));
            }
        }
        
    }
}
