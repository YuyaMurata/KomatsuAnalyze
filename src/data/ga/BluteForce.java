/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.ga;

import data.analize.EvaluateCorrelation;
import file.CSVFileReadWrite;
import file.UserDefinedFile;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import param.KomatsuUserParameter;

/**
 *
 * @author ZZ17390
 */
public class BluteForce {

    private static Map evalPartsMap = UserDefinedFile.evalMatrix(KomatsuUserParameter.PC200_PARTS_EVAL_FILE);
    private static Map evalKMErrMap = UserDefinedFile.evalMatrix(KomatsuUserParameter.PC200_KMERR_EVAL_FILE);
    private static double[][] fit;
    private static double[] tg;
    private static Population best;
    
    public static void main(String[] args) {
        //Eval Settings
        List<String> partsNameHeader = ((List<List<String>>) evalPartsMap.get("headers")).get(0);
        List<String> kmerrNameHeader = ((List<List<String>>) evalKMErrMap.get("headers")).get(0);
        
        int kmeIdx = 10;
        System.out.println("Tartget-" + kmerrNameHeader.get(kmeIdx));
        System.out.println("PartsMatrix-" + partsNameHeader);
        
        fit = (double[][]) evalPartsMap.get("data");
        tg = ((double[][]) evalKMErrMap.get("data"))[kmeIdx];
        matsize = fit.length;
        
        best = new Population(bitInc(0), 0d);
        
        try (PrintWriter pw = CSVFileReadWrite.writer("bluteforce_test.csv")) {
            pw.println("Tartget-" + kmerrNameHeader.get(kmeIdx));
            long cnt = 0;
            for (;;) {
                cnt++;
                Population p = fitness(bitInc(cnt));
                
                if(p.getFit() > best.getFit()){
                    best = p;
                    pw.println(p.f+","+p.g);
                }
                
                if(p.g.stream().mapToInt(l -> l).sum() == matsize)
                    break;
                
                if(cnt % 10000 == 0){
                    System.out.println(cnt+":"+best.f);
                }
            }
        }
    }

    static int matsize;

    private static List<Integer> bitInc(long d) {
        List<Integer> bit = new ArrayList<>();
        long inc = d;
        while (inc != 0) {
            int m = (int) (inc % 2);
            inc = inc / 2;
            bit.add(m);
        }

        while (bit.size() < matsize) {
            bit.add(0);
        }
        Collections.reverse(bit);
        return bit;
    }

    //適応値の計算
    public static Population fitness(List<Integer> list) {
        Population p = new Population(list, 0d);
        
        double[] farray = new double[fit[0].length];
        Arrays.fill(farray, 0d);

        IntStream.range(0, list.size())
            .filter(i -> list.get(i) == 1)
            .forEach(i -> {
                IntStream.range(0, farray.length).parallel()
                    .forEach(j -> farray[j] += fit[i][j]);
            });

        //相関 正値と負値をとるため
        p.f = EvaluateCorrelation.correlation(tg, farray);
        if (p.f.isNaN()) {
            p.f = 0d;
        }
        
        return p;
    }
}
