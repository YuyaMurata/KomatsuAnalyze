/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analyze;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author zz17390
 */
public class MovingAverage {
    public static List<Integer> avg(List series, Integer interval){
        List<Integer> ma = new ArrayList();
        
        List temp_series = new ArrayList();
        for(int i=0; i < interval/2+1; i++){
            temp_series.add(series.get(0));
            series.add(series.get(series.size()-1));
        }
        temp_series.addAll(series);
        System.out.println(temp_series);
        series = temp_series;
        
        for(int i=0; i < series.size()-interval; i++){
            int n = 0;
            for(int j=i; j < i+interval; j++){
                n += Integer.valueOf(series.get(j).toString());
            }
            ma.add(n / interval);
        }
        System.out.println(ma);
        
        return ma;
    }
}
