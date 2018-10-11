/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.ga;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.IntStream;
import org.apache.hive.druid.org.jboss.netty.util.internal.ConcurrentHashMap;

/**
 *
 * @author kaeru_yuya
 */
public class GeneticAlgorithm {
    private Random RAND = new Random();
    private Integer LOOP_N = 100;
    private Integer N = 100;
    private Double MUPB = 0.1;
    private Double CXPB = 0.5;
    private static Integer len = 40;
    private double[][] fit;
    private Map<List<Integer>, Integer> POOL =new ConcurrentHashMap();
    
    public static void main(String[] args) {
        GeneticAlgorithm ga = new GeneticAlgorithm();
        ga.settings(new double[len][10]);
        ga.initialize(ga.N);
    }
    
    public void settings(double[][] matrix){
        len = matrix.length;
        fit = matrix;
        
        //test
        for(int i=0; i < len; i++)
            for(int j=0; j < 10; j++)
                fit[i][j] = i+j;
    }
    
    //初期個体の生成
    public void initialize(int n){
        while(POOL.size() < n){
            List<Integer> gene = new ArrayList();
            IntStream.range(0, len).map(i -> RAND.nextInt(2)).forEach(gene::add);
            POOL.put(gene, 0);
        }
        
        fitness();
        
        //Test
        POOL.entrySet().stream().map(e -> e.getKey()+":"+e.getValue()).forEach(System.out::println);
    }
    
    public void fitness(){
        POOL.entrySet().parallelStream().forEach(g ->{
            Integer f = IntStream.range(0, len)
                        .filter(i -> g.getKey().get(i)==1)
                        .map(i -> IntStream.range(0, fit[i].length)
                                            .map(j -> Double.valueOf(fit[i][j]).intValue())
                                            .sum())
                        .sum();
            g.setValue(f);
        });
    }
    
    public void crossover(){
        
    }
    
    public void mutable(){
        
    }
    
    public void selection(){
        
    }
}
