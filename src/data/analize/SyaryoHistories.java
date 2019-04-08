/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.analize;

import analizer.SyaryoAnalizer;
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
    
    public static void main(String[] args) {
        LOADER.setFile("PC200_form");
        Map<String, String[]> map = new TreeMap();
        LOADER.getSyaryoMap().values().stream().forEach(syaryo ->{
            try(SyaryoAnalizer s = new SyaryoAnalizer(syaryo, Boolean.FALSE)){
                map.put(s.name, new String[]{s.lifestart, s.lifestop});
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        
        map.entrySet().stream().map(e -> e.getKey()+":"+Arrays.asList(e.getValue())).forEach(System.out::println);
        
        List<String> term = map.values().stream()
                .flatMap(sa -> Arrays.asList(sa).stream().map(s -> Integer.valueOf(s.substring(0,4))))
                .distinct()
                .sorted()
                .map(s -> s.toString()).collect(Collectors.toList());
        term = term.stream().flatMap(s -> IntStream.range(1, 12).boxed().map(i -> s+i)).collect(Collectors.toList());
        System.out.println(term);
    }    
}
