/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 *
 * @author ZZ17390
 */
public class IntStreampararellTest {

    private static Random RAND = new Random();

    public static void main(String[] args) {
        int n = 1000000;
        for (int t = 0; t < n; t++) {
            List<String> p1 = new ArrayList();
            List<String> p2 = new ArrayList();
            
            IntStream.range(0, 10).forEach(i -> {
                p1.add(String.valueOf(RAND.nextInt(2)));
                p2.add(String.valueOf(RAND.nextInt(2)));
            });

            //System.out.println("p1:"+p1);
            //System.out.println("p2:"+p2);
            
            List c0 = crossover(p1, p2);
            //System.out.println("c0:"+c0);

            List m0 = mutable(c0);
            //System.out.println("MU:"+m0);
        }
        
        System.out.println("pb:"+(cnt/(n*10)));
    }

    //交叉 (一様交叉)
    public static List<String> crossover(List<String> p1, List<String> p2) {
        List<String> child = new ArrayList(p1);
        List<Integer> test = new CopyOnWriteArrayList();

        IntStream.range(0, p1.size()).parallel()
            .filter(i -> RAND.nextBoolean())
            .forEach(i -> {
                child.set(i, p2.get(i));
                //test.add(i);
            });

        //test.sort(Comparator.naturalOrder());
        //System.out.println(test);

        //検証
        /*for (int t : test) {
            if (!child.get(t).equals(p2.get(t))) {
                System.out.println("×");
            }
        }*/

        return child;
    }

    //突然変異
    static Double cnt = 0d;

    public static List<String> mutable(List<String> p) {
        List<String> child = p.parallelStream().map(g -> RAND.nextDouble() >= 0.1?g:inverse(g)).collect(Collectors.toList());
        //List<String> child = p.parallelStream().map(g -> RAND.nextDouble() >= 0.1 ? g : String.valueOf((Integer.valueOf(g) + 1) % 2)).collect(Collectors.toList());

        //検証
        cnt += IntStream.range(0, p.size()).filter(i -> !p.get(i).equals(child.get(i))).count();

        return child;
    }

    public static String inverse(String o) {
        if (o.equals("1")) {
            return "0";
        } else {
            return "1";
        }
    }
}
