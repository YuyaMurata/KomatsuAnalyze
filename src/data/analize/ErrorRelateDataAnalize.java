/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.analize;

import file.CSVFileReadWrite;
import index.SyaryoObjectElementsIndex;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import obj.LoadSyaryoObject;
import obj.SyaryoObject4;

/**
 *
 * @author ZZ17390
 */
public class ErrorRelateDataAnalize {

    static String KISY = "PC200";

    static String[] ERR_CODE = new String[]{"KOMTRAX_ERROR|ERROR_CODE|AA10NX"};
    static String[] CODE = new String[]{"部品|HNBN|600-185-3100", "部品|HNBN|600-185-4100"};
    private static Map<String, List> dataIndex = SyaryoObjectElementsIndex.getInstance().getIndex();

    public static void main(String[] args) {
        Map<String, SyaryoObject4> syaryoMap = LoadSyaryoObject.load(KISY + "_km_form.bz2");

        Map<String, List<String>> relate = syaryoMap.keySet().stream().collect(Collectors.toMap(e -> e, e -> new ArrayList<>()));

        //エラー情報の取得
        errCounts(syaryoMap, relate);

        //コード情報の取得
        counts(syaryoMap, relate);

        //出力
        try (PrintWriter csv = CSVFileReadWrite.writer(KISY + "_relate_err_code.csv")) {
            //header
            csv.println("sid," + String.join(",", ERR_CODE) + "," + String.join(",", CODE));

            //data
            relate.entrySet().stream()
                .map(e -> e.getKey() + "," + String.join(",", e.getValue()))
                .forEach(csv::println);
        }
    }

    //エラーカウント
    private static void errCounts(Map<String, SyaryoObject4> syaryoMap, Map<String, List<String>> relate) {
        List<String> field = Arrays.asList(ERR_CODE);
        System.out.println(field);

        syaryoMap.values().stream().forEach(s -> {
            s.startHighPerformaceAccess();

            for (String keys : field) {
                String[] sarr = keys.split("\\|");
                String k = sarr[0];
                int idx = dataIndex.get(k).indexOf(sarr[1]);
                String c = sarr[2];

                if (s.get(k) != null) {
                    long cnt = s.get(k).values().stream().filter(v -> v.get(idx).toString().equals(c)).count();
                    relate.get(s.name).add(String.valueOf(cnt));
                } else {
                    relate.get(s.name).add(String.valueOf(0));
                }
            }

            s.stopHighPerformaceAccess();
        });

        System.out.println("エラーカウント完了");
    }

    //コードカウント
    private static void counts(Map<String, SyaryoObject4> syaryoMap, Map<String, List<String>> relate) {
        List<String> field = Arrays.asList(CODE);

        syaryoMap.values().stream().forEach(s -> {
            s.startHighPerformaceAccess();

            for (String keys : field) {
                String[] sarr = keys.split("\\|");
                String k = sarr[0];
                int idx = dataIndex.get(k).indexOf(sarr[1]);
                String c = sarr[2];

                if (s.get(k) != null) {
                    long cnt = s.get(k).values().stream().filter(v -> v.get(idx).toString().equals(c)).count();
                    relate.get(s.name).add(String.valueOf(cnt));
                } else {
                    relate.get(s.name).add(String.valueOf(0));
                }
            }

            s.stopHighPerformaceAccess();
        });

        System.out.println("コードカウント完了");
    }
}
