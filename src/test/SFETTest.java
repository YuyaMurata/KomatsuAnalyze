/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import analyze.SFET;
import analyze.TimeSpread;
import file.CSVFileReadWrite;
import java.io.BufferedReader;
import java.io.IOException;

/**
 *
 * @author ZZ17390
 */
public class SFETTest {

    public static void main(String[] args) throws IOException {
        //CSV
        test2();
    }

    private static void test2() throws IOException {
        //TreeMap<Integer, Double> x = new TreeMap<>();
        BufferedReader br = CSVFileReadWrite.reader("None_WA470-7-10180_error.csv");
        
        //header
        br.readLine();
        TimeSpread x = new TimeSpread();
        String str;
        while ((str = br.readLine()) != null) {
            //x.put(Integer.valueOf(str.split(",")[0]), Double.valueOf(str.split(",")[1]));
            x.set(Integer.valueOf(str.split(",")[0]), Double.valueOf(str.split(",")[36]));
        }
        
        x.check();
        x.dump();
        SFET sfet = new SFET(x, 98, 0.05d);
    }
}
