/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

/**
 *
 * @author ZZ17390
 */
public class HammingTest {
    public static void main(String[] args) {
        List<Integer> g = Arrays.asList(new Integer[]{1,0,0,0,1});
        List<Integer> g2 = Arrays.asList(new Integer[]{0,1,1,1,0});
        List<List<Integer>> gall = new ArrayList<>();
        
        gall.add(g);
        gall.add(g2);
        
        System.out.println(
            gall.stream().mapToDouble(gi -> 
                IntStream.range(0, gi.size()).filter(i -> !Objects.equals(g.get(i), gi.get(i))).count()/10
            ).average().getAsDouble()
        );
    }
}
