/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analyze;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;
import jsc.contingencytables.ContingencyTable2x2;
import jsc.contingencytables.FishersExactTest;

/**
 *
 * @author ZZ17390
 */
public class SFET {
    private Double[] quartile(Map<Integer, Double> divTimeData, int w){
        Double[] output = new Double[3];
        Double[] y = divTimeData.values().toArray(new Double[divTimeData.size()]);
        Arrays.sort(y);
        
        if(w % 4 == 0){
            output[0] = y[w/4-1];
            output[2] = y[(3*w)/4-1];
        }else{
            output[0] = y[w/4-1]/2 + y[w/4]/2;
            output[2] = y[(3*w)/4-1]/2 + y[(3*w)/4]/2;
        }
        if(w % 2 == 0){
            output[1] = y[w/2-1]/2 + y[w/2]/2;
        }else{
            output[1] = y[(w-1)/2];
        }
        
        return output;
    }
    
    private int[][] count(Map<Integer, Double> divTimeData,int th, Double xth){
        int[][] output = new int[2][2];
        for(Integer t : divTimeData.keySet()){
            if(t < th && divTimeData.get(t) >= xth) // a
                output[0][0]++;
            else if(t >= th && divTimeData.get(t) >= xth) // b
                output[1][0]++;
            else if(t < th && divTimeData.get(t) < xth) // c
                output[0][1]++;
            else // d
                output[1][1]++;
        }
        return output;
    }
    
    private BigInteger factorial(BigInteger n){
        BigInteger factorial = BigInteger.ONE;
        if(n.equals(BigInteger.ZERO))
            return factorial;
        
        for(int i=1; i <= n.intValue(); i++)
            factorial = factorial.multiply(BigInteger.valueOf(i));
        
        return factorial;
    }
    
    public Double fishersExactTest(int[][] contTable){
        int col1 = contTable[0][0] + contTable[1][0];
        int col2 = contTable[0][1] + contTable[1][1];
        int row1 = contTable[0][0] + contTable[0][1];
        int row2 = contTable[1][0] + contTable[1][1];
        
        return 0d;
    }
    
    TreeMap<Integer, Double> map = new TreeMap<>();
    public SFET(TreeMap<Integer, Double> timeData, int w, double alpha){
        for(int t=0; t < (timeData.size() - w); t++){
            int tr = t + w - 1;
            for(int th = t; t < tr; th++){
                Double[] xth = quartile(timeData.subMap(t, tr), w);
                for(int i=0; i < 3; i++){
                    int[][] contTable = count(timeData.subMap(t, tr), th, xth[i]);
                    //double p = new Combinations(i, i)
                    System.out.println("a="+contTable[0][0]+" b="+contTable[1][0]+" c="+contTable[0][1]+" d="+contTable[1][1]);
                    FishersExactTest f = new FishersExactTest(new ContingencyTable2x2(contTable));
                    Integer s = contTable[0][0]*contTable[1][0] - contTable[0][1]*contTable[1][1];
                    if(f.getSP() <= alpha){
                        System.out.println("t_th=,"+th+" ,s="+s+" ,P=,"+f.getSP());
                        map.put(th, f.getSP());
                    }
                }
            }
        }
    }
    
    public Double getTSP(Integer t){
        if(map.get(t) != null)
            return map.get(t);
        else
            return 1d; 
    }
}
