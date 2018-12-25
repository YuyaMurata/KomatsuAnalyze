/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.analize;

import file.CSVFileReadWrite;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import obj.LoadSyaryoObject;
import obj.SyaryoObject;
import rmi.SyaryoObjectClient;

/**
 *
 * @author ZZ17390
 */
public class CreateMatrix {

    private static SyaryoObjectClient CLIENT = SyaryoObjectClient.getInstance();
    private static LoadSyaryoObject LOADER;

    private static String KISY = "PC200";

    public static void main(String[] args) {
        CLIENT.setLoadFile(KISY + "_sv_form");
        LOADER = CLIENT.getLoader();

        String[][] mx = customerMatrix();

        //CSV
        try (PrintWriter pw = CSVFileReadWrite.writer(KISY + "_customer_matrix.csv")) {
            for (String[] row : mx) {
                pw.println(String.join(",", row));
            }
        }
    }

    private static String[][] customerMatrix() {
        Map<String, SyaryoObject> map = LOADER.getSyaryoMap();
        List<String> sids = map.keySet().stream().collect(Collectors.toList());
        List<String> cids = map.values().stream()
            .filter(s -> s.get("顧客") != null)
            .flatMap(s -> s.get("顧客").values().stream())
            .map(c -> c.get(LOADER.index("顧客", "顧客CD")).toString())
            .distinct().collect(Collectors.toList());

        String[][] mx = new String[map.size() + 1][cids.size() + 1];
        
        //initialize
        IntStream.range(0, sids.size()+1).forEach(i -> Arrays.fill(mx[i], ""));
        
        //info
        System.out.println("車両数:"+sids.size());
        System.out.println("顧客数:"+cids.size());
        
        //header
        IntStream.range(1, sids.size()+1).forEach(i -> mx[i][0] = sids.get(i-1));
        IntStream.range(1, cids.size()+1).forEach(i -> mx[0][i] = cids.get(i-1));

        map.values().stream().forEach(s -> {
            if (s.get("顧客") != null) {
                List<String> scid = s.get("顧客").values().stream()
                    .map(c -> c.get(LOADER.index("顧客", "顧客CD")).toString())
                    .distinct().collect(Collectors.toList());

                for (String cid : scid) {
                    mx[sids.indexOf(s.name) + 1][cids.indexOf(cid) + 1] = "1";
                }
            }
        });

        return mx;
    }
}
