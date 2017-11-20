/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import com.google.common.collect.Iterators;
import java.util.Arrays;
import java.util.Iterator;
import org.apache.commons.math3.util.Combinations;

/**
 *
 * @author ZZ17390
 */
public class CombinationTest {
    public static void main(String[] args) {
        Combinations comb = new Combinations(4, 2);
        System.out.println(comb.getN()+"C"+comb.getK()+"="+comb);
        Iterator it = comb.iterator();
        System.out.println(Iterators.size(it));
    }
}
