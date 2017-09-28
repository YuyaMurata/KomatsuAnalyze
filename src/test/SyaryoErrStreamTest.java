/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 *
 * @author ZZ17390
 */
public class SyaryoErrStreamTest {

    public static void main(String[] args) throws IOException {
        String f = "車両テンプレート\\syaryo_history_template_eqpkeireki_error.csv";
        long n = Files.lines(Paths.get(f)).count();
        long s = Files.lines(Paths.get(f)).map(str -> str.split(",")[1]).distinct().count();
        long no = Files.lines(Paths.get(f)).map(str -> str.split(",")[1]).filter(str -> str=="").count();
        
        Map map = new HashMap();
        Files.lines(Paths.get(f)).map(str -> str.split(",")[1]).forEach(str ->{
            map.put(str, "1");
        });
        
        System.out.println("車両(N/A)：" + n + "件 (" + s + ", " + no + ")");
        System.out.println(map.size());
    }
}
