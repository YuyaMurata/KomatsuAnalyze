/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.ga;

import data.analize.EvaluateCorrelation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.TreeMap;
import java.util.stream.Collectors;
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
    private Double CXPB = 0.8;
    private Double UNICX = 0.5;
    private Integer ELITE_G = 4;
    private static Integer len = 40;
    private double[][] fit;
    private double[] tg;
    private Map<List<Integer>, Double> POOL =new ConcurrentHashMap();
    
    public static void main(String[] args) {
        GeneticAlgorithm ga = new GeneticAlgorithm();
        ga.settings(new double[len][10], new double[10]);
        ga.initialize(ga.N);
    }
    
    public void settings(double[][] matrix, double[] target){
        len = matrix.length;
        fit = matrix;
        tg = target;
        
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
            POOL.put(gene, 0d);
        }
        
        fitness();
        
        //Test
        POOL.entrySet().stream().map(e -> e.getKey()+":"+e.getValue()).forEach(System.out::println);
    }
    
    //適応値の計算
    public void fitness(){
        POOL.entrySet().parallelStream().forEach(g ->{
            double[] farray = new double[fit[0].length];
            Arrays.fill(farray, 0d);
            
            IntStream.range(0, len)
                    .filter(i -> g.getKey().get(i)==1)
                    .forEach(i -> {
                        IntStream.range(0, farray.length).parallel()
                                .forEach(j -> farray[j] += fit[i][j]);
                    });
            
            //相関 正値と負値をとるため
            double f = Math.abs(EvaluateCorrelation.correlation(tg, farray));
            
            g.setValue(f);
        });
    }
    
    public List selection(){
        //エリート選択
        List<List<Integer>> select = POOL.entrySet().stream()
                                            .sorted(Entry.comparingByValue())
                                            .limit(ELITE_G)
                                            .map(e -> e.getKey())
                                            .collect(Collectors.toList());
        
        //ルーレット選択 (POOL size - Elite size)
        TreeMap<Double, List<Integer>> roullet = new TreeMap();
        Double acm = 0d;
        double sum = POOL.entrySet().parallelStream().filter(g -> !select.contains(g.getKey()))
                                                .mapToDouble(g -> g.getValue()).sum();
        for(List<Integer> key : POOL.keySet().stream().filter(g -> !select.contains(g)).collect(Collectors.toList())){
            roullet.put(acm, key);
            acm += POOL.get(key) / sum;
        }
        while(select.size() < N)
            select.add(roullet.floorEntry(RAND.nextDouble()).getValue());
        
        return select;
    }
    
    //交叉 (一様交叉)
    public void crossover(List<Integer> p1, List<Integer> p2){
        if(RAND.nextDouble() >= CXPB)
            return;
        
        List<Integer> child = new ArrayList<>();
        IntStream.range(0, p1.size()).parallel()
                .filter(i -> RAND.nextDouble() >= UNICX)
                .forEach(i -> {
                    Integer sw = p1.get(i);
                    p1.set(i, p2.get(i));
                    p2.set(i, sw);
                });
    }
    
    //突然変異
    public void mutable(List<Integer> p){
        if(RAND.nextDouble() >= MUPB)
            return ;
            
        IntStream.range(0, p.size()).parallel()
                            .filter(i -> RAND.nextDouble() < MUPB)
                            .forEach(i -> p.set(i, (p.get(i)+1)%2));
    }
    
    public void next(){
        
    }
}
