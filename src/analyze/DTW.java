/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analyze;

import java.util.Arrays;
import java.util.List;

/**
 *
 * @author zz17390
 */
public class DTW {
    public static Integer distance(Integer[] s, Integer[] t){
        Integer[][] dtw = new Integer[s.length][t.length];
        
        for(int i=1; i < s.length; i++)
            dtw[i][0] = Integer.MAX_VALUE;
        for(int i=1; i < t.length; i++)
            dtw[0][i] = Integer.MAX_VALUE;
        dtw[0][0] = 0;
        
        for(int i=1; i < s.length; i++){
            for(int j=1; j < t.length; j++){
                int cost = Math.abs(s[i] - t[j]);
                dtw[i][j] = cost + Math.min(Math.min(dtw[i-1][j], dtw[i][j-1]), dtw[i-1][j-1]);
            }
        }
        
        return dtw[s.length-1][t.length-1];
    }
    
    //test
    public static void main(String[] args){
        Integer[] s = new Integer[]{199, 199, 184, 162, 146, 166, 171, 180, 193, 181, 183, 218, 230, 242, 209};
        Integer[] t = new Integer[]{183, 218, 230, 242, 209, 191, 172, 194, 196, 196, 236, 235, 229, 243, 264};
        
        System.out.println(DTW.distance(s, t));
    }
}
