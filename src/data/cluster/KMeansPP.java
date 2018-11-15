/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.cluster;

import data.ga.Population;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 *
 * @author ZZ17390
 */
public class KMeansPP {
    private static final Random rand = new Random();
    Map<String, List<Double>> s;
    Map<String, Integer> cluster;
    
    int k = 0;
    
    public void set(int k, Map sample){
        this.k = k;
        this.s = sample;
    }
    
    //x, yでユークリッド距離計算
    private Double distance(List<Double> x, List<Double> y){
        Double d = IntStream.range(0, x.size())
                            .mapToDouble(i -> Math.pow(x.get(i)-y.get(i),2))
                            .sum();
        d = Math.sqrt(d);
        return d;
    }
    
    private Map initialize(){
        cluster = new HashMap<>();
        Map<String, List<Double>> c = new HashMap<>();
        Map<String, List<Double>> temp = new HashMap(s);
        
        //初期値選択
        String ci = new ArrayList<>(temp.keySet()).get(rand.nextInt(temp.size()));
        
        //初期 k点を選択
        for(int i=0; i < k; i++){
            c.put(ci, temp.get(ci));
            temp.remove(ci);
            
            //中心点とそれ以外で距離計算
            final String cj = ci;
            List<Double> d = temp.values().stream()
                                        .map(v -> distance(c.get(cj), v))
                                        .collect(Collectors.toList());
            
            //cjをルーレット選択
            TreeMap<Double, String> r = new TreeMap();
            Double acm = 0d;
            Double sum = d.stream().mapToDouble(di -> di).sum();
            int j = 0;
            for (String key : temp.keySet()) {
                r.put(acm, key);
                acm += d.get(j++) / sum;
            }
            ci = r.floorEntry(rand.nextDouble()).getValue();
        }
        
        //keyを数字に変更
        Map<Integer, List<Double>> cf = new HashMap<>();
        int i = 0;
        for(String key : c.keySet())
            cf.put(i++, c.get(key));
            
        return cf;
    }
    
    private Boolean changeCluster(Map<Integer, List<Double>> c){
        Boolean change = false;
        Map before = cluster;
        
        for(String key : s.keySet()){
            List<Double> v1 = s.get(key);
            
            Integer minKey = -1;
            Double min = Double.MAX_VALUE;
            for(Integer i : c.keySet()){
                Double d = distance(v1, c.get(i));
                if(d < min){
                    minKey = i;
                    min = d;
                }
            }
            
            if(cluster.get(key) != null){
                if(!cluster.get(key).equals(minKey))
                    change = true;
            } else
                change = true;
            cluster.put(key, minKey);
        }
        
        return change;
    }
    
    private Map<Integer, List<Double>> calcMedian(Map<Integer, List<Double>> c){
        Map medc = new HashMap();
        for(Integer id : c.keySet()){
            //1Clusterを抽出
            List<String> cg = cluster.entrySet().stream()
                                        .filter(e -> e.getValue().equals(id)).map(e -> e.getKey())
                                        .collect(Collectors.toList());
            //重心点を計算
            List<Double> med = IntStream.range(0, c.get(id).size())
                                        .mapToDouble(i -> cg.stream().mapToDouble(key -> s.get(key).get(i)).average().getAsDouble())
                                        .boxed()
                                        .collect(Collectors.toList());
            //重心点を更新
            medc.put(id, med);
        }
        
        return medc;
    }
    
    public Map<String, Integer> execute(){
        //初期中心点選択
        Map<Integer, List<Double>> c = initialize();
        
        //k-means
        int cnt = 0;
        while(changeCluster(c)){
            c = calcMedian(c);
            cnt++;
            System.out.println(cnt+":"+c);
        }
        
        return cluster;
    }
    
    public static void main(String[] args) {
        //Test Sample
        Random r = new Random();
        Map<String, List> test = new HashMap();
        for(int i=0; i < 100; i++){
            Double[] p = new Double[]{(double)r.nextInt(100), (double)r.nextInt(100)};
            test.put("t_"+i, Arrays.asList(p));
        }
        
        KMeansPP km = new KMeansPP();
        
        km.set(4, test);
        Map<String, Integer> result = km.execute();
        
        System.out.println("-----test print");
        for(String key : result.keySet()){
            System.out.println(key+","+test.get(key).get(0)+","+test.get(key).get(1)+","+result.get(key));
        }
    }
}
