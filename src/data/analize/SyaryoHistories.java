/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.analize;

import analizer.SyaryoAnalizer;
import file.CSVFileReadWrite;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import obj.SyaryoLoader;

/**
 *
 * @author ZZ17807
 */
public class SyaryoHistories {
    private static SyaryoLoader LOADER = SyaryoLoader.getInstance();
    private static String KISY ="PC200";
    
    public static void main(String[] args) {
        LOADER.setFile(KISY+"_form");
        Map<String, String[]> map = new TreeMap();
        LOADER.getSyaryoMap().values().stream().forEach(syaryo ->{
            try(SyaryoAnalizer s = new SyaryoAnalizer(syaryo, Boolean.FALSE)){
                map.put(s.name, new String[]{s.lifestart, s.lifestop});
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        
        //map.entrySet().stream().map(e -> e.getKey()+":"+Arrays.asList(e.getValue())).forEach(System.out::println);
        
        List<String> term = map.values().stream()
                .flatMap(sa -> Arrays.asList(sa).stream().map(s -> Integer.valueOf(s.substring(0,4))))
                .distinct()
                .sorted()
                .map(s -> s.toString()).collect(Collectors.toList());
        
        try(PrintWriter pw = CSVFileReadWrite.writerSJIS("syaryo_history_"+KISY+".csv")){
            //header
            pw.println("Y,"+term.stream().flatMap(t -> IntStream.range(1, 13).boxed().map(i -> i==1?t:"")).collect(Collectors.joining(",")));
            pw.println("SID/M,"+term.stream().flatMap(t -> IntStream.range(1, 13).boxed().map(i -> i.toString())).collect(Collectors.joining(",")));
            
            //history
            DecimalFormat df = new DecimalFormat("00");
            map.entrySet().stream()
                    .map(e -> e.getKey()+","+term.stream()
                                                .flatMap(t -> IntStream.range(1, 13)
                                                        .boxed()
                                                        .map(i -> Integer.valueOf(t+df.format(i)))
                                                        .map(ym -> (Integer.valueOf(e.getValue()[0].substring(0, 6)) <= ym && ym <= Integer.valueOf(e.getValue()[1].substring(0, 6)))?"1":""))
                                                        .collect(Collectors.joining(",")))
                    .forEach(pw::println);
        }
    }    
}
