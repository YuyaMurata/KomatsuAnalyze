/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import program.r.R;

/**
 *
 * @author kaeru_yuya
 */
public class RFunctionTest {
    public static void main(String[] args) {
        R r = R.getInstance();
        
        List s = new ArrayList();
        List t = new ArrayList();
        for(int i=0; i < 100; i++){
            if(i % 10 == 0)
                s.add(i * 100);
            else
                s.add(i);
            t.add(i);
        }
        
        Map results = r.detectOuters(t, s);
        System.out.println(results);
    }
}
