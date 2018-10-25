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
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.hive.druid.org.jboss.netty.util.internal.ConcurrentHashMap;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import param.KomatsuUserParameter;

/**
 *
 * @author kaeru_yuya
 */
public class GeneticAlgorithm {

    private String tgname;
    private List<String> matname;

    private static Random RAND = new Random();
    private static Integer LOOP_N = 10_000;
    private Integer GEN_N = 100;
    private Double MUPB = 0.1;
    private Double CXPB = 1.0;
    private Double UNICX = 0.5;
    private int ELITE_G = (int) (0.1 * GEN_N);
    private int SELECT_G = (int) (0.1 * GEN_N);
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
            //int kmeIdx = 0;

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
                //ga.testPrint();
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
        POOL.sort(Comparator.comparing(Population::getFit).reversed());
        List<Population> select = POOL.subList(0, ELITE_G);
        POOL = new CopyOnWriteArrayList<>(POOL.subList(ELITE_G, POOL.size()));

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

        while (select.size() < ELITE_G + SELECT_G) {
            select.add(roullet.floorEntry(RAND.nextDouble()).getValue());
        }

        return select;
    }

    //交叉
    public List<Population> crossover(Population p1, Population p2) {
        List<Population> childlen = new ArrayList<>();
        childlen.add(new Population(p1.g, p1.f));
        childlen.add(new Population(p1.g, p1.f));

        if (RAND.nextDouble() >= CXPB) {
            return childlen;
        }

        //一様交叉
        /*IntStream.range(0, p1.g.size()).parallel()
            .filter(i -> RAND.nextDouble() >= UNICX)
            .forEach(i -> {

                childlen.get(0).g.set(i, p2.g.get(i));
                childlen.get(1).g.set(i, p1.g.get(i));
            });
        */
        //2点交叉
        List<Integer> point = IntStream.range(0, 2).map(i -> RAND.nextInt(GEN_LEN)).boxed().sorted().collect(Collectors.toList());
        for (int i = point.get(0); i < point.get(1); i++) {
            childlen.get(0).g.set(i, p2.g.get(i));
            childlen.get(1).g.set(i, p1.g.get(i));
        }

        return childlen;
    }

    //突然変異
    public Population mutable(Population p) {
        Population child = new Population(p.g, p.f);

        if (RAND.nextDouble() >= MUPB) {
            return child;
        }

        //int r = RAND.nextInt(p.g.size());
        IntStream.range(0, p.g.size()).parallel()
            .filter(i -> RAND.nextDouble() < MUPB)
            .forEach(i -> child.g.set(i, (child.g.get(i) + 1) % 2));
        //child.g.set(r, (p.g.get(r) + 1) % 2);

        return child;
    }

    //世代交代
    public List<Population> nextGen(List<Population> nextgene) {
        List<Population> n = new CopyOnWriteArrayList<>();
        n.addAll(nextgene);
        n.addAll(POOL.subList(0, GEN_N - nextgene.size()));

        return n;
    }

    public int cnt = 0;

    public void next() {
        //ループ回数
        cnt++;

        //評価
        fitness();

        //選択
        List<Population> selectgene = selection();

        List<Population> nextgene = new CopyOnWriteArrayList<>();
        for (int i = 0; i < selectgene.size() - 1; i++) {
            //交叉
            List<Population> childlen = crossover(selectgene.get(i), selectgene.get(i + 1));

            //突然変異
            nextgene.addAll(childlen.parallelStream().map(g -> mutable(g)).collect(Collectors.toList()));
        }

        //次世代の登録
        nextgene.addAll(selectgene);
        POOL = nextGen(nextgene);

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
        //System.out.println(cnt + "," + POOL.stream().map(p -> p.f.toString()).collect(Collectors.joining(",")));
        POOL.stream().forEach(System.out::println);
    }
}
