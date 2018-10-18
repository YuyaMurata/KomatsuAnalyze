/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.ga;

import data.analize.EvaluateCorrelation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.TreeMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.hive.druid.org.jboss.netty.util.internal.ConcurrentHashMap;

/**
 *
 * @author kaeru_yuya
 */
public class GeneticAlgorithm {

    private static Random RAND = new Random();
    private static Integer LOOP_N = 1000;
    private Integer GEN_N = 20;
    private Double MUPB = 0.1;
    private Double CXPB = 1.0;
    private Double UNICX = 0.3;
    private int ELITE_G = (int) (0.1 * GEN_N);
    private static Integer GEN_LEN = 10;
    private double[][] fit;
    private double[] tg;
    private List<Population> POOL = new CopyOnWriteArrayList<>();
    public static Double best;

    public static void main(String[] args) {
        double totalavg = 0;
        for (int n = 0; n < 1000; n++) {
            GeneticAlgorithm ga = new GeneticAlgorithm();
            ga.settings(new double[GEN_LEN][10], new double[10]);
            ga.initialize(ga.GEN_N);
            best = 0d;

            for (int i = 0; i < LOOP_N; i++) {
                ga.next();
                if (best == 10) {
                    break;
                }
            }
            
            totalavg += ga.cnt;
        }
        
        System.out.println(totalavg / 1000);
    }

    public void settings(double[][] matrix, double[] target) {
        GEN_LEN = matrix.length;
        fit = matrix;
        tg = target;

        //test
        for (int i = 0; i < GEN_LEN; i++) {
            for (int j = 0; j < 10; j++) {
                fit[i][j] = i + j;
            }
        }
    }

    //初期個体の生成
    public void initialize(int n) {
        Map<List<Integer>, Double> initPOOL = new ConcurrentHashMap();

        while (initPOOL.size() < n) {
            List<Integer> gene = new ArrayList();
            IntStream.range(0, GEN_LEN).map(i -> RAND.nextInt(2)).forEach(gene::add);
            initPOOL.put(gene, -1d);
        }

        //Test
        initPOOL.entrySet().stream()
            .map(g -> new Population(g.getKey(), g.getValue())).forEach(POOL::add);
    }

    //適応値の計算
    public void fitness() {
        POOL.parallelStream().forEach(p -> {
            /*
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
             */
            //test

            p.f = IntStream.range(0, GEN_LEN).filter(i -> p.g.get(i) == 1).mapToDouble(i -> 1d).sum();
        });
    }

    public List<Population> selection() {
        //エリート選択
        List<Population> select = POOL.stream()
            .sorted(Comparator.comparing(Population::getFit).reversed())
            .limit(ELITE_G)
            .collect(Collectors.toList());
        POOL.removeAll(select);
        
        if(!select.isEmpty())
            best = select.get(0).f;
        else
            best = POOL.stream().mapToDouble(p -> p.f).max().getAsDouble();

        //ルーレット選択 (POOL size - Elite size)
        TreeMap<Double, Population> roullet = new TreeMap();
        Double acm = 0d;
        double sum = POOL.parallelStream().mapToDouble(p -> p.f).sum();
        for (Population p : POOL) {
            roullet.put(acm, p);
            acm += p.f / sum;
        }

        while (select.size() < GEN_N) {
            select.add(roullet.floorEntry(RAND.nextDouble()).getValue());
        }

        return select;
    }

    //交叉 (一様交叉)
    public Population crossover(Population p1, Population p2) {
        Population child = new Population(p1.g, p1.f);

        if (RAND.nextDouble() >= CXPB) {
            return child;
        }

        IntStream.range(0, p1.g.size()).parallel()
            .filter(i -> RAND.nextDouble() >= UNICX)
            .forEach(i -> {
                child.g.set(i, p2.g.get(i));
            });

        return child;
    }

    //突然変異
    public Population mutable(Population p) {
        Population child = new Population(p.g, p.f);

        if (RAND.nextDouble() >= MUPB) {
            return child;
        }
        
        int r = RAND.nextInt(p.g.size());
        /*IntStream.range(0, p.g.size()).parallel()
            .filter(i -> RAND.nextDouble() < MUPB)
            .forEach(i -> child.g.set(i, (child.g.get(i) + 1) % 2));*/
        child.g.set(r, (p.g.get(r)+1)%2);

        return child;
    }

    public int cnt = 0;

    public void next() {
        //ループ回数
        cnt++;

        //評価
        fitness();

        //選択
        List<Population> nextgene = selection();

        for (int i = ELITE_G; i < nextgene.size(); i++) {
            //交叉
            nextgene.set(i, crossover(nextgene.get(i), nextgene.get((i+1) % GEN_N)));

            //突然変異
            nextgene.set(i, mutable(nextgene.get(i)));     
        }
        //次世代の登録
        POOL = new CopyOnWriteArrayList<>();
        nextgene.stream().forEach(POOL::add);

        //testPrint();
    }

    private void testPrint() {
        System.out.println("------  N="+cnt+"  ------");
        System.out.println(cnt + "," + POOL.stream().map(p -> p.f.toString()).collect(Collectors.joining(",")));
    }
}
