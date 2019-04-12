/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.analize;

import analizer.SyaryoAnalizer;
import file.CSVFileReadWrite;
import java.io.PrintWriter;
import java.util.ArrayList;
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
        Map<Integer, Integer> smrmap = new TreeMap();
        List<String> verilist = new ArrayList<>();
        LOADER.getSyaryoMap().values().stream().forEach(syaryo ->{
            try(SyaryoAnalizer s = new SyaryoAnalizer(syaryo, true)){
                String date;
                int smr = 0;
                Map<Integer, Integer> ysmr = new TreeMap();
                
                while((date = s.getSMRToDate(smr)) != null){
                    int y = s.age(date) / 365;
                    
                    ysmr.put(y, smr);
                    if(smrmap.get(smr) == null)
                        smrmap.put(smr, 0);
                    smrmap.put(smr, smrmap.get(smr)+1);
                    
                    //検証用
                    verilist.add(s.name+","+smr+","+date+","+s.getDateToSMR(date).getKey()+","+s.getDateToSMR(date).getValue());
                    
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
        Integer[][] mat = new Integer[hsmr.size()][hy.size()+1];
        IntStream.range(0, mat.length).forEach(i -> Arrays.fill(mat[i], 0));
        
        map.entrySet().stream().forEach(e ->{
            Integer smr = Integer.valueOf(e.getKey().split("_")[0]);
            Integer y = Integer.valueOf(e.getKey().split("_")[1]);
            mat[hsmr.indexOf(smr)][hy.indexOf(y)] = e.getValue();
        });
        
        //SMR残存数の追加
        hsmr.stream().forEach(hs ->{
            mat[hsmr.indexOf(hs)][hy.size()] = smrmap.get(hs);
        });
        
        //print
        try(PrintWriter pw = CSVFileReadWrite.writerSJIS("age_smr_all_matrix.csv")){
            //header
            pw.println("SMR/Y,"+hy.stream().map(y -> y.toString()).collect(Collectors.joining(","))+",SMR残存数");
            
            //data
            for(int i=0; i < mat.length; i++){
                System.out.println(hsmr.get(i)+","+Arrays.asList(mat[i]).stream().map(m -> m.toString()).collect(Collectors.joining(",")));
                pw.println(hsmr.get(i)+","+Arrays.asList(mat[i]).stream().map(m -> m.toString()).collect(Collectors.joining(",")));
            }
            List<String> ytotal = IntStream.range(0, mat[0].length-1)
                                        .map(i -> IntStream.range(0, mat.length).map(j -> mat[j][i]).sum())
                                        .boxed().map(v -> v.toString()).collect(Collectors.toList());
            pw.println("経年残存数,"+String.join(",", ytotal));
        }
        
        //検証用
        try(PrintWriter pw = CSVFileReadWrite.writerSJIS("age_smr_all_matrix_verify.csv")){
            pw.println("SID,FSMR,日付,経過年,SMR");
            verilist.stream().forEach(pw::println);
        }
    }
}
