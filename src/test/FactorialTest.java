/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.math.BigInteger;

/**
 *
 * @author ZZ17390
 */
public class FactorialTest {
    public static void main(String[] args) {
        int n = 9;
        BigInteger f = new FactorialTest().factorial(BigInteger.valueOf(n));
        System.out.println(f);
    }
    
    private BigInteger factorial(BigInteger n){
        BigInteger factorial = BigInteger.ONE;
        if(n.equals(BigInteger.ZERO))
            return factorial;
        
        for(int i=1; i <= n.intValue(); i++)
            factorial = factorial.multiply(BigInteger.valueOf(i));
        
        return factorial;
    }
}
