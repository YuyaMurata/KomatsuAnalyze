/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 *
 * @author ZZ17390
 */
public class StreamFilterTest {
    public static void main(String[] args) {
        List<Integer> d = IntStream.range(0, 10).boxed().collect(Collectors.toList());
        
        System.out.println("i < 5");
        d.stream().filter(i -> i < 5).forEach(System.out::println);
        
        System.out.println("i > 5");
        d.stream().filter(i -> i > 5).forEach(System.out::println);
        
        System.out.println("count i > 10 : "+d.stream().filter(i -> i > 10).count());
        System.out.println("count i < 10 : "+d.stream().filter(i -> i < 10).count());
    }
}
