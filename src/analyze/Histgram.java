/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analyze;

import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;

/**
 *
 * @author murata
 */
public class Histgram {
    public void create(List<Integer> data){
        
        TreeMap<Long, Integer> map = new TreeMap();
        
        //Sturges'rule
        long k = Math.round(Math.log(data.size())/Math.log(2));
        long min = data.stream().min(Comparator.naturalOrder()).get();
        long max = data.stream().max(Comparator.naturalOrder()).get();
        long width = (max-min) / k;
        for(int i=0; i < k; i++)
            map.put(min+(i*width), 0);
        
        for(Integer d : data){
            Long key = map.floorKey(d.longValue());
            map.put(key, map.get(key)+1);
        }
        
    }
    
    public static void main(String[] args) {
        
    }
}
