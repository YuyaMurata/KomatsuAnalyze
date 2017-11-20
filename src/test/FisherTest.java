/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.math.BigDecimal;
import java.math.BigInteger;
import jsc.contingencytables.ContingencyTable2x2;
import jsc.contingencytables.FishersExactTest;

/**
 *
 * @author ZZ17390
 */
public class FisherTest {
    public static void main(String[] args) {
        int a = 8;
        int b = 12;
        int c = 0;
        int d = 3;
        ContingencyTable2x2 cont = new ContingencyTable2x2(a, b, c, d);
        FishersExactTest f = new FishersExactTest(cont);
        System.out.println(f.getSP());
    }
}
