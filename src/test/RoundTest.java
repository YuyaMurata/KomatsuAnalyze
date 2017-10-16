/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ZZ17390
 */
public class RoundTest {
    public static void main(String[] args) {
        Double min = 42d;
        Double max = 3789d;
        Double k = 20d;
        
        Double cell = (max-min)/k;
        Double base = Math.pow(10, Math.floor(Math.log10(cell)));
        System.out.println(base);
        Double unit;
        if(cell <= 1.4*base) {
            unit = base;
        } else if(cell < 2.8*base) {
            unit = 2 * base;
        } else if(cell < 7*base) {
            unit = 5 * base;
        } else {
            unit = 10 * base;
        }
        
        Double min_unit = Math.floor(min / unit);
        Double max_unit = Math.ceil(max / unit);
        
        min = min_unit * unit;
        max = max_unit * unit;
        Double ndiv = max_unit - min_unit;
        
        List<Integer> seq = new ArrayList();
        for(int i=0; i < ndiv+1; i++){
            seq.add((int)(i*unit));
        }
        
        System.out.println("min="+min+" max="+max+" n="+(ndiv+1));
        System.out.println(seq);
    }
}
