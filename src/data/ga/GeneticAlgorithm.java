/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.ga;

import data.analize.EvaluateCorrelation;
import file.UserDefinedFile;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.TreeMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.hive.druid.org.jboss.netty.util.internal.ConcurrentHashMap;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import param.KomatsuDataParameter;
import param.KomatsuUserParameter;

/**
 *
 * @author kaeru_yuya
 */
public class GeneticAlgorithm {

    private String tgname;
    private List<String> matname;

    private static Random RAND = new Random();
    private static Integer LOOP_N = 100_000;
    private Integer GEN_N = 100;
    private Double MUPB = 0.1;
    private Double CXPB = 1.0;
    private Double UNICX = 0.3;
    private int ELITE_G = (int) (0.1 * GEN_N);
    private Integer GEN_LEN;
    private double[][] fit;
    private double[] tg;
    private List<Population> POOL = new CopyOnWriteArrayList<>();
    public Population best;
    public Double divercity;
    private static Logger log;

    private static Map evalPartsMap = UserDefinedFile.evalMatrix(KomatsuUserParameter.PC200_PARTS_EVAL_FILE);
    private static Map evalKMErrMap = UserDefinedFile.evalMatrix(KomatsuUserParameter.PC200_KMERR_EVAL_FILE);

    public static void main(String[] args) {
        //Eval Settings
        List<String> partsNameHeader = ((List<List<String>>) evalPartsMap.get("headers")).get(0);
        List<String> kmerrNameHeader = ((List<List<String>>) evalKMErrMap.get("headers")).get(0);
        System.out.println("PartsMatrix-" + partsNameHeader);
        
        for (int kmeIdx = 0; kmeIdx < kmerrNameHeader.size(); kmeIdx++) {
            GeneticAlgorithm ga = new GeneticAlgorithm();
            
            System.out.println("Tartget-" + kmerrNameHeader.get(kmeIdx));

            //Log Settings
            LocalDateTime d = LocalDateTime.now();
            DateTimeFormatter df1 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH_mm_ss_");
            System.setProperty("format", "Tartget-" + kmerrNameHeader.get(kmeIdx) + "_" + df1.format(d));
            log = Logger.getLogger(GeneticAlgorithm.class);
            DOMConfigurator.configure("xml/galog4j.xml");

            //log.info("-------------------------------  new Execution  -------------------------------");

            ga.settings(partsNameHeader, (double[][]) evalPartsMap.get("data"),
                kmerrNameHeader.get(kmeIdx), ((double[][]) evalKMErrMap.get("data"))[kmeIdx]);
            ga.initialize(ga.GEN_N);

            for (int i = 0; i < LOOP_N; i++) {
                ga.next();
                ga.logPrint();
                if (((i + 1) % 1000) == 0) {
                    System.out.println("Loop-" + (i + 1) + "," + ga.best.f + "," + ga.divercity);
                }
            }
        }
    }

    public void settings(List<String> matName, double[][] matrix, String tgName, double[] target) {
        this.tgname = tgName;
        this.matname = matName;

        this.GEN_LEN = matrix.length;
        this.fit = matrix;
        this.tg = target;
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
            double[] farray = new double[fit[0].length];
            Arrays.fill(farray, 0d);

            IntStream.range(0, GEN_LEN)
                .filter(i -> p.g.get(i) == 1)
                .forEach(i -> {
                    IntStream.range(0, farray.length).parallel()
                        .forEach(j -> farray[j] += fit[i][j]);
                });

            //相関 正値と負値をとるため
            p.f = EvaluateCorrelation.correlation(tg, farray);
            if (p.f.isNaN()) {
                p.f = 0d;
            }

            //test
            //p.f = IntStream.range(0, GEN_LEN).filter(i -> p.g.get(i) == 1).mapToDouble(i -> 1d).sum();
        });
    }

    public List<Population> selection() {
        //エリート選択
        List<Population> select = POOL.stream()
            .sorted(Comparator.comparing(Population::getFit).reversed())
            .limit(ELITE_G)
            .collect(Collectors.toList());
        POOL.removeAll(select);

        if (!select.isEmpty()) {
            this.best = select.get(0);
        } else {
            this.best = POOL.stream()
                .sorted(Comparator.comparing(Population::getFit).reversed())
                .limit(1).findFirst().get();
        }

        //ルーレット選択 (POOL size - Elite size)
        TreeMap<Double, Population> roullet = new TreeMap();
        Double acm = 0d;
        double sum = POOL.parallelStream().mapToDouble(p -> p.getFit()).sum();
        for (Population p : POOL) {
            roullet.put(acm, p);
            acm += p.getFit() / sum;
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
        child.g.set(r, (p.g.get(r) + 1) % 2);

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
            nextgene.set(i, crossover(nextgene.get(i), nextgene.get((i + 1) % GEN_N)));

            //突然変異
            nextgene.set(i, mutable(nextgene.get(i)));
        }

        //次世代の登録
        POOL = new CopyOnWriteArrayList<>();
        nextgene.stream().forEach(POOL::add);

        calcDivercity();
        //testPrint();
    }

    private void calcDivercity() {
        Population sample = RAND.ints(0, POOL.size()).distinct()
            .limit(1)
            .mapToObj(POOL::get).findFirst().get();

        //Test
        divercity = POOL.stream()
            .mapToDouble(p -> IntStream.range(0, p.g.size())
            .filter(i -> p.g.get(i) != sample.g.get(i))
            .count())
            .average().getAsDouble();
    }

    private void logPrint() {
        log.info("Div.," + divercity + ",Best," + best.f + "," + "t=" + tgname + "," + best.g);
        //System.out.println("Best,"+best.f+","+"t="+tgname+","+best.g);
    }

    private void testPrint() {
        System.out.println("------  N=" + cnt + "  ------");
        System.out.println(cnt + "," + POOL.stream().map(p -> p.f.toString()).collect(Collectors.joining(",")));
    }
}
