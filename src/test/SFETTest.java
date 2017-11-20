/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import analyze.SFET;
import file.CSVFileReadWrite;
import java.io.PrintWriter;
import java.util.Random;
import java.util.TreeMap;
import org.apache.commons.math3.random.RandomDataGenerator;

/**
 *
 * @author ZZ17390
 */
public class SFETTest {
    public static void main(String[] args) {
        TreeMap<Integer, Double> x = new TreeMap<>();
        RandomDataGenerator rand = new RandomDataGenerator();
        x.put(0, 1d);
        x.put(1, 1d);
        //Random rand = new Random();
        for(int i=2; i < 10000; i++){
            Double xt = 0d;
            /*if(i%1000 == 0){
                xt += i/100;
            }
            Double c = 0.6d*x.get(i-1);
            Double d = - 0.5d*x.get(i-2);
            Double r = rand.nextGaussian(0, 1d);
            xt +=  c + d + r;
            System.out.println("xt="+xt+" ,c="+c+" ,d="+d+" ,r="+r);
            */
            xt = (int)(i / 1000)*5 + rand.nextGaussian(0, 1d);
            x.put(i, xt);
        }
        
        PrintWriter pw = CSVFileReadWrite.writer("test_SFET.csv");
        SFET sfet = new SFET(x, 98, 1d);
        for(Integer t : x.keySet()){
            pw.println(t+","+x.get(t)+","+sfet.getTSP(t));
        } 
        pw.close();
    }
}
